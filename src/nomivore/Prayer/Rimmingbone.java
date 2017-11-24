package nomivore.Prayer;

import CustomAPI.ClientContext;
import CustomAPI.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.regex.Pattern;

@Script.Manifest(
        name = "RimmingBone", properties = "author=nomivore; topic=1340394; client=4;",
        description = "Gilded altar at Rimmington script")
public class Rimmingbone extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int altarID = 13197;
    private String npcName = "Phials";
    private int housePortalID = 4525;
    private int rimPortalID = 15478;
    private int boneID;
    private int notedBoneID;
    private int[] reqItems;
    private int productDone;
    private int level;
    private int startExp;
    private long startTime;
    private int skill = Constants.SKILLS_PRAYER;

    @Override
    public void messaged(MessageEvent messageEvent) {
        if (messageEvent.text().contains("The gods are very pleased")) {
            productDone++;
        }
        if (messageEvent.text().contains("That player is offline")) {
            ctx.controller.stop();
        }
    }

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(skill);
        notedBoneID = ctx.inventory.select().name(Pattern.compile("(.*bones.*)")).poll().id();
        boneID = notedBoneID-1;
        reqItems = new int[] {boneID, notedBoneID,995};
        level = ctx.skills.realLevel(skill);
    }

    @Override
    public void repaint(Graphics graphics) {
        Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int exp = ctx.skills.experience(skill) - startExp;
        int expHr = (int)(exp*3600000D/realRuntime(startTime));

        g.setColor(Color.WHITE);
        g.drawString(runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Done %d", productDone) , 10, 140);
        g.drawString(String.format("Level %d", level) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }

    @Override
    public void poll() {
        switch (getState()) {
            case TOALTAR:
                //enter house in build mode, then walk to bookcase location
                GameObject rimPortal = ctx.objects.select(10).id(rimPortalID).poll();
                if (rimPortal.valid()) {
                    APturnTo(rimPortal);
                    if (!rimPortal.inViewport()) {
                        ctx.movement.step(rimPortal);
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        APmoveRandom();
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (rimPortal.inViewport()) {
                        rimPortal.interact("Friend's house", "Portal");
                        Condition.sleep();
                        APmoveRandom();
                        ctx.input.send("{VK_ENTER}");
                        Condition.wait(() -> !ctx.objects.select(5).id(housePortalID).isEmpty());
                    }
                }
                break;
            case OFFERING:
                //check if altar, use bones on altar
                GameObject altar = ctx.objects.select(10).id(altarID).poll();
                if (altar.valid()) {
                    APturnTo(altar);
                    if (altar.inViewport()) {
                        Item bones = ctx.inventory.select().id(boneID).poll();
                        if (bones.interact("Use") && altar.interact("Use", "Altar")) {
                            APmoveOffScreen();
                            Condition.wait(() -> ctx.chat.canContinue() || ctx.inventory.select().id(boneID).isEmpty(), 10000, 6);
                            level = ctx.skills.realLevel(skill);
                        }
                    } else {
                        ctx.movement.stepWait(altar);
                        APmoveRandom();
                    }
                }
                break;
            case TOPHILES:
                //not enough normal planks, exit via portal then use noted planks on philes
                if (ctx.inventory.select().id(notedBoneID).isEmpty()) ctx.controller.stop();
                GameObject portal = ctx.objects.select(10).id(housePortalID).poll();
                if (portal.valid()) {
                    APturnTo(portal);
                    if (!portal.inViewport()) {
                        ctx.movement.step(portal);
                        APmoveRandom();
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (portal.inViewport()) {
                        portal.interact("Enter", "Portal");
                        Condition.wait(() -> !ctx.objects.select(5).id(rimPortalID).isEmpty());
                    }
                }
                Npc philly = ctx.npcs.select().name(npcName).poll();
                if (philly.valid()) {
                    APturnTo(philly);
                    if (!philly.inViewport() && philly.valid()) {
                        ctx.movement.step(philly);
                        APmoveRandom();
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (philly.inViewport() && philly.valid() && !ctx.inventory.select().id(notedBoneID).isEmpty()) {
                        ctx.inventory.deselectItem();
                        Item noted = ctx.inventory.select().id(notedBoneID).poll();
                        noted.interact("Use");
                        Condition.sleep();
                        philly.interact("Use", npcName);
                        Condition.wait(ctx.chat::chatting, 500, 5);
                        if (ctx.chat.chatting()) {
                            Condition.sleep();
                            APmoveRandom();
                            ctx.input.send("3");
                            Condition.wait(() -> !ctx.chat.chatting(), 500, 5);
                        }
                    }
                }
                break;
            case WAIT:
                break;
        }
    }

    private enum State {
        OFFERING,TOPHILES, TOALTAR,WAIT
    }

    private State getState() {
        //if the bookcase either complete or incomplete is nearby and have req items. will remove/build depending on which
        if (!ctx.objects.select(10).id(altarID).isEmpty() && hasReqs()) {
            return State.OFFERING;
        }
        //if do not have all req items, go to philes
        if (!hasReqs()) {
            return State.TOPHILES;
        }
        //if have req items but not near bookcase, go to buildmode
        if (ctx.objects.select(10).id(altarID).isEmpty() && hasReqs()) {
            return State.TOALTAR;
        }
        return State.WAIT;
    }

    private boolean hasReqs() {
        return ctx.inventory.hasAll(reqItems);
    }
}

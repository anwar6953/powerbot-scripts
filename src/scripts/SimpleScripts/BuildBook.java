package scripts.SimpleScripts;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;

//@Script.Manifest(
       //  name = "BuildBook", properties = "author=LOL; topic=1330081; client=4;",
      //description = "Makes fires at Varrock")
public class BuildBook extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int spaceID = 4521;
    private String spaceName = "Chair space";
    private String objName = "Chair";
    private String npcName = "Phials";
    private Component buildUI = ctx.widgets.component(458,0);
    private int housePortalID = 4525;
    private int rimPortalID = 15478;
    private int plankID = 960;
    private int notedPlankID = plankID+1;
    private int nailID = 1539;
    private int[] reqItems = new int[] {plankID,notedPlankID,8794,2347,995}; //nailID
    private int productDone;
    private int level;
    private int startExp;
    private long startTime;
    private int skill = Constants.SKILLS_CONSTRUCTION;
    private int expPerAction = 58;
    private State state = State.WAIT;

    @Override
    public void messaged(MessageEvent messageEvent) {

    }

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(skill);
    }

    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics graphics) {
        Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int exp = ctx.skills.experience(skill) - startExp;
        productDone = exp/expPerAction;
        int expHr = (int)(exp*3600000D/Utils.realRuntime(startTime));

        strings.clear();
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add(String.format("Done %d", productDone));
        strings.add(String.format("Level %d", level));
        strings.add(String.format("Exp %d/hr", expHr));
        strings.add("State " + state.name());
        Utils.simplePaint(g, strings.toArray(new String[strings.size()]));
    }

    @Override
    public void poll() {
        state = getState();
        switch (state) {
            case TOBUILD:
                //enter house in build mode, then walk to bookcase location
                GameObject rimPortal = ctx.objects.select(10).id(rimPortalID).poll();
                if (rimPortal.valid()) {
                    new Random();
                    if (Random.nextBoolean()) {
                        ctx.movement.stepWait(new Tile(2952, 3221));
                    } else {
                        ctx.movement.stepWait(new Tile(2951, 3221));
                    }
                    Utils.stepInteract(rimPortal,"Build mode");
                    Condition.wait(()->!ctx.objects.select(5).id(housePortalID).isEmpty());
                    break;
                }
                GameObject buildLoc = ctx.objects.select(10).name(spaceName,objName).poll();
                if (!buildLoc.inViewport() && buildLoc.valid()) {
                    ctx.movement.step(buildLoc);
                    Condition.wait(()->ctx.players.local().inMotion(),500,5);
                    Condition.wait(()->!ctx.players.local().inMotion());
                }
                break;
            case BUILDING:
                //check if bookcase made or not, then do action
                if (!ctx.objects.select(5).name(spaceName).isEmpty()) {
                    GameObject space = ctx.objects.select(5).name(spaceName).poll();
                    if (buildUI.visible()) {
                        ctx.input.send("1");
                        Condition.wait(()->!ctx.objects.select(5).name(objName).isEmpty(), 500, 30);
                    }
                    else if (space.interact("Build", spaceName)) {
                        Condition.wait(()->buildUI.visible(),1000,3);
                    }
                    break;
                }
                if (!ctx.objects.select(5).name(objName).isEmpty()) {
                    GameObject obj = ctx.objects.select(5).name(objName).poll();
                    if (obj.interact("Remove", objName)) {
                        Condition.wait(ctx.chat::chatting,500,5);
                        ctx.input.send("1");
                        Condition.wait(()->ctx.players.local().animation() != -1,500,5);
                        Condition.wait(()->ctx.players.local().animation() == -1);
                    }
                }
                break;
            case TOPHILES:
                //not enough normal planks, exit via portal then use noted planks on philes
                if (ctx.inventory.select().id(notedPlankID).isEmpty()) ctx.controller.stop();
                GameObject portal = ctx.objects.select(10).id(housePortalID).poll();
                if (portal.valid()) {
                    Utils.stepInteract(portal,"Enter");
                    Condition.wait(()->!ctx.objects.select(5).id(rimPortalID).isEmpty());
                    break;
                }
                Npc philly = ctx.npcs.select().name(npcName).poll();
                if (philly.valid()) {
                    ctx.movement.stepWait(philly);
                    ctx.inventory.deselectItem();
                    Item noted = ctx.inventory.select().id(notedPlankID).poll();
                    noted.interact("Use");
                    philly.interact("Use", npcName);
                    Condition.wait(ctx.chat::chatting,500,5);
                    if (ctx.chat.chatting()) {
                        ctx.input.send("3");
                        Condition.wait(()->!ctx.chat.chatting(),500,5);
                    }
                }
                break;
            case WAIT:
                break;
        }
    }

    private enum State {
        BUILDING,TOPHILES,TOBUILD,WAIT
    }

    private State getState() {
        //if the bookcase either complete or incomplete is nearby and have req ITEMS. will remove/build depending on which
        if (!ctx.objects.select(5).name(spaceName,objName).isEmpty()
                && hasReqs()
                && !ctx.objects.select(15).id(housePortalID).isEmpty()) {
            return State.BUILDING;
        }
        //if do not have all req ITEMS, go to philes
        if (!hasReqs()) {
            return State.TOPHILES;
        }
        //if have req ITEMS but not near bookcase, go to buildmode
        if (ctx.objects.select(5).name(spaceName,objName).isEmpty()
                && hasReqs()) {
            return State.TOBUILD;
        }
        return State.WAIT;
    }

    private boolean hasReqs() {
        return ctx.inventory.hasAll(reqItems)
                && ctx.inventory.select().id(plankID).count() >= 2
                && ctx.inventory.select().id(nailID).count(true) >= 2;
    }
}

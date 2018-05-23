package scripts.AIOThiever.Tasks;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Pickpocket extends Task<ClientContext> {
    public Pickpocket(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private String npcName = "";
    private int foodID = -1;
    private State state = State.WAIT;
    private Npc npc = ctx.npcs.nil();
    private int delay;
    private boolean ardyKnight = false;

    @Override
    public void initialise() {
        npcName = promptNearbyNpc();
        if (npcName.isEmpty()) ctx.controller.stop();
        if (npcName.equals("Knight of Ardougne")) ardyKnight = true;
        foodID = findFood();
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case WAIT:
                Condition.sleep(delay);
                delay = 0;
                break;
            case PICK:
                npc = (npc.valid() && !npc.equals(ctx.npcs.nil())) ? npc : ctx.npcs.select().name(npcName).action("Pickpocket").within(5).nearest().poll();
//                if (npc.tile().distanceTo(ctx.players.local()) > 5) ctx.controller.stop();
                Utils.stepInteract(npc,"Pickpocket");
                break;
            case HEAL:
                ctx.inventory.select().id(foodID).poll().click();
                Condition.wait(()->ctx.players.local().animation() != -1);
//                foodID = findFood();
                break;
            case BANK:
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept(995);
                    ctx.bank.withdrawUntil(foodID, 27);
                    if (ctx.bank.noneLeft(foodID)) ctx.controller.stop();
                    ctx.bank.closeBank();
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains("stunned")) {
            delay = Math.abs((int)(r.nextGaussian()*1000));
            log.info("Stunned! " + delay);
        }
    }

    private State getState() {
        if (ctx.players.local().animation() == 424 || delay != 0) {
            if (HPercent() < 90) return State.HEAL;
            return State.WAIT;
        }
        if (ctx.inventory.select().id(foodID).isEmpty()) {
            ctx.controller.stop();
            return State.WAIT;
        }
        if ((HP() < 7 || lostHP() > 10)  && !ctx.inventory.select().id(foodID).isEmpty()) {
            return State.HEAL;
        }
        return State.PICK;
    }

    private enum State {
        WAIT,PICK,HEAL,BANK
    }

    private String promptNearbyNpc() {
        ArrayList<Npc> allMobs = new ArrayList<>();
        ArrayList<String> mobList = new ArrayList<>();
        ctx.npcs.select().addTo(allMobs);
        for (Npc m : allMobs) {
            if (!mobList.contains(m.name())) mobList.add(m.name());
        }
        String[] mobs = mobList.toArray(new String[mobList.size()]);
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                mobs, mobs[0]);

        if (selectedValue == null) return "";
        else return selectedValue.toString();
    }

    private int findFood() {
        for (Item i : ctx.inventory.items()) {
            if (Arrays.asList(i.actions()).contains("Eat") || i.name().equals("Jug of wine")) {
                log.info("Found food " + i.name());
                return i.id();
            }
        }
        return -1;
    }

    @Override
    public String getStateName() {
        return state.name();
    }

    private int HP() {
        return ctx.skills.level(Constants.SKILLS_HITPOINTS);
    }

    private int lostHP() {
        return ctx.skills.realLevel(Constants.SKILLS_HITPOINTS) - ctx.skills.level(Constants.SKILLS_HITPOINTS);
    }

    private int HPercent() {
        return ctx.skills.level(Constants.SKILLS_HITPOINTS)*100/ctx.skills.realLevel(Constants.SKILLS_HITPOINTS);
    }
}

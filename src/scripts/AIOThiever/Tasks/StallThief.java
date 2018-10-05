package scripts.AIOThiever.Tasks;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class StallThief extends Task<ClientContext> {
    public StallThief(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private String stallName = "";
    private State state = State.WAIT;
    private GameObject stall = ctx.objects.nil();


    @Override
    public void initialise() {
        stallName = promptNearbyNpc();
        if (stallName.isEmpty()) ctx.controller.stop();
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
                Condition.sleep(2000);
                break;
            case PICK:
                stall = (stall.valid() && !stall.equals(ctx.objects.nil())) ? stall : ctx.objects.select().name(stallName).nearest().poll();
                Utils.stepInteract(stall,"Steal-from");
                Condition.sleep();
                break;
            case HEAL:
                for (Item i : ctx.inventory.items()) {
                    if (Arrays.asList(i.actions()).contains("Eat") || i.name().equals("Jug of wine")) {
                        i.click();
                        Condition.wait(()->ctx.players.local().animation() == -1);
                        break;
                    }
                }
                break;
            case DROP:
                ctx.inventory.shiftDrop(-1);
                break;
            case RUN:
                Tile tile = ctx.players.local().tile().derive(0,40);
                if (Condition.wait(()->ctx.movement.stepWait(tile))) ctx.controller.stop();

                break;
        }
    }

    @Override
    public void message(MessageEvent me) {

    }

    private State getState() {
//        if (ctx.players.local().animation() == 424) {
//            return State.MOVE;
//        }
        if (ctx.players.local().inCombat()) {
            return State.RUN;
        }
        if ((HP() < 7 || lostHP() > 10)) {
            return State.HEAL;
        }
        if (ctx.inventory.select().count() == 28) {
            return State.DROP;
        }
        return State.PICK;
//        return State.MOVE;
    }

    private enum State {
        WAIT,PICK,HEAL,DROP,RUN
    }

    private String promptNearbyNpc() {
        ArrayList<GameObject> allMobs = new ArrayList<>();
        ArrayList<String> objList = new ArrayList<>();
        ctx.objects.select().addTo(allMobs);
        for (GameObject m : allMobs) {
            if (!m.name().toLowerCase().contains("stall")) continue;
            if (!objList.contains(m.name())) objList.add(m.name());
        }
        String[] mobs = objList.toArray(new String[objList.size()]);
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                mobs, mobs[0]);

        if (selectedValue == null) return "";
        else return selectedValue.toString();
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
        return ctx.skills.level(Constants.SKILLS_HITPOINTS)/ctx.skills.level(Constants.SKILLS_HITPOINTS);
    }
}

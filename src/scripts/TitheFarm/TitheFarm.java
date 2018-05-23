package scripts.TitheFarm;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;

import java.util.HashSet;
import java.util.regex.Pattern;


@Script.Manifest(name = "TitheFarm", properties = "author=nomivore; topic=1341279; client=4;", description = "Plays Tithe Farm. Requires Humidify, see thread for details")
public class TitheFarm extends PollingScript<ClientContext> {

    private Status Status = new Status(ctx,Utils);
    private Status.State state;
    private Paint pcPaint = new Paint(ctx,Utils);
    private Tile base;

    public static long startTime;
    public static Area patchArea;
    public static final int[] unwateredIDss = new int[] {27384,27387,27390};
    public static final HashSet<Integer> unwateredIDs = new HashSet<Integer>() {{
        for (int i : unwateredIDss) {
            add(i);
        }
    }};
    public static GameObject patch;

    @Override
    public void start() {
        startTime = getRuntime();
        ctx.dispatcher.add(pcPaint);
        patch = ctx.objects.nil();
        base = ctx.players.local().tile();
        patchArea = new Area(base.derive(4,-7), base.derive(-4,+7));
    }

    @Override
    public void poll() {
        state = Status.getState();
        pcPaint.setStateString(state.toString());
        ctx.inventory.deselectItem();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).count() < 6) {
            castHumidify();
        }

        boolean leftClick = true;
        if (!ctx.objects.select().within(10).id(27393).isEmpty()) {
            leftClick = false;
        }
        switch (state) {
            case WAIT:
//                Condition.sleep(2000);
//                if (base.distanceTo(ctx.players.local()) > 3)
//                        ctx.movement.stepWait(base);
                break;
            case PLANTING:
                Utils.APturnTo(patch);
                Item seed = ctx.inventory.select().name(Pattern.compile("(.* seed)")).poll();
                final int seedCount = seed.stackSize();
//                if (!patch.inViewport()) ctx.camera.turnTo(patch);
                seed.interact("Use");
                patch.interact(leftClick,"Use",patch.name());
//                ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).poll().hover();
                Condition.wait(()->patch.tile().distanceTo(ctx.players.local()) < 3, 500,7);
                Condition.wait(()->seedCount != ctx.inventory.select().name(Pattern.compile("(.* seed)")).poll().stackSize(),500,5);
                break;
            case WATERING:
                Utils.APturnTo(patch);
                Item wateringCan = ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).poll();
                final int canCount = ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).count();
                wateringCan.interact("Use",wateringCan.name());
                patch.interact(leftClick,"Use",patch.name());
                Condition.wait(()->patch.tile().distanceTo(ctx.players.local()) < 4, 500,7);
                Condition.wait(()->ctx.players.local().animation() != -1,300,7);
                break;
            case RESETTING:
                ctx.movement.stepWait(base);
                break;
            case HARVESTING:
                Utils.APturnTo(patch);
                patch.interact(leftClick,"Harvest",patch.name());
                final int fruitCount = ctx.inventory.select().name(Pattern.compile("(.* fruit)")).poll().stackSize();
                Condition.wait(()->patch.tile().distanceTo(ctx.players.local()) < 3, 500,7);
                Condition.wait(()->fruitCount != ctx.inventory.select().name(Pattern.compile("(.* fruit)")).poll().stackSize(),300,5);
                break;
            case CLEARING:
                Utils.APturnTo(patch);
                patch.interact(leftClick,"Clear",patch.name());
                Condition.wait(()->patch.tile().distanceTo(ctx.players.local()) < 3, 500,7);
                Condition.wait(()->ctx.players.local().animation() != -1,300,7);
                break;
        }
    }

    private void castHumidify() {
        if (!ctx.widgets.component(218, 105).visible()) ctx.game.tab(Game.Tab.MAGIC);
        if (ctx.widgets.component(218, 105).click()) {
            Condition.wait(() -> ctx.players.local().animation() == -1, 300, 8);
        }
    }
}
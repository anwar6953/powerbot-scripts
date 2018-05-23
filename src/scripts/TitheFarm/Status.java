package scripts.TitheFarm;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.rt4.GameObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static scripts.TitheFarm.TitheFarm.patch;
import static scripts.TitheFarm.TitheFarm.patchArea;
import static scripts.TitheFarm.TitheFarm.unwateredIDs;

public class Status {
    private ClientContext ctx;
    private PollingScript.Utils Utils;
    public Status(ClientContext ctx, PollingScript.Utils Utils) {
        this.ctx = ctx;
        this.Utils = Utils;
    }

    List<GameObject> patches = new ArrayList<>();
//    GameObject curr = ctx.objects.nil();
    private State state;
    public State getState() {
        if (patches.isEmpty()) {
            ctx.objects.select().select(obj -> patchArea.contains(obj) &&
                    (obj.name().contains("Golovanova") ||
                            obj.name().contains("Tithe patch"))).addTo(patches);
            patches.sort(new CustomComparator());
        }
        System.out.print(patches.size());

        state = State.WAIT;

        for (GameObject g : patches) {
            System.out.print(g.name() + "\n");
            if (!g.valid() || g.id() <= 0) continue;
            patch = g;
            if (g.name().equals("Tithe patch") && !ctx.inventory.select().name(Pattern.compile("(.* seed)")).isEmpty()) {
                state = State.PLANTING;
                break;
            }
            if (unwateredIDs.contains(g.id()) && !ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).isEmpty()) {
                state = State.WATERING;
                break;
            }
            if (g.name().contains("Blighted")) {
                state = State.CLEARING;
                break;
            }
            if (g.id() == 27393) {
                state = State.HARVESTING;
                break;
            }
        }
        if (state == State.WAIT) patches.clear();
        else patches.remove(patch);
//
//        for (GameObject g : patches) {
//            if (ctx.inventory.select().name(Pattern.compile("(.* seed)")).isEmpty()) break;
//            if (!g.valid() || g.id() <= 0) continue;
//            String name = g.name();
//            if (name.equals("Tithe patch")) {
//                patch = g;
//                patches.remove(g);
//                return State.PLANTING;
//            }
//        }
//        for (GameObject g : patches) {
//            if (ctx.inventory.select().name(Pattern.compile("Watering can\\(.*")).isEmpty()) break;
//            if (!g.valid() || g.id() <= 0) continue;
//            if (unwateredIDs.contains(g.id())) {
//                patch = g;
//                patches.remove(g);
//                return State.WATERING;
//            }
//        }
//        for (GameObject g : patches) {
//            if (!g.valid() || g.id() <= 0) continue;
//            if (g.name().contains("Blighted")) {
//                patch = g;
//                patches.remove(g);
//                return State.CLEARING;
//            }
//        }
//        for (GameObject g : patches) {
//            if (!g.valid() || g.id() <= 0) continue;
//            if (g.id() == 27393) {
//                patch = g;
//                patches.remove(g);
//                return State.HARVESTING;
//            }
//        }
//        patches.clear();
        return state;
    }

    private boolean hasReqs() {
        return (ctx.inventory.select().name("Seed dibber").isEmpty() ||
                ctx.inventory.select().name("Spade").isEmpty() ||
                ctx.inventory.select().name(Pattern.compile("(.* seed)")).isEmpty() ||
                ctx.inventory.select().name(Pattern.compile("(Watering can\\(\\d)")).isEmpty());
    }

    private boolean canHumidify() {
        return (!ctx.inventory.select().name("Watering can").isEmpty() &&
        !ctx.inventory.select().name("Astral rune").isEmpty());
    }

    public enum State {
        WAIT, PLANTING, RESETTING, WATERING, HARVESTING, CLEARING
    }

    public class CustomComparator implements Comparator<GameObject> {
        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.tile().y() - o2.tile().y();
        }
    }
}

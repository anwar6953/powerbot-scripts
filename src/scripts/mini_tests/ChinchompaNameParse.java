package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

//@Script.Manifest(name = "ChinchompaNameParse", properties = "client=4;", description = "")
public class ChinchompaNameParse extends PollingScript<ClientContext> implements PaintListener{
    private Npc npc = ctx.npcs.nil();
    private String parsedName = "";
    private HashMap<String,String> names = new HashMap<>();

    @Override
    public void start() {
        super.start();
        names.put("Chinchompa","Grey chinchompas");
        names.put("Carnivorous chinchompa","Red chinchompa");
        names.put("Black chinchompa","Black chinchompa");
        for (Map.Entry<String,String> entry : names.entrySet()) {
            ctx.npcs.select().name(entry.getKey()).nearest();
            if (ctx.npcs.isEmpty()) continue;
            npc = ctx.npcs.poll();
            parsedName = entry.getValue();
            break;
        }
    }

    @Override
    public void poll() {
        for (Map.Entry<String,String> entry : names.entrySet()) {
            ctx.npcs.select().name(entry.getKey()).nearest();
            if (ctx.npcs.isEmpty()) continue;
            npc = ctx.npcs.poll();
            parsedName = entry.getValue();
            break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        Utils.simplePaint(g,npc.name(),parsedName);
        g.drawPolygon(npc.tile().matrix(ctx).bounds());
    }
}

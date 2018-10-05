package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import java.awt.*;
import java.util.ArrayList;

@Script.Manifest(name = "MouseHover", properties = "client=4;", description = "")
public class MouseHover extends PollingScript<ClientContext> implements PaintListener {

    @Override
    public void start() {
        super.start();
        ctx.input.blocking(false);
    }

    @Override
    public void poll() {
    }

    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics g) {
        strings.clear();
        for (MenuCommand m : ctx.menu.commands()) {
            strings.add(m.action);
        }
        Utils.simplePaint(g,strings);
    }
}
package scripts.mini_tests;

import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;

import java.awt.*;
import java.util.ArrayList;

import api.ClientContext;
import api.Components;
import api.PollingScript;

public class FindCloseButton extends PollingScript<ClientContext> implements PaintListener {
    private boolean bankOpen = false;
    private int size = 0;
    private Component c = ctx.components.nil();
    @Override
    public void start() {

    }

    @Override
    public void poll() {
        bankOpen = closeWindows();
    }

    private boolean closeWindows() {
        ctx.components.select().text("Grand Exchange","Collection box","Item Sets","options","display","keybinding","Clan Chat Setup").visible();
        size = ctx.components.size();
        if (ctx.components.isEmpty()) return false;
        for (Component c : ctx.components) {
            Widget parent = c.widget();
            Component closeButton = ctx.components.findCloseButton(parent);
            if (closeButton != ctx.components.nil()) return closeButton.click();
        }
        return false;
    }


    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        strings.clear();
//        ctx.chat.select();
//        for (ChatOption c : ctx.chat) strings.add(c.text());
//        strings.add("Can continue"+ctx.chat.canContinue());
//        strings.add("Pending input"+ctx.chat.pendingInput());
//        strings.add("Chatting"+ctx.chat.chatting());
//        strings.add("Size"+ctx.chat.size());
//        strings.add("Has previous"+ctx.chat.text("Previous").isEmpty());


        strings.add("Widget visible " + bankOpen);
        strings.add(size+"");
        Utils.simplePaint(g,strings);

        for (Component c : ctx.components.select().text("Grand Exchange","Collection box","Item Sets","options","display","keybinding","setup").visible()) {
            g.drawPolygon(Components.RectangleToPolygon(c));
        }
    }
}
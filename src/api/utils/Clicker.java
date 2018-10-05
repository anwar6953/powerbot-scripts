package api.utils;

import api.ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Interactive;

import java.awt.*;
import java.util.Arrays;

public class Clicker {
    private static Point clickPoint;

    public static void hoverClick(ClientContext ctx, Interactive obj, String hover) {
        if (!ctx.input.getLocation().equals(clickPoint)) clickPoint = null;
        boolean hovering = false;
        hovering = Condition.wait(()->
            !Arrays.asList(ctx.menu.commands()).isEmpty()
                    && Arrays.asList(ctx.menu.commands()).get(0).action.equals(hover)
        ,5,10);
//        for (MenuCommand m : ctx.menu.commands()) {
//            if (m.action.contains(hover)) {
//                hovering = true;
//                clickPoint = ctx.input.getLocation();
//            }
//        }
        if (!hovering) {
            clickPoint = null;
        } else {
            clickPoint = ctx.input.getLocation();
        }
        if (clickPoint != null) {
            ctx.input.click(clickPoint,true);
        } else {
            obj.click();
        }
    }

}

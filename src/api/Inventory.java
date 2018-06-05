package api;

import java.awt.*;
import java.util.List;
import java.util.Random;
import api.ClientContext.*;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;

import static api.ClientContext.invalidItemID;

public class Inventory extends org.powerbot.script.rt4.Inventory {

    public Inventory(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }


    public static Polygon polygon(Item i) {
        final Point center = i.centerPoint();
        final int WIDTH = 42, HEIGHT = 36;
        final Rectangle r = new Rectangle(center.x-WIDTH/2,center.y-HEIGHT/2, WIDTH, HEIGHT);
        return Components.RectangleToPolygon(r);
    }

    public Polygon polygon(int index) {
        final int WIDTH = 42, HEIGHT = 36;
        final Point base = this.component().screenPoint();
        final int x = base.x - 3 + (index % 4) * WIDTH, y = base.y - 2 + (index / 4) * HEIGHT;
        final Rectangle r = new Rectangle(x,y, WIDTH, HEIGHT);
        return Components.RectangleToPolygon(r);
    }

    public int[] pattern() {
        Random r = new Random();
        int i;
        i = r.nextInt(100);
        int[] pattern0 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};
        int[] pattern1 = {0,1,2,3,7,6,5,4,8,9,10,11,15,14,13,12,16,17,18,19,23,22,21,20,24,25,26,27};
        int[] pattern2 = {0,1,4,5,8,9,12,13,16,17,20,21,24,25,26,27,22,23,18,19,14,15,10,11,6,7,2,3};

        if (i%5 == 0) return pattern2;
        else if (i%4 == 0) return pattern0;
        else return pattern1;
    }

    public void shiftDrop(int... dropIDs) {
        shiftDrop("Drop",dropIDs);
    }

    public void shiftDrop(String dropString, int... dropIDs) {
        ctx.game.tab(Game.Tab.INVENTORY);
        Item[] inven = ctx.inventory.items();
        if (ctx.inventory.selectedItem().valid())
            ctx.inventory.selectedItem().interact("Cancel");
        for (int index : pattern()) {
            for (int ID : dropIDs) {
                if (inven[index].id() == ID || ID == -1) {
                    if (ctx.varpbits.varpbit(1055,17,1) == 1) {
                        ctx.input.send("{VK_SHIFT down}");
                        inven[index].click(true);
                        ctx.input.send("{VK_SHIFT up}");
                    } else {
                        inven[index].interact(dropString);
                    }
                    break;
                }
            }
        }
        ctx.input.send("{VK_SHIFT up}");
    }

    public void deselectItem() {
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
    }

    public boolean hasAll(int... itemIDs) {
        for (int i : itemIDs) {
            if (ctx.inventory.select().id(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int selectID(int[] ids) {
        for (int selectResource : ids) {
            if (!ctx.inventory.select().id(selectResource).isEmpty()) {
                return selectResource;
            }
        }
        return invalidItemID;
    }

    public Item lastItem(int id) {
        Item last = nil();
        for (Item it : ctx.inventory.items()) {
            if (it.id()==id && it.valid()) last = it;
        }
        return last;
    }

    public Item lastItem(int[] IDs) {
        Item last = nil();
        for (Item it : ctx.inventory.items()) {
            for (int ID : IDs) {
                if (it.id() == ID && it.valid()) last = it;
            }
        }
        return last;
    }

    public Item firstLeft(int id) {
        int[] left = {0,4,8,12,16,20,24};
        return selectIndexes(id,left);
    }

    public Item lastLeft(int id) {
        int[] lastLeft = {24,20,16,12,8,4,0};
        return selectIndexes(id,lastLeft);
    }

    public Item selectIndexes(int id, int... indexes) {
        Item[] inven = ctx.inventory.items();
        for (int index : indexes) {
            if (inven[index].id()==id && inven[index].valid()) return inven[index];
        }
        return ctx.inventory.select().id(id).poll();
    }

    public void dragItemTo(Item it, int index) {
        it.hover();
        Component c = ctx.widgets.component(149,0);
        int x = 16+index%4*42;
        int y = 15+index/4*33;
        Point p = new Point(c.nextPoint().x+x,c.nextPoint().y+y);
        Condition.sleep(500);
        ctx.input.drag(p, true);
    }
    public void dragItemTo(Item it1, Item it2) {
        it1.hover();
        Condition.sleep(500);
        ctx.input.drag(it2.centerPoint(), true);
    }
}

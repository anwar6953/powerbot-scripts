package CustomAPI;

import java.util.List;
import java.util.Random;
import CustomAPI.ClientContext.*;
import org.powerbot.script.rt4.Item;

import static CustomAPI.ClientContext.invalidItemID;

public class Inventory extends org.powerbot.script.rt4.Inventory {

    public Inventory(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
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

    public void deselectItem() {
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
    }

    public boolean hasAll(int[] itemIDs) {
        for (int i : itemIDs) {
            if (ctx.inventory.select().id(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int selectID(int[] ids) {
        for (int selectResource : ids) {
            if (ctx.inventory.select().id(selectResource).count(true) > 0) {
                return selectResource;
            }
        }
        return invalidItemID;
    }

    public int selectID(List<itemSkillPair> pairs, int skill) {
        int level = ctx.skills.realLevel(skill);
        for (itemSkillPair isp : pairs) {
            if (ctx.inventory.select().id(isp.ID).count(true) > 0 &&
                    level >= isp.level) {
                return isp.ID;
            }
        }
        return invalidItemID;
    }

    public Item lastItem(int ID) {
        Item last = nil();
        for (Item it : ctx.inventory.items()) {
            if (it.id()==ID && it.valid()) last = it;
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

    public Item firstLeft(int ID) {
        int[] left = {0,4,8,12,16,20,24};
        Item[] inven = ctx.inventory.items();
        for (int index : left) {
            if (inven[index].id()==ID && inven[index].valid()) return inven[index];
        }
        return ctx.inventory.select().id(ID).poll();
    }

    public Item lastLeft(int ID) {
        int[] left = {0,4,8,12,16,20,24};
        Item[] inven = ctx.inventory.items();
        Item leItem = ctx.inventory.select().id(ID).poll();
        for (int index : left) {
            if (inven[index].id()==ID && inven[index].valid()) leItem = inven[index];
        }
        return leItem;
    }

//    public void dragItemTo(Item it, int index) {
//    }
}

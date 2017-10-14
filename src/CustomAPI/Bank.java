package CustomAPI;

import CustomAPI.ClientContext.*;

import java.util.List;

import static CustomAPI.ClientContext.invalidItemID;

public class Bank extends org.powerbot.script.rt4.Bank {
    public Bank(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public int selectID(int[] ids) {
        for (int selectResource : ids) {
            if (ctx.bank.select().id(selectResource).count(true) > 0) {
                return selectResource;
            }
        }
        return invalidItemID;
    }

    public int selectID(List<itemSkillPair> pairs, int skill) {
        int level = ctx.skills.realLevel(skill);
        for (itemSkillPair isp : pairs) {
            if (!ctx.bank.select().id(isp.ID).isEmpty() &&
                    level >= isp.level) {
                return isp.ID;
            }
        }
        return invalidItemID;
    }

    public boolean noneLeft(int ID) {
        if (ctx.bank.select().id(ID).isEmpty() &&
                ctx.inventory.select().id(ID).isEmpty()) {
            return true;
        }
        return false;
    }
}

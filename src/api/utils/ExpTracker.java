package api.utils;

import api.ClientContext;

public class ExpTracker extends Timer {
    private int skill;
    private int startLevel;
    private int startExp;
    private int prevExp;
    private int expDrops;

    public ExpTracker(ClientContext ctx) {
        super(ctx);
    }

    public void initialise(int skill) {
        this.skill = skill;
        startExp = ctx.skills.experience(skill);
        startLevel = ctx.skills.realLevel(skill);
        prevExp = getExp();
    }

    @Override
    public long getRuntime() {
        if (prevExp < getExp()) {
            prevExp = getExp();
            expDrops++;
            resetTimer();
        }
        return ctx.controller.script().getRuntime() - startTime;
    }

    public int getExp() {
        return ctx.skills.experience(skill) - startExp;
    }

    public int level() {
        return ctx.skills.realLevel(skill);
    }

    public int expDrops() {
        return expDrops;
    }

    public int gainedLevels() {
        return ctx.skills.realLevel(skill) - startLevel;
    }
}

package api.utils;

import api.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.util.HashMap;
import java.util.logging.Logger;

public class Stats {
    protected Logger log = Logger.getLogger(getClass().getSimpleName());
    protected ClientContext ctx;
    protected Timer runTimer;
    protected ExpTracker expTracker;

    protected int skill;

    protected String skillString = "";

    public Stats(ClientContext ctx) {
        this.ctx = ctx;
        runTimer = new Timer(ctx);
        expTracker = new ExpTracker(ctx);
    }

    public void initialise(int skill) {
        this.skill = skill;
        setSkillString();
        expTracker.initialise(skill);
        runTimer.resetTimer();
        expTracker.resetTimer();
    }

    protected void setSkillString() {
        HashMap<Integer,String> skillMap = new HashMap<>();
        skillMap.put(Constants.SKILLS_ATTACK,"Attack");
        skillMap.put(Constants.SKILLS_DEFENSE,"Defense");
        skillMap.put(Constants.SKILLS_STRENGTH,"Strength");
        skillMap.put(Constants.SKILLS_HITPOINTS,"Hitpoints");
        skillMap.put(Constants.SKILLS_RANGE,"Range");
        skillMap.put(Constants.SKILLS_PRAYER,"Prayer");
        skillMap.put(Constants.SKILLS_MAGIC,"Magic");
        skillMap.put(Constants.SKILLS_COOKING,"Cooking");
        skillMap.put(Constants.SKILLS_WOODCUTTING,"Woodcutting");
        skillMap.put(Constants.SKILLS_FLETCHING,"Fletching");
        skillMap.put(Constants.SKILLS_FISHING,"Fishing");
        skillMap.put(Constants.SKILLS_FIREMAKING,"Firemaking");
        skillMap.put(Constants.SKILLS_CRAFTING,"Crafting");
        skillMap.put(Constants.SKILLS_SMITHING,"Smithing");
        skillMap.put(Constants.SKILLS_MINING,"Mining");
        skillMap.put(Constants.SKILLS_HERBLORE,"Herblore");
        skillMap.put(Constants.SKILLS_AGILITY,"Agility");
        skillMap.put(Constants.SKILLS_THIEVING,"Thieving");
        skillMap.put(Constants.SKILLS_SLAYER,"Slayer");
        skillMap.put(Constants.SKILLS_FARMING,"Farming");
        skillMap.put(Constants.SKILLS_RUNECRAFTING,"Runecrafting");
        skillMap.put(Constants.SKILLS_HUNTER,"Hunter");
        skillMap.put(Constants.SKILLS_CONSTRUCTION,"Construction");

        skillString = skillMap.get(skill);
    }

    public long runtime() {
        return runTimer.getRuntime();
    }

    public long level() {
        return ctx.skills.realLevel(skill);
    }

    public int expPerHour() {
        return Timer.unitPerHour(expTracker.getExp(),runTimer.getRuntime());
    }

    public long TTL() {
        if (expPerHour() == 0) return 0;
        return (long)(expToLevel()*(3600000/ expPerHour()));
    }

    public int expToLevel() {
        return ctx.skills.experienceAt(ctx.skills.realLevel(skill)+1) - ctx.skills.experience(skill);
    }

    public String skillName() {
        return skillString;
    }

    public int skill() {
        return skill;
    }

    public int exp() {
        return expTracker.getExp();
    }

    public int expDrops() {
        return expTracker.expDrops();
    }

    public int gainedLevels() {
        return expTracker.gainedLevels();
    }

    public long timeSinceExpDrop() {
        return expTracker.getRuntime();
    }

    public static String formatNumber(int n) {
        if (n < 1000) return String.valueOf(n);
        if (n < 1000000) return String.valueOf(n/1000)+"k";
        if (n >= 1000000) return String.valueOf(n/1000000)+"m";
        return String.valueOf(n);
    }

}

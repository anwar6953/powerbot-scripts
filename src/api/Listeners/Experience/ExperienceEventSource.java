package api.Listeners.Experience;

import api.Listeners.EventDispatcher;
import org.powerbot.script.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 11/16/15
 */

public class ExperienceEventSource implements Runnable {

    private final EventDispatcher dispatcher;
    private final ClientContext ctx;
    private final int skillCount;
    private final Map<Integer, Integer> experienceCache;
    private final HashMap<Integer, String> skillMap;

    public ExperienceEventSource(EventDispatcher dispatcher, ClientContext ctx) {
        this.dispatcher = dispatcher;
        this.ctx = ctx;
        this.skillCount = ((ctx instanceof org.powerbot.script.rt4.ClientContext) ? 23 : 26);
        this.experienceCache = new HashMap<Integer, Integer>();

        for (int i = 0; i < skillCount; i++) {
            experienceCache.put(i, getExperienceForSkillIndex(i));
        }
        skillMap = new HashMap<>();
        skillMap.put(Constants.SKILLS_ATTACK, "Attack");
        skillMap.put(Constants.SKILLS_DEFENSE, "Defense");
        skillMap.put(Constants.SKILLS_STRENGTH, "Strength");
        skillMap.put(Constants.SKILLS_HITPOINTS, "Hitpoints");
        skillMap.put(Constants.SKILLS_RANGE, "Range");
        skillMap.put(Constants.SKILLS_PRAYER, "Prayer");
        skillMap.put(Constants.SKILLS_MAGIC, "Magic");
        skillMap.put(Constants.SKILLS_COOKING, "Cooking");
        skillMap.put(Constants.SKILLS_WOODCUTTING, "Woodcutting");
        skillMap.put(Constants.SKILLS_FLETCHING, "Fletching");
        skillMap.put(Constants.SKILLS_FISHING, "Fishing");
        skillMap.put(Constants.SKILLS_FIREMAKING, "Firemaking");
        skillMap.put(Constants.SKILLS_CRAFTING, "Crafting");
        skillMap.put(Constants.SKILLS_SMITHING, "Smithing");
        skillMap.put(Constants.SKILLS_MINING, "Mining");
        skillMap.put(Constants.SKILLS_HERBLORE, "Herblore");
        skillMap.put(Constants.SKILLS_AGILITY, "Agility");
        skillMap.put(Constants.SKILLS_THIEVING, "Thieving");
        skillMap.put(Constants.SKILLS_SLAYER, "Slayer");
        skillMap.put(Constants.SKILLS_FARMING, "Farming");
        skillMap.put(Constants.SKILLS_RUNECRAFTING, "Runecrafting");
        skillMap.put(Constants.SKILLS_HUNTER, "Hunter");
        skillMap.put(Constants.SKILLS_CONSTRUCTION, "Construction");
    }

    @Override
    public void run() {
        while (dispatcher.isRunning() || !ctx.controller.isStopping()) {
            for (int i = 0; i < skillCount; i++) {
                int oldExperience = experienceCache.get(i);
                int newExperience = getExperienceForSkillIndex(i);

                if (oldExperience != newExperience) {
                    dispatcher.fireEvent(new ExperienceEvent(i, skillMap.get(i), oldExperience, newExperience));
                    experienceCache.put(i, newExperience);
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) { }
        }
    }

    private int getExperienceForSkillIndex(int skillIndex) {
        return (ctx instanceof org.powerbot.script.rt4.ClientContext) ?
                ((org.powerbot.script.rt4.ClientContext) ctx).skills.experience(skillIndex) :
                ((org.powerbot.script.rt6.ClientContext) ctx).skills.experience(skillIndex);
    }

}

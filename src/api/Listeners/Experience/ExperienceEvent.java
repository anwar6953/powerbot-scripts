package api.Listeners.Experience;

import java.util.EventObject;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 11/16/15
 */

public class ExperienceEvent extends EventObject {

    private final int skillIndex;
    private final String skillName;
    private final int oldExperience;
    private final int newExperience;

    public ExperienceEvent(int skillIndex, String skillName, int oldExperience, int newExperience) {
        super(skillIndex);
        this.skillIndex = skillIndex;
        this.skillName = skillName;
        this.oldExperience = oldExperience;
        this.newExperience = newExperience;
    }

    public int getSkillIndex() {
        return skillIndex;
    }

    public int getOldExperience() {
        return oldExperience;
    }

    public int getNewExperience() {
        return newExperience;
    }

    public int getExperienceChange() {
        return newExperience - oldExperience;
    }

    public String getSkillName() {
        return skillName;
    }
}
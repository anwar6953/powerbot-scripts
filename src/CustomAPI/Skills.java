package CustomAPI;


public class Skills extends org.powerbot.script.rt4.Skills {

    public Skills(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public boolean almostLevel(int skill) {
        int level = ctx.skills.level(skill);
        if (ctx.skills.experienceAt(level+1) - ctx.skills.experience(skill) < Math.pow(level,2.1) &&
                (level%10 == 9 || level == 98) &&
                level > 25) {
            return true;
        } else {
            return false;
        }
    }

    public int remainingXP(int skill) {
        int level = ctx.skills.level(skill);
        return ctx.skills.experienceAt(level+1) - ctx.skills.experience(skill);
    }
}

package api;


public class Skills extends org.powerbot.script.rt4.Skills {

    public Skills(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public int remainingXP(int skill) {
        int level = ctx.skills.level(skill);
        return ctx.skills.experienceAt(level+1) - ctx.skills.experience(skill);
    }

}

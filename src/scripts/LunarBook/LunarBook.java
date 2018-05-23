package scripts.Magic.LunarBook;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
@Script.Manifest(name = "SpinFlax", properties = "author=nomivore; topic=1341511; client=4;",description = "Casts Spin Flax")
public class LunarBook extends PollingScript<ClientContext> implements PaintListener {
    private int[] allowedIDs = new int[] {1750755,1793587}; //737537
    private HashSet<Integer> allowedIDsSet = new HashSet<Integer>() {{
        for (int i : allowedIDs) {
            add(i);
        }
    }};
    private boolean fullAccess = false;
    public static long startTime;

    public static int startExp,
        level,
        startLevel,
        productDone,
        expEarned,

        skill = Constants.SKILLS_MAGIC;
    private SpinFlax spinFlax = new SpinFlax(ctx,Utils);

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(skill);
        startLevel = ctx.skills.realLevel(skill);
        int userID = Integer.parseInt(ctx.properties.getProperty("user.id"));
        if (allowedIDsSet.contains(userID) || ctx.properties.getProperty("user.vip").equals("true")) { //
            fullAccess = true;
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Must be VIP for access.");
            ctx.controller.stop();
        }
        //Currently no locked features
        log.info("User " + userID + " Access " + fullAccess);
    }

    @Override
    public void poll() {
        level = ctx.skills.realLevel(skill);
        if (spinFlax.activate()) {
            spinFlax.execute();
            return;
        }
        ctx.controller.stop();
    }


    private Font font = new Font("Trebuchet MS", Font.PLAIN, 20);
    private String versionString = "Version 1.0.0";
    private ArrayList<String> strings = new ArrayList<>();
    public void repaint(Graphics g) {
        strings.clear();
        expEarned = ctx.skills.experience(skill) - startExp;
        int expHr = (int) (expEarned * 3600000D / Utils.realRuntime(startTime)) / 100 * 100;

        strings.add(versionString);
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add(String.format("Spells cast %d", productDone));
        strings.add(String.format("Magic level %d", level));
        strings.add(String.format("Exp %d/hr", expHr));
        strings.add(String.format("Levels gained %d", ctx.skills.realLevel(skill) - startLevel));
        g.setFont(font);
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
    }
}

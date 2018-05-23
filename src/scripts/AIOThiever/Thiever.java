package scripts.AIOThiever;

import api.ClientContext;
import api.PollingScript;
import scripts.AIOThiever.Tasks.Pickpocket;
import scripts.AIOThiever.Tasks.StallThief;
import scripts.AIOThiever.Tasks.Task;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Constants;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;


@Script.Manifest(name = "AIOThiever", properties = "author=nomivore; topic=1341133; client=4;", description = "Thiever")
public class Thiever extends PollingScript<ClientContext> implements MessageListener{
    public static long startTime;
    public static int startExp,
        earnedExp,
        level,
        skill = Constants.SKILLS_THIEVING;
    private Paint tvPaint = new Paint(ctx,Utils);
    private Message tvMessage = new Message(ctx,Utils);
    private int[] allowedIDs = new int[] {1750755};
    private HashSet<Integer> allowedIDsSet = new HashSet<Integer>() {{
        for (int i : allowedIDs) {
            add(i);
        }
    }};
    private boolean fullAccess = false;

    private Task currTask;

    @Override
    public void start() {
        int userID = Integer.parseInt(ctx.properties.getProperty("user.id"));
        if (allowedIDsSet.contains(userID)) {
            fullAccess = true;
        }
        log.info("User " + userID + " Access " + fullAccess);
        switch ((taskType)JOptionPane.showInputDialog(null,"Choose task","Settings", JOptionPane.QUESTION_MESSAGE,null,taskType.values(),taskType.PICKPOCKET)) {
            case PICKPOCKET:
                currTask = new Pickpocket(ctx,Utils);
                break;
            case STALL:
                currTask = new StallThief(ctx,Utils);
                break;
        };

        startTime = getRuntime();
        startExp = ctx.skills.experience(skill);
        ctx.dispatcher.add(tvPaint);
        ctx.dispatcher.add(tvMessage);
        currTask.initialise();

    }

    private enum taskType {
        PICKPOCKET, STALL
    }

    @Override
    public void stop() {
        try {
            String path = getStorageDirectory() + "\\log.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.append(Utils.runtimeFormatted(startTime));
            writer.append("\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void poll() {
        earnedExp = ctx.skills.experience(skill) - startExp;
        level = ctx.skills.realLevel(skill);
        if (currTask.activate()) {
            tvPaint.setStateName(currTask.getStateName());
            currTask.execute();
            return;
        }
//        ctx.controller.stop();
    }

    @Override
    public void messaged(MessageEvent me) {
        if (currTask.activate()) {
            currTask.message(me);
            return;
        }
    }
}

package scripts.loader;

import api.ClientContext;
import api.utils.Stats;
import api.utils.Timer;
import api.PollingScript;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import scripts.loader.f2ptobond.SeagullKiller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@Script.Manifest(
        name = "AAARequestLoader", properties = "author=LOL; topic=1; client=4;",
        description = "Loader")
public class Loader extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private long
            waitTime;
    private Stats stats = new Stats(ctx);
    private ArrayList<Task> taskArrayList = new ArrayList<>(Arrays.asList(
        new SeagullKiller(ctx,Utils)
    ));
    private Task task;

    @Override
    public void start() {
        task = (Task) JOptionPane.showInputDialog(null,"Select task","Option", JOptionPane.INFORMATION_MESSAGE,null,
                taskArrayList.toArray(),taskArrayList.get(0));
        stats.initialise(task.getSkill());
        task.initialise();
        log.info("Started " + task.toString());
    }

    @Override
    public void stop() {
        log.info("Runtime " + stats.runtime());
        log.info("Level " + stats.level());
        log.info("Exp " + stats.exp() + ":" + stats.expPerHour());
        log.info("Gained levels "+ stats.gainedLevels());
    }

    @Override
    public void poll() {
        failStop();
        if (task.activate()) task.execute();
        else ctx.controller.stop();
    }

    @Override
    public void messaged(MessageEvent me) {
        if (task instanceof MessageListener) ((MessageListener) task).messaged(me);
    }

    private final Font font = new Font("Trebuchet MS", Font.PLAIN, 18);
    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics g) {
        g.setFont(font);
        if (task == null) return;
        strings.clear();
        strings.add(Timer.runtimeFormatted(stats.runtime()));
        strings.add(stats.skillName() + " level " + stats.level());
        strings.add("Exp " + stats.exp() + ":" + stats.expPerHour());
        strings.add("Actions " + stats.expDrops() + ":" + Timer.unitPerHour(stats.expDrops(),stats.runtime()));
        strings.add("State " + task.getStateName());
        strings.add("Last exp drop " + Timer.formatTime(stats.timeSinceExpDrop()));
        strings.add("TTL " + Timer.formatTime(stats.TTL()));
        strings.add("Stop time " + Utils.formatTime(task.stopTime()));
        strings.add(String.format("Fatigue scale %.2f", task.gausTimeScale()));
        Utils.simplePaint(g,strings);

        if (task instanceof PaintListener) {
            ((PaintListener) task).repaint(g);
        }
    }

    private void failStop() {
        if (stats.runtime() > task.stopTime()) {
            log.info("Over stop time, stopping");
            ctx.controller.stop();
        }
        if (stats.timeSinceExpDrop() > 300000) {
            log.info("Haven't gained exp in 5 minutes, stopping");
            ctx.controller.stop();
        }
    }

}

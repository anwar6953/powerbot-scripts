package scripts.GEAfker;

//import org.powerbot.bot.rt4.EventDispatcher;
import api.Listeners.EventDispatcher;
import api.Listeners.Inventory.InventoryEvent;
import api.Listeners.Inventory.InventoryListener;
import org.powerbot.script.*;
import api.ClientContext;
import api.PollingScript;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


//@Script.Manifest(
       //  name = "AAAfker", properties = "author=nomivore; client=4;",
      //description = "Does various GE afk activities")

public class GEAfker extends PollingScript<ClientContext> implements PaintListener, MessageListener, InventoryListener
{
    public static List<Task> taskList = new ArrayList<Task>();
    private long startTime;
    private Task currTask;
    private EventDispatcher eventDispatcher;

    public void start()
    {
//        taskList.add(new GenericCombinerr(ctx, "Sapphires","1", ResItem.Sapphires));
        taskList.add(new GenericCombinerr(ctx, "UltraC","1", ResItem.UltraCompost));
        taskList.add(new GenericCombinerr(ctx, "Amethyst", "2", ResItem.AmethystTips));
        taskList.add(new GenericCombinerr(ctx, "XericF","2", ResItem.XericFab));
        taskList.add(new BoltEnchanter(ctx));
        taskList.add(new PlankMake(ctx));
//        taskList.add(new HighAlch(ctx));
        taskList.add(new Composter(ctx));
//        taskList.add(new FlaxSpin(ctx));
//        taskList.add(new Humidify(ctx));
//        taskList.add(new GenericCombinerr(ctx, ResItem.StringMagic));
//        System.out.print(getStorageDirectory());
//        final GUI gui = new GUI(ctx);
//
//        while(!gui.done()) {
//            Condition.afkReturn();
//        }

        for (Task task : taskList) {
            task.initialise();
        }
//        eventDispatcher = ctx.dispatcher.add(Listeners);
        startTime = getRuntime();

        eventDispatcher = new EventDispatcher(ctx);
        eventDispatcher.addListener(this);
    }


    public void poll()
    {
        if (Utils.realRuntime(startTime) > 7200000) ctx.controller.stop();
        if (ctx.chat.canContinue()) {
            ctx.input.send("{VK_SPACE}");
            Condition.sleep(2000);
            ctx.input.send("{VK_SPACE}");
        }

        boolean cont = false;
        for (Task task : taskList) {
            if (task.activate()) {
                currTask = task;
                cont = true;
                break;
            }
        }

        if (currTask != null) {
            currTask.Utils.stuckCheck(Utils.realRuntime(startTime));
            currTask.execute();
            // if task finishes on this run
            if (!currTask.activate()) logData(currTask);
        }

        if (!cont) {
            ctx.bank.close();
//            ctx.game.tab(Game.Tab.LOGOUT);
//            ctx.widgets.component(182, 10).interact("Logout");
            ctx.controller.stop();
        }
    }

    public void stop() {
        eventDispatcher.setRunning(false);
        try {
            String path = getStorageDirectory() + "\\file.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.append(Utils.runtimeFormatted(startTime));
            writer.append("\n");
//            for (Task task : taskList) {
//                if (task.productDone != 0 || task.limit != 0) {
//                    String temp = task.getActionName(Utils.realRuntime(startTime)) +
//                            "\nTime between activate and start" + formatTime(task.startTime - task.activateTime) +
//                            "\nTime between start and end" + formatTime(task.endTime - task.startTime);
//                    log.info(temp);
//                    writer.append(' ');
//                    writer.append(temp);
//                    writer.append("\n");
//                }
//            }
            writer.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Override
    public void messaged(MessageEvent me) {
        if (currTask != null) currTask.message(me);
    }


    @Override
    public void onInventoryChange(InventoryEvent inventoryEvent) {
        if (currTask != null) currTask.inventory(inventoryEvent);
    }

    private BufferedImage img;
    private Font font = new Font("Trebuchet MS", Font.PLAIN, 18);
    private ArrayList<String> strings = new ArrayList<>();
//    String[] strings;
    @Override
    public void repaint(Graphics g) {
        if (img == null) img = Utils.downloadBackground("http://i.imgur.com/urjji0r.png");
        else Utils.paintBackground(g, img);
        g.setFont(font);
        strings.clear();
        strings.add(Utils.runtimeFormatted(startTime));
        for (Task task : taskList) {
            if (task.productDone != 0 || task.limit != 0)
            strings.add(task.getActionName(Utils.realRuntime(startTime)));
        }
        Utils.paintStrings(g, strings.toArray(new String[strings.size()]));

    }

    private void logData(Task task) {
        try {
            String path = getStorageDirectory() + "\\file.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.append(Utils.runtimeFormatted(startTime));
            writer.append("\n");
            if (task.productDone != 0 || task.limit != 0) {
                String temp = task.getActionName(Utils.realRuntime(startTime)) +
                        "\nTime between activate and start " + Utils.formatTime(task.startTime - task.activateTime) +
                        "\nTime between start and end " + Utils.formatTime(task.endTime - task.startTime);
                log.info(temp);
                writer.append(' ');
                writer.append(temp);
                writer.append("\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

}
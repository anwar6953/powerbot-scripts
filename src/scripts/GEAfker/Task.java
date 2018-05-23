package scripts.GEAfker;

import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.MessageEvent;
//import api.ClientContext;
import api.ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Game;
import java.util.Random;
import java.util.concurrent.Callable;


public abstract class Task<C extends ClientContext> extends ClientAccessor<C>
{
  protected Utils Utils = new Utils();
  public Task(C ctx)
  {
    super(ctx);
  }

  private Random r = new Random();
  private int i;

  protected boolean failed = false;

  public int limit;
  public int resourceID1;
  public int[] resourceIDARRAY1;
  public int resourceID2;
  public int[] resourceIDARRAY2;
  public int resourceLeft1 = 1;
  public int resourceLeft2 = 1;
  public int productDone;
  public int skill;
  public String gameMsg;
  public String taskName;
  public String actionName;
  public long startTime;
  public long endTime;
  public long activateTime;
  public boolean expCheck;

  public abstract void initialise();

  public abstract boolean activate();

  public abstract void execute();

  public abstract void message(MessageEvent me);

public abstract void inventory(InventoryEvent ie);

  public boolean failed()
  {
    return failed;
  }

  public String getName() {
    return taskName;
  }

  public String getActionName(long runTime) {
    if (startTime == 0 && productDone > 0) startTime = runTime;
    if (activate()) endTime = runTime;
    int perHr = (int) (productDone * 3600000D / (endTime - startTime));
    return actionName + " " + productDone + "/" + limit + " (" + perHr + ")";
  }


  //  public abstract String getActionName(long runTime);
  public class Utils {

  public void stuckCheck(long runTime) {
    if (activate() && activateTime == 0) activateTime = runTime;
    if (runTime - activateTime > 60000 && productDone <= 5) {
      ctx.controller.stop();
      System.out.print("Abort due to being stuck\n");
    }
  }

  public void epilogue() {
    if (ctx.bank.opened()) {
      if (ctx.bank.depositInventory()) {
        Condition.wait(() -> ctx.inventory.select().count() == 0);
      }
    } else {
      openNearbyBank();
      epilogue();
    }
  }

  public boolean almostLevel() {
    int level = ctx.skills.level(skill);
    return ctx.skills.experienceAt(level + 1) - ctx.skills.experience(skill) < Math.pow(level, 2.1) &&
            (level % 10 == 9 || level == 98) &&
            level > 40;
  }

  public int[] pattern() {
    i = r.nextInt(100);
    int[] pattern0 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
    int[] pattern1 = {0, 1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27};
    int[] pattern2 = {0, 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24, 25, 26, 27, 22, 23, 18, 19, 14, 15, 10, 11, 6, 7, 2, 3};

    if (i % 5 == 0) return pattern2;
    else if (i % 4 == 0) return pattern0;
    else return pattern1;
  }

  public boolean makeXall() {
    i = r.nextInt(100);
    int s = 0;
    if (i % 9 == 0) s = 66;
    else if (i % 7 == 0) s = 55;
    else if (i % 6 == 0) s = 55;
    else s = 33;
    return ctx.chat.sendInput(s);
  }

  public void openNearbyBank() {
    if (ctx.bank.inViewport()) {
      if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
      if (ctx.bank.open()) {
        Condition.wait(() -> ctx.bank.opened(), 250, 10);
      }
      if (!ctx.bank.opened()) ctx.input.click(true);
    } else {
      ctx.camera.turnTo(ctx.bank.nearest());
    }
  }

  public void depositInventory() {
    if (ctx.bank.depositInventory()) {
      Condition.wait(() -> ctx.inventory.select().count() == 0);
    }
  }

  public void closeBank() {
    i = r.nextInt(100);
    if (ctx.bank.opened()) {
      if (i % 9 == 0) ctx.bank.close();
      else ctx.input.send("{VK_ESCAPE}");
      Condition.wait(() -> !ctx.bank.opened(), 500, 4);
    }
  }

  public int selectResource(int[] itemArray) {
    for (int selectResource : itemArray) {
      if (ctx.bank.select().id(selectResource).count(true) > 0) {
        return selectResource;
      }
    }
    for (int selectResource : itemArray) {
      if (ctx.inventory.select().id(selectResource).count(true) > 0) {
        return selectResource;
      }
    }
    return -1;
  }

  public void logout() {
    ctx.game.tab(Game.Tab.LOGOUT);
    ctx.widgets.component(182, 10).interact("Logout");
  }
}
}
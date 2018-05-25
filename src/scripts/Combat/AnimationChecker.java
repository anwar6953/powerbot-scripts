package scripts.Combat;

import api.ClientContext;

import static java.lang.Thread.sleep;

public class AnimationChecker implements Runnable {
    private static boolean recentlyAnimated = false;
    private ClientContext ctx;
    AnimationChecker(ClientContext ctx) {
        this.ctx = ctx;
    }
    @Override
    public void run() {
        while (!ctx.controller.isStopping()) {
            if (ctx.controller.isSuspended()) continue;
            recentlyAnimated = ctx.players.local().animation() != -1;
//            System.out.print(recentlyAnimated);
            try {
                if (recentlyAnimated) {
                    System.out.print("Recently Animated\n");
                    sleep(5000);
                }
                sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasRecentlyAnimated() {
        return recentlyAnimated;
    }
}

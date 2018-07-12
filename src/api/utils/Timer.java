package api.utils;

import api.ClientContext;

public class Timer {
    protected long startTime;
    protected ClientContext ctx;

    public Timer(ClientContext ctx) {
        this.ctx = ctx;
        resetTimer();
    }
    public Timer() {
        resetTimer();
    }

    public void resetTimer() {
        if (ctx != null)
            startTime = ctx.controller.script().getRuntime();
        else startTime = System.currentTimeMillis();
    }

    public long getRuntime() {
        if (ctx != null)
            return ctx.controller.script().getRuntime() - startTime;
        else return System.currentTimeMillis() - startTime;
    }

    public static String runtimeFormatted(final long time) {
        return String.format("Runtime %s", formatTime(time));
    }

    public static String formatTime(final long time) {
        int s = (int)Math.floor(time/1000 % 60);
        int m = (int)Math.floor(time/60000 % 60);
        int h = (int)Math.floor(time/3600000);
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static int unitPerHour(final int number,final long time) {
        return (int)(number*3600000D/time);
    }
    public int unitPerHour(final int number) {
        return (int)(number*3600000D/getRuntime());
    }

    public static int longToMinutes(long milli) {
        return (int)(milli/60000);
    }
}

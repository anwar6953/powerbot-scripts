package scripts.loader;

import api.ClientContext;
import api.Walker;
import api.utils.Clicker;
import api.utils.Timer;
import api.PollingScript;
import org.powerbot.script.ClientAccessor;

import java.util.Random;
import java.util.logging.Logger;

public abstract class Task<C extends ClientContext> extends ClientAccessor<C> {
    protected Clicker clicker = new Clicker();
    protected Walker walker = new Walker(ctx);
    protected Random r = new Random();
    protected Logger log = Logger.getLogger(getClass().getName());
    protected PollingScript.Utils Utils;
    protected Timer timer = new Timer();
    public Task(C ctx, PollingScript.Utils Utils) {
        super(ctx);
        this.Utils = Utils;
    }

    public abstract int stopTime();
    public abstract int getSkill();
    public abstract void initialise();
    public abstract boolean activate();
    public abstract void execute();
    public abstract void finish();
    public abstract String getStateName();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected int gausInt(int num) {
        int delay = Math.abs((int)(r.nextGaussian()*num*gausTimeScale()));
//        System.out.println("Delay " + num +"->"+ delay);
        return delay;
    }

    protected double gausTimeScale() {
        return 2- Math.exp(-timer.getRuntime()/3600000D);
    }
}
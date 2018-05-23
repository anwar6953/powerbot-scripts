package scripts.AIOThiever.Tasks;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.MessageEvent;

import java.util.Random;
import java.util.logging.Logger;

public abstract class Task<C extends ClientContext> extends ClientAccessor<C> {
    protected Random r = new Random();
    protected Logger log = Logger.getLogger(getClass().getName());
    protected PollingScript.Utils Utils;
    public Task(C ctx, PollingScript.Utils Utils) {
        super(ctx);
        this.Utils = Utils;
    }

    public abstract void initialise();
    public abstract boolean activate();
    public abstract void execute();
    public abstract void message(MessageEvent me);
    public abstract String getStateName();
}
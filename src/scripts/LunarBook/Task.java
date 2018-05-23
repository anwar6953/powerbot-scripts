package scripts.Magic.LunarBook;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.ClientAccessor;

public abstract class Task<C extends ClientContext> extends ClientAccessor<C> {
    public ClientContext ctx;
    public PollingScript.Utils Utils;

    public Task(C ctx, PollingScript.Utils Utils) {
        super(ctx);
        this.ctx = ctx;
        this.Utils = Utils;
    }

    public abstract boolean activate();

    public abstract void execute();
}

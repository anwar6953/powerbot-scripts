package scripts.AIOThiever;

import api.ClientContext;
import api.PollingScript;


public class Status {
    private ClientContext ctx;
    private PollingScript.Utils Utils;
    public Status(ClientContext ctx, PollingScript.Utils Utils) {
        this.ctx = ctx;
        this.Utils = Utils;
    }

    public State getState() {
        return State.WAIT;
    }

    public enum State {
        WAIT
    }
}

package scripts.AIOThiever;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;

public class Message implements MessageListener {
    private PollingScript.Utils Utils;
    private ClientContext ctx;
    Message(ClientContext ctx, PollingScript.Utils Utils) {
        this.Utils = Utils;
        this.ctx = ctx;
    }

    @Override
    public void messaged(MessageEvent me) {
        if (me.source().isEmpty()) {
        }
    }

}

package scripts.AIOFletcher;

import api.Antipattern;
import api.ClientContext;
import api.PollingScript;

import java.awt.*;

public class testAP extends Antipattern {
    public testAP(ClientContext ctx, PollingScript.Utils utils) {
        super(ctx, utils);
        profileHandler();
    }

    @Override
    public void repaint(Graphics graphics) {

    }
}

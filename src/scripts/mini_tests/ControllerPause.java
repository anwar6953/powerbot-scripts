package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.Script;

//@Script.Manifest(name = "ControllerPause", properties = "client=4;", description = "")
public class ControllerPause extends PollingScript<ClientContext> {

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void poll() {
//        if (ctx.bank.opened()) {
//            for (int i = 0; i < 1000; i++) {
//                ctx.controller.stop();
//                ctx.controller.suspend();
//                ctx.input.blocking(true);
//                ctx.input.speed(0);
//                Condition.sleep(10000);
//            }
//        }
        if (ctx.bank.opened()) {
            ctx.bank.close();
        } else {
            ctx.bank.open();
        }
    }
}
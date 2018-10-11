package scripts.mini_tests.image_test;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Script;

import javax.swing.*;
import java.awt.image.BufferedImage;

@Script.Manifest(name = "ImageTest", properties = "client=4;", description = "")
public class guiImageTest extends PollingScript<ClientContext> {
    @Override
    public void start() {
        new myGui(ctx);
    }

    @Override
    public void poll() {
        log.info("Polling");
        ctx.controller.stop();
    }
}


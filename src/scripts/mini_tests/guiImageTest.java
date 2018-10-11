package scripts.mini_tests;

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

class myGui {
    private ClientContext ctx;
    private JFrame frame;
    myGui(ClientContext ctx) {
        this.ctx = ctx;
        initComponents();
    }

    private void initComponents() {
        String headLocation = "http://" + ctx.properties.getProperty("sdn.host") + "/324912/resources/Head_slot.png";
        BufferedImage headIcon = ctx.controller.script().downloadImage(headLocation);
        JLabel HeadIcon = new JLabel(new ImageIcon(headIcon));

        JPanel panel = new JPanel();
        panel.add(HeadIcon);

        frame = new JFrame("LOL");
        frame.add(panel);

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
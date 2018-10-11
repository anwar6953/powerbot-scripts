package scripts.mini_tests.image_test;

import api.ClientContext;

import javax.swing.*;
import java.awt.image.BufferedImage;

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

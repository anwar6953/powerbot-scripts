package scripts.TitheFarm;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static scripts.TitheFarm.TitheFarm.startTime;

public class Paint implements PaintListener {
    private PollingScript.Utils Utils;
    private ClientContext ctx;

    Paint(ClientContext ctx, PollingScript.Utils Utils) {
        this.Utils = Utils;
        this.ctx = ctx;
    }

    private BufferedImage bg,
            buttonBlue,
            buttonBlack;

    private ArrayList<String> strings = new ArrayList<>();
    private String stateString = "";
    private final Font font = new Font("Trebuchet MS", Font.PLAIN, 16);
    private Point displayTogglePoint;
    private String versionString = "Version 0.1";

    @Override
    public void repaint(Graphics g) {
        g.setFont(font);

        strings.clear();
        strings.add(versionString);
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add(stateString);
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
//        g.drawPolygon(patchArea.getPolygon());
    }



    public void setStateString(String stateString) {
        this.stateString = stateString;
    }

}

package scripts.AIOThiever;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Paint implements PaintListener, MouseListener {
    private PollingScript.Utils Utils;
    private ClientContext ctx;

    Paint(ClientContext ctx, PollingScript.Utils Utils) {
        this.Utils = Utils;
        this.ctx = ctx;
    }

    private BufferedImage bg,
            buttonBlue,
            buttonRed,
            buttonGreen,
            buttonBlack;

    private final Font font = new Font("Trebuchet MS", Font.PLAIN, 18);
    private ArrayList<String> strings = new ArrayList<>();
    private String stateName = "";
    private String versionString = "Version 0.0.1";
    private Polygon displayTogglePoint;
    private DisplayState state = DisplayState.MINI;

    @Override
    public void repaint(Graphics g) {
        g.setFont(font);
        if (!nullCheck()) {
            bg = Utils.downloadBackground("");
            buttonBlue = Utils.downloadBackground("http://i.imgur.com/FGKj8td.png","buttonBlue");
            buttonGreen = Utils.changeHSV("buttonGreen", buttonBlue,(float)120/360, (float)-1, (float)-1);
            buttonRed = Utils.changeHSV("buttonRed", buttonBlue,(float)0/360, (float)-1 ,(float)-1);
            buttonBlack = Utils.changeHSV("buttonBlack", buttonBlue,(float)-1,(float)0,(float)-1);
            return;
        }
        if (state != DisplayState.MINI) {
            Utils.paintBackground(g, bg);
            displayTogglePoint = Utils.paintBackground(g, buttonBlue);
        }
        switch (state) {
            case MAIN:
                strings.clear();
                strings.add(versionString);
                strings.add(Utils.runtimeFormatted(Thiever.startTime));
                strings.add("State: " + stateName);
                Utils.paintStrings(g,strings.toArray(new String[strings.size()]));
                break;
//            case MOB:
//                g.setFont(fontt);
//                Utils.paintStrings(g,4, allMobNames);
//                mobPoints = Utils.paintImgs(g,4,mobButtons);
//                break;
            case MINI:
                strings.clear();
                displayTogglePoint = Utils.paintBackground(g, buttonBlack);
                strings.add("Nomivore's Thiever");
                strings.add(versionString);
                strings.add(Utils.runtimeFormatted(Thiever.startTime));
                strings.add("Exp(hr): " + Thiever.earnedExp + "("+Utils.unitPerHour(Thiever.earnedExp, Thiever.startTime) + ")");
                strings.add("Remaining " + ctx.skills.remainingXP(Thiever.skill));
                strings.add("Level " + Thiever.level);
                strings.add("State: " + stateName);
                Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
                break;
        }
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (displayTogglePoint == null || buttonBlue == null) return;
        if (displayTogglePoint.contains(e.getPoint())) {
            switch (state) {
                case MINI:
                    state = DisplayState.MAIN;
                    break;
                case MAIN:
                    state = DisplayState.MINI;
                    break;
            }
        }
        if (state == DisplayState.MINI) return;

    }

    private enum DisplayState {
        MINI, MAIN
    }

    private boolean nullCheck() {
        for (BufferedImage b : new BufferedImage[] {bg,
                buttonBlue,
                buttonRed,
                buttonGreen,
                buttonBlack,}) {
            if (b == null) return false;
        }
        return true;
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}

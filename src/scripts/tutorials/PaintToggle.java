package scripts.tutorials;


import api.Components;
import api.utils.ImageUtils;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import api.PollingScript;
import org.powerbot.script.Script;
import api.ClientContext;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Player;
import scripts.ID;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

@Script.Manifest(name = "PaintToggle", properties = "author=nomivore; topic=1341279; client=4;", description = "Kills chickens")
public class PaintToggle extends PollingScript<ClientContext> implements PaintListener, MouseListener {
    @Override
    public void poll() {
    }

    //Method 1, using a polygon
    private Polygon p = new Polygon();
    private int polygonWidth = 50;
    private int polygonHeight = 50;
    {
        p.addPoint(0,0);
        p.addPoint(polygonWidth,0);
        p.addPoint(polygonWidth,polygonHeight);
        p.addPoint(0,polygonHeight);
        p.translate(100,100);
    }

    //Method 2, using a single bufferedimage
    private BufferedImage toggleImage = new BufferedImage(50,50,BufferedImage.TYPE_3BYTE_BGR);
    private Polygon toggleImagePolygon = new Polygon();
    private Point toggleImagePoint = new Point(100,200);
    {
        toggleImagePolygon.addPoint(0,0);
        toggleImagePolygon.addPoint(toggleImage.getWidth(),0);
        toggleImagePolygon.addPoint(toggleImage.getWidth(),toggleImage.getHeight());
        toggleImagePolygon.addPoint(0,toggleImage.getHeight());
        toggleImagePolygon.translate(toggleImagePoint.x,toggleImagePoint.y);
        setColour(toggleImage,255,255,255);
    }

    private void setColour(BufferedImage img, int r,int g, int b) {
        Graphics graphics = img.getGraphics();
        graphics.setColor(new Color(r,g,b));
        graphics.fillRect(0,0,img.getWidth(),img.getHeight());
    }

    //Method 3, using an image, fixed point
    private BufferedImage imageOff = ImageUtils.getItemImage(ctx,ID.RED_BALLOON_9937);
    private BufferedImage imageOn = ImageUtils.getItemImage(ctx,ID.GREEN_BALLOON_9939);
    private Polygon imagePolygon = new Polygon();
    private Point imagePoint = chatboxTopLeft();
    {
        imagePolygon.addPoint(0,0);
        imagePolygon.addPoint(imageOn.getWidth(),0);
        imagePolygon.addPoint(imageOn.getWidth(),imageOn.getHeight());
        imagePolygon.addPoint(0,imageOn.getHeight());
        imagePolygon.translate(imagePoint.x,imagePoint.y);
    }

    //Method 4, using an image, dynamic point
    private boolean pointInImage(Point p, Point imagePoint, BufferedImage img) {
        Polygon imagePolygon = new Polygon();
        imagePolygon.addPoint(0,0);
        imagePolygon.addPoint(img.getWidth(),0);
        imagePolygon.addPoint(img.getWidth(),img.getHeight());
        imagePolygon.addPoint(0,img.getHeight());
        imagePolygon.translate(imagePoint.x,imagePoint.y);
        return imagePolygon.contains(p);
    }

    public Point chatboxTopLeft() {
        int y;
        if (ctx.game.resizable()) {
            y = (int) ctx.game.dimensions().getHeight() - 165;
        } else {
            y = 338;
        }
        return new Point(0,y);
    }

    private boolean toggled = false;
    @Override
    public void repaint(Graphics g) {
        //Method 1
        if (toggled) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.fillPolygon(p);

        //Method 2
        g.drawImage(toggleImage,toggleImagePoint.x,toggleImagePoint.y,null);

        //Method 3
        if (toggled) {
           g.drawImage(imageOn,imagePoint.x,imagePoint.y,null);
        } else {
           g.drawImage(imageOff,imagePoint.x,imagePoint.y,null);
        }

        //Method 4
        Point p = chatboxTopLeft();
        if (toggled) {
            g.drawImage(imageOn,p.x,p.y,null);
        } else {
            g.drawImage(imageOff,p.x,p.y,null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (
            //Method 1
            p.contains(e.getPoint()) ||
            //Method 2
            imagePolygon.contains(e.getPoint()) ||
            //Method 3
            toggleImagePolygon.contains(e.getPoint()) ||
            //Method 4
            pointInImage(e.getPoint(),chatboxTopLeft(),imageOn)
            )
            toggled = !toggled;

        //Method 2, set colour
        if (toggled) {
            setColour(toggleImage,0,0,0);
        } else {
            setColour(toggleImage,255,255,255);
        }
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
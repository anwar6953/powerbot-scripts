package api;

import scripts.ID;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Interactive;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;

public abstract class PollingScript<C extends ClientContext> extends org.powerbot.script.PollingScript<ClientContext> {
    protected Utils Utils;
    public PollingScript() {
        this.Utils = getUtils();
    }

    private Utils getUtils() {
        return new Utils();
    }


    public class Utils {
        private Random r = new Random();
        private int i;
        public final String buffer = "                                     ";


        public String runtimeFormatted(long startTime) {
            return String.format("Runtime %s", formatTime(realRuntime(startTime)));
        }

        public long realRuntime(long startTime) {
            return getRuntime() - startTime;
        }

        public String formatTime(long time) {
            int s = (int)Math.floor(time/1000 % 60);
            int m = (int)Math.floor(time/60000 % 60);
            int h = (int)Math.floor(time/3600000);
            return String.format("%02d:%02d:%02d", h, m, s);
        }

        public int unitPerHour(int number,long startTime) {
            return (int)(number*3600000D/realRuntime(startTime));
        }

        public void depositInventory() {
            if (ctx.bank.depositInventory()) {
                Condition.wait(() -> ctx.inventory.select().count() == 0,500,6);
                if (ctx.inventory.select().count() != 0) depositInventory();
            }
        }

        public <T extends Interactive & Nameable & InteractiveEntity & Identifiable & Validatable & Actionable>
            boolean stepInteract(T obj, String action) {
            if (obj.inViewport()) {
                if (action.isEmpty()) return obj.click();
                else return obj.interact(action,obj.name());
            } else {
                return ctx.movement.stepWait(obj);
            }
        }

        public <T extends Interactive & Nameable & InteractiveEntity & Identifiable & Validatable & Actionable>
            boolean stepInteract(T obj) {
            return stepInteract(obj, "");
        }

//        public boolean stepInteract(GameObject obj, String action) {
//            if (!Arrays.asList(obj.actions()).contains(action)) return false;
//            if (obj.inViewport()) {
//                return obj.interact(action,obj.name());
//            } else {
//                ctx.movement.stepWait(obj);
//            }
//            return false;
//        }
//        public boolean stepInteract(Npc obj, String action) {
//            if (!Arrays.asList(obj.actions()).contains(action)) return false;
//            if (obj.inViewport()) {
//                return obj.interact(action,obj.name());
//            } else {
//                ctx.movement.stepWait(obj);
//            }
//            return false;
//        }
//        public boolean stepInteract(Npc obj) {
//            if (obj.inViewport()) {
//                return obj.click();
//            } else {
//                ctx.movement.stepWait(obj);
//            }
//            return false;
//        }


        public void toggleQuickPrayer(boolean on) {
            if (ctx.varpbits.varpbit(84) == 0) return;
            Component prayer = ctx.widgets.component(160,16);
            int ppoints = Integer.parseInt(ctx.widgets.component(160,15).text());
            if (ppoints == 0) return;
            if (on) {
                if (prayer.textureId() == 1063) prayer.interact("Activate");
            } else {
                if (prayer.textureId() == 1066) prayer.interact("Deactivate");
            }
        }

        public boolean isRunning() {
            Condition.sleep(500);
            return ctx.widgets.component(160,24).textureId() == 1065;
        }

        public boolean checkAllSelected() {
            if (ctx.widgets.component(ID.WIDGET_MAKE,ID.COMPONENT_MAKE).visible()) {
                return ctx.widgets.component(ID.WIDGET_MAKE,12).textureId() == -1;
            }
            return false;
        }

        public Locatable nearest(Locatable... locatables) {
            Locatable nearest = locatables[0];
            double distance = nearest.tile().distanceTo(ctx.players.local());
            for(Locatable l : locatables) {
                if (l.tile().distanceTo(ctx.players.local()) < distance) nearest = l;
            }
            return nearest;
        }

        // used for Mouse listener
        public boolean pointInImage(Point topLeft, BufferedImage img, Point click){
            return (click.getX() < topLeft.getX() + img.getWidth()) &&
            (click.getX() > topLeft.getX()) &&
            (click.getY() > topLeft.getY()) &&
            (click.getY() < topLeft.getY() + img.getHeight());
        }

        // from http://tech.abdulfatir.com/2014/05/changing-hue-of-image.html
        public BufferedImage changeHSV(String name, BufferedImage img, float hue, float sat, float val) {
//            System.out.print("Change hue");
            name += ".png";
            File file = new File(getStorageDirectory(),name);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            BufferedImage processed = changeHSV(img,hue,sat,val);

            try {
                ImageIO.write(processed,"png",new File(getStorageDirectory() + "\\" + name));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return processed;
        }

        public BufferedImage changeHSV(BufferedImage img, float hue, float sat, float val) {
            BufferedImage processed = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
            for (int x = 0;x<img.getWidth();x++) {
                for (int y=0;y<img.getHeight();y++) {
                    Color c = new Color(img.getRGB(x, y), true);
                    if (c.getAlpha() <= 0) continue;
                    int RGB = img.getRGB(x,y);
                    int R = (RGB >> 16) & 0xff;
                    int G = (RGB >> 8) & 0xff;
                    int B = (RGB) & 0xff;
                    float HSV[]=new float[3];
                    HSV = Color.RGBtoHSB(R,G,B,HSV);
                    float h = (hue < 0) ? HSV[0] : hue;
                    float s = (sat < 0) ? HSV[1] : sat;
                    float v = (val < 0) ? HSV[2] : val;
                    processed.setRGB(x,y,Color.getHSBColor(h,s,v).getRGB());
                }
            }
            return processed;
        }

        public Point translatePoint(Point p, int x, int y) {
            Point newPoint = new Point(p);
            newPoint.translate(x,y);
            return newPoint;
        }

        public BufferedImage desaturateImg(BufferedImage img) {
            return changeHSV(img,-1,0,-1);
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

        public Polygon paintPoint(Graphics g, BufferedImage img, Point p) {
            g.drawImage(img, p.x, p.y,null);
            return imagePolygon(img,p.x, p.y);
        }

        public Polygon paintBackground(Graphics g, BufferedImage img){
            return paintBackground(g,img,0,0);
        }

        public Polygon paintBackground(Graphics g, BufferedImage img, int offsetX, int offsetY){
            Point destPoint = translatePoint(chatboxTopLeft(),offsetX,offsetY);
            return paintPoint(g,img,destPoint);
        }

        public Polygon imagePolygon(BufferedImage img, int x, int y) {
            Polygon p = new Polygon();
            p.addPoint(x,y);
            p.addPoint(x+img.getWidth(),y);
            p.addPoint(x+img.getWidth(),y+img.getHeight());
            p.addPoint(x,y+img.getHeight());
            return p;
        }

        //download background image to cover chatbox, returns BufferedImage
        public BufferedImage downloadBackground(String url){
            return downloadBackground(url,"background");
        }

        public BufferedImage downloadBackground(String url, String name){
            //default background, grey box
            if (url.isEmpty()) url = "http://i.imgur.com/tDyUr3Q.png";
            try {
                name += ".png";
                return downloadImage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

//        public BufferedImage downloadBackground(String url, String name){
//            //default background, grey box
//            if (url.isEmpty()) url = "http://i.imgur.com/tDyUr3Q.png";
//            try {
//                name += ".png";
//                //if no background image found, download
//                File file = new File(getStorageDirectory(),name);
//                if (file.exists()) {
//                    return ImageIO.read(file);
//                } else {
//                    download(url, name);
//                    URL imgUrl = new URL(url);
//                    return ImageIO.read(imgUrl);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }

        public void simplePaint(Graphics graphics, String... strings) {
            Graphics2D g = (Graphics2D) graphics;
            int yBase=0, xBase=10, x, y, width=185, height = 0;
            y = g.getFont().getSize()*120/100;
            x = xBase;

            height += strings.length*g.getFont().getSize()*110/100;

            g.setColor(Color.BLACK);
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
            g.setComposite(alphaComposite);
            g.fillRect(0, 0, width, height);

            g.setColor(Color.WHITE);
            for (String s : strings) {
                g.drawString(s,x,y);
                y += g.getFont().getSize();
            }
        }
        public void simplePaint(Graphics graphics, ArrayList<String> strings) {
            simplePaint(graphics, strings.toArray(new String[strings.size()]));
        }

        public void paintStrings(Graphics g, int cols, String... lines){
            int x;
            int y;
            int xBase = 40;
            int yBase;
            int counter = 1;
            if (ctx.game.resizable()) {
                yBase = (int) ctx.game.dimensions().getHeight() - 100;
            } else {
                yBase = 400;
            }
            y = yBase;
            x = xBase;
            for (String s : lines) {
                g.drawString(s,x,y);
                switch (counter % cols) {
                    case 0:
                        y += g.getFont().getSize()*120/100;
                        x = xBase;
                        break;
                    default:
                        x += 500/cols;
                        break;
                }
                counter++;
            }
        }

        public void paintStrings(Graphics g, String... lines){
            paintStrings(g, 2, lines);
        }

        public boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
            // The images must be the same size.
            if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
                int width = imgA.getWidth();
                int height = imgA.getHeight();

                // Loop over every pixel.
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        // Compare the pixels for equality.
                        if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }

            return true;
        }

        public void drawStringOutline(Graphics graphics, int x, int y, String text){
            Color color = graphics.getColor();
            Graphics2D g = (Graphics2D) graphics;
            FontRenderContext frc = g.getFontRenderContext();
            AffineTransform transform = g.getTransform();
            TextLayout textTl;
            Shape outline;

            transform.translate(x, y);
            textTl = new TextLayout(text, g.getFont(), frc);
            outline = textTl.getOutline(null);
            outline = transform.createTransformedShape(outline);
            g.setColor(Color.BLACK);
            g.draw(outline);

            g.setColor(color);
            g.drawString(text,x, y);
        }


        public void paintStringsOutline(Graphics graphics, String... lines){
            int x;
            int y;
            int xBase = 40;
            int yBase;
            int counter = 0;
            if (ctx.game.resizable()) {
                yBase = (int) ctx.game.dimensions().getHeight() - 100;
            } else {
                yBase = 400;
            }
            y = yBase;
            x = xBase;

            Graphics2D g = (Graphics2D) graphics;
            FontRenderContext frc = g.getFontRenderContext();
            AffineTransform transform = g.getTransform();
            TextLayout textTl;
            Shape outline;

            transform.translate(x, y);
//        g.transform(transform);
//        g.setStroke(new BasicStroke(1));
            for (String s : lines) {
//            System.out.print(s);
                textTl = new TextLayout(s, g.getFont(), frc);
                outline = textTl.getOutline(null);
                outline = transform.createTransformedShape(outline);
                g.draw(outline);
//            g.setClip(outline);
                if (counter % 2 == 0) {
                    transform.translate(250, 0);
                } else {
                    y = g.getFont().getSize()*120/100;
                    transform.translate(-250, y);
                }
                counter++;
            }
//        Font font = new Font(g.getFont().getFontName(), Font.BOLD, g.getFont().getSize()+2);
//        g.setFont(font);
//        g.drawString(title,xBase, yBase-30);
        }

        /**
         ANTIPATTERN METHODS
         **/
        public void openNearbyBank() {
            if (ctx.bank.inViewport()) {
                if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

                if (ctx.bank.open()) {
                    Condition.wait(() -> ctx.bank.opened(), 250, 5);
                }
                if (!ctx.bank.opened()) {
                    ctx.input.click(true);
                }
            } else {
                ctx.camera.turnTo(ctx.bank.nearest());
            }
        }

        public void openNearbyBank(int objID, String action) {
            if (ctx.bank.inViewport()) {
                if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
                if (ctx.objects.select(10).id(objID).nearest().poll().interact(action)) {
                    Condition.wait(() -> ctx.bank.opened(), 250, 10);
                }
            } else {
                ctx.camera.turnTo(ctx.objects.select(10).id(objID).nearest().poll());
            }
        }

        public void APidleStep() {
            APidleStep(2,ctx.players.local());
        }

        public void APidleStep(int der) {
            APidleStep(der,ctx.players.local());
        }

        public void APidleStep(int der,Locatable obj) {
            if (r.nextBoolean()) return;
            Tile center = obj.tile();
            int x = r.nextInt(1+der*2)-der;
            int y = r.nextInt(1+der*2)-der;
            Tile dest = center.derive(x,y);
            if (!ctx.movement.reachable(ctx.players.local(),dest)) {
                System.out.print("Not reachable");
                return;
            }
            ctx.movement.stepWait(center.derive(x,y));
        }

        public void APturnTo(Locatable obj) {
            if (obj.tile().matrix(ctx).inViewport()) return;
            new Thread(()->ctx.camera.pitch(r.nextInt(50)+50)).start();
            new Thread(() -> ctx.camera.turnTo(obj)).start();
//            if (!obj.tile().matrix(ctx).inViewport()) {
//                ctx.camera.turnTo(obj);
//                ctx.camera.pitch(r.nextInt(50)+50);
//                return;
//            }
//            if (new Random().nextBoolean()) {
//                ctx.camera.turnTo(obj);
//                ctx.camera.pitch(r.nextInt(50)+50);
//                return;
//            }
        }

        public void APrandomTurn() {
            if (r.nextBoolean()) return;
            new Thread(()->ctx.camera.pitch(r.nextInt(50)+50)).start();
            if (r.nextBoolean()) new Thread(() -> ctx.camera.angle(r.nextInt(300) + 60)).start();
        }

        public void APmouseOffScreen() {
            Condition.sleep();
            switch (random(0, 3)) {
                case 0: // To Top
                    ctx.input.move(random(0, ctx.game.dimensions().getWidth()-1),0);
                    break;
                case 1: // To Bottom
                    ctx.input.move(random(0, ctx.game.dimensions().getWidth()-1),
                            (int) (ctx.game.dimensions().getHeight()-1));
                    break;
                case 2: // To Left
                    ctx.input.move(0,
                            random(0, ctx.game.dimensions().getHeight()-1));
                    break;
                case 3: // To Right
                    ctx.input.move((int) ctx.game.dimensions().getWidth()-1,
                            random(0, ctx.game.dimensions().getHeight()-1));
                    break;
            }
        }

        public void APmouseRandom() {
            Condition.sleep();
            switch (random(0, 5)) {
                case 0: // To Top right
                    ctx.input.move(
                            random(ctx.game.dimensions().getWidth()/2, ctx.game.dimensions().getWidth()-1),
                            random(0, ctx.game.dimensions().getHeight()/2));
                    break;
                case 1: // To Bottom right
                    ctx.input.move(
                            random(ctx.game.dimensions().getWidth()/2, ctx.game.dimensions().getWidth()-1),
                            random(ctx.game.dimensions().getHeight()/2, ctx.game.dimensions().getHeight()-1));
                    break;
                case 2: // To Top left
                    ctx.input.move(
                            random(0, ctx.game.dimensions().getWidth()/2),
                            random(0, ctx.game.dimensions().getHeight()/2));
                    break;
                case 3: // To Bottom left
                    ctx.input.move(
                            random(0, ctx.game.dimensions().getWidth()/2),
                            random(ctx.game.dimensions().getHeight()/2, ctx.game.dimensions().getHeight()-1));
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
        }

        private int random(double a, double b){
            int min = (int)a;
            int max = (int)b;
            return r.nextInt((max-min)+1)+min;
        }
    }

}

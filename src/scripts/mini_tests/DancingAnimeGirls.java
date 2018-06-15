package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Script.Manifest(name = "ControllerPause", properties = "client=4;", description = "")
public class DancingAnimeGirls extends PollingScript<ClientContext> implements PaintListener {
    private ArrayList<BufferedImage> images = new ArrayList<>();

    @Override
    public void start() {
        super.start();
        try {
            images = getFrames(ctx.controller.script().download("http://i.imgur.com/pgaYOTs.gif","lol.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void poll() {
    }

    private int counter = 0;
    @Override
    public void repaint(Graphics g) {
        if (!images.isEmpty()) {
            BufferedImage img = images.get(counter % images.size());
            g.drawImage(img, Utils.chatboxTopLeft().x, Utils.chatboxTopLeft().y - img.getHeight(), null);
            counter++;
        }
    }

    //    https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage#16234122
    public ArrayList<BufferedImage> getFrames(File gif) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
        ir.setInput(ImageIO.createImageInputStream(gif));
        for(int i = 0; i < ir.getNumImages(true); i++)
            frames.add(ir.read(i));
        return frames;
    }
}
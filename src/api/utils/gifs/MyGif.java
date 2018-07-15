package api.utils.gifs;

import api.ClientContext;
import api.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class MyGif {
    private ArrayList<BufferedImage> frames = new ArrayList<>();
    private int index = 0;
    private int skip = 0;
    private final int skipFrames = 2;

    public MyGif(ClientContext ctx, String url) {
        getFrames(ctx, url);
    }

    public MyGif(ClientContext ctx) {
        getFrames(ctx, urls.get(new Random().nextInt(1)));
    }

    private void getFrames(ClientContext ctx, String url) {
        new Thread(()->{
            try {
                ArrayList<BufferedImage> defaultImages = ImageUtils.getFrames(ctx.controller.script().download(url, String.valueOf(System.currentTimeMillis())));
                for (BufferedImage i : defaultImages) {
                    frames.add(ImageUtils.scaleImageHeight(150,i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public BufferedImage nextFrame() {
        if (frames.isEmpty()) return null;
        skip = (skip+1)%skipFrames;
        if (skip == 0) index = (index+1)%frames.size();
        return frames.get(index);
    }

    private ArrayList<String> urls = new ArrayList<>(Arrays.asList(
            "http://i.imgur.com/mqVR0VL.gif", //megumin
            "http://i.imgur.com/eTmS1Jl.gif" //rem
    ));

    /**
     * Insert coordinates of bottom left corner
     * @param g
     * @param x
     * @param y
     */
    public void draw(Graphics g, int x, int y) {
        BufferedImage b = nextFrame();
        if (b == null) return;
        g.drawImage(b, x, y - b.getHeight(), null);
    }
}

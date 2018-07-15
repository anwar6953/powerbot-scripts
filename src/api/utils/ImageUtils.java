package api.utils;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtils {

    public static ArrayList<BufferedImage> getFrames(File gif) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
        ir.setInput(ImageIO.createImageInputStream(gif));
        for(int i = 0; i < ir.getNumImages(true); i++)
            frames.add(ir.read(i));
        return frames;
    }

    public static BufferedImage scaleImage(int width, int height, BufferedImage bi) {
        return toBufferedImage(bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH));
    }

    public static BufferedImage scaleImageHeight(int height, BufferedImage bi) {
        double scale = (double)height/(double)bi.getHeight();
        return scaleImage(scale,bi);
    }

    public static BufferedImage scaleImage(double scale, BufferedImage bi) {
        int width = (int)(scale*bi.getWidth());
        int height = (int)(scale*bi.getHeight());
        return toBufferedImage(bi.getScaledInstance(width, height, BufferedImage.SCALE_AREA_AVERAGING));
    }

//    public static BufferedImage scaleImage(double scale, BufferedImage bi) {
//        int width = (int)(scale*bi.getWidth());
//        int height = (int)(scale*bi.getHeight());
//        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g = resized.createGraphics();
//        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g.drawImage(bi, 0, 0, width, height, 0, 0, bi.getWidth(),
//                bi.getHeight(), null);
//        g.dispose();
//        return resized;
//    }

    //https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}

package tiny.engine.utils;

import java.awt.*;
import java.awt.image.*;

/**
 * @author Damian Strain
 */
public final class ImageUtils {

    /**
     * Creates and returns a BufferImage of the specified width and height that
     * is compatible with the current graphics configuration.
     *
     * @param width The width of the returned image
     * @param height The height of the returned image
     * @return A compatible BufferedImage image
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsConfiguration gfx = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        return gfx.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    /**
     * Optimizes and returns the specified BufferedImage to be compatible with
     * the current graphics configuration.
     *
     * @param image The BufferedImage to optimize
     * @return The optimized BufferedImage for your graphics configuration
     */
    public static BufferedImage toCompatibleImage(BufferedImage image) {
        // Obtain the current system graphical settings
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        /*
         * If the image is already compatible and optimized for the
         * current system settings, simply return it.
         */
        if (image.getColorModel().equals(gfxConfig.getColorModel())) {
            return image;
        }

        // The image is not optimized, so create a new image that is
        BufferedImage optimizedImg = gfxConfig.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

        // Get the sprite context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) optimizedImg.getGraphics();

        // Actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Return the new optimized image
        return optimizedImg;
    }

    /**
     * Returns an array of BufferImages for the specified image. The image is
     * split according to the specified number of columns and rows. For example,
     * if your sprite sheet contained six images in a row, then you would pass
     * in six columns and one row as the argument.
     *
     * @param spriteSheet The sprite sheet to split
     * @param cols The number of columns to split the sprite sheet into
     * @param rows The number of rows to split the sprite sheet into
     * @return An array of BufferImages
     */
    public static BufferedImage[] splitSpriteSheet(BufferedImage spriteSheet, int cols, int rows) {
        int w = spriteSheet.getWidth() / cols;
        int h = spriteSheet.getHeight() / rows;

        int num = 0;

        BufferedImage images[] = new BufferedImage[cols * rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (num == images.length) {
                    break;
                }

                images[num] = ImageUtils.createCompatibleImage(w, h);

                // Tell the sprite to draw only one block of the image
                Graphics2D g = images[num].createGraphics();
                g.drawImage(spriteSheet, 0, 0, w, h, w * x, h * y, w * x + w, h * y + h, null);
                g.dispose();
                num++;
            }
        }
        return images;
    }

    /**
     * Alters the specified Color's transparency to the specified alpha value.
     *
     * @param color The Color to replace.
     * @param newAlphaValue A hex-value of the new alpha value.
     */
    public void invokeTransparency(BufferedImage sprite, Color color, int newAlphaValue) {
        sprite = makeTransparent(sprite, color, newAlphaValue);

//        if (this.cols > 0 & this.rows > 0)
//        {
//            splitSpriteSheet(this.cols, this.rows);
//        }
    }

    private static BufferedImage makeTransparent(BufferedImage sprite, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(sprite.getSource(), filter);
        Image temp = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage bufImg = createCompatibleImage(sprite.getWidth(), sprite.getHeight());
        Graphics2D g = bufImg.createGraphics();
        g.drawImage(temp, 0, 0, null);
        g.dispose();

        return bufImg;
    }

    private static BufferedImage makeTransparent(BufferedImage img, final Color color, final int newColor) {
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return newColor & rgb;
                } else {
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        Image temp = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage bufImg = createCompatibleImage(img.getWidth(), img.getHeight());
        Graphics2D g = bufImg.createGraphics();
        g.drawImage(temp, 0, 0, null);
        g.dispose();

        return bufImg;
    }

    public static BufferedImage duplicateAndReverse(BufferedImage sprite) {
        BufferedImage temp = createCompatibleImage(sprite.getWidth() * 2, sprite.getHeight());

        int w = sprite.getWidth();
        int h = sprite.getHeight();

        Graphics2D g = temp.createGraphics();

        g.drawImage(sprite, 0, 0, null);
        g.drawImage(sprite, w, 0, w * 2, h, w, 0, 0, h, null);
        g.dispose();

        return temp;
    }
}

package tiny.engine.entity;

import java.awt.*;
import java.awt.image.*;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * You may use this sprite-class (or parts of it) in any way you want, as long
 * as you don't remove this notice and give me credit for my work.
 * <p/>
 * The only thing I didn't make myself is the splitImage(); method, which I
 * found (and copied) from: http://www.javalobby.org/articles/ultimate-image/#13
 * <p/>
 * The reference to the BufferedImage you use in the constructor is not kept
 * since the Sprite creates it's own optimized BufferedImage.
 *
 * @author Captain Awesome
 * (http://www.javagaming.org/index.php?action=profile;u=28320)
 */
public class AnimatedSprite implements Cloneable, Serializable {

    //For serialization
    private static final long serialVersionUID = 1L;

    private transient BufferedImage spriteImg;
    private transient BufferedImage[] animImg;

    private int x;
    private int y;

    private int refX;
    private int refY;

    private int frameSequence[] = {0};
    private int currentFrame = 0;
    private int sleepTime;
    private int currentSleepFrame;

    private boolean runAnim = false;

    private int cols = 1;
    private int rows = 1;

    private String frameSequenceName = "ORIG";

    private Rectangle bounds;

    public AnimatedSprite(BufferedImage img) {
        spriteImg = toCompatibleImage(img);
    }

    /**
     * Splits the image to create an animation
     */
    private static BufferedImage[] splitImage(BufferedImage img, int cols, int rows) {
        int w = img.getWidth() / cols;
        int h = img.getHeight() / rows;

        int num = 0;

        BufferedImage imgs[] = new BufferedImage[cols * rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (num == imgs.length) {
                    break;
                }
                imgs[num] = createCompatibleImage(w, h);

                // Tell the graphics to draw only one block of the image
                Graphics2D g = imgs[num].createGraphics();
                g.drawImage(img, 0, 0, w, h, w * x, h * y, w * x + w, h * y + h, null);
                g.dispose();
                num++;
            }
        }
        return imgs;
    }

    //Creates a BufferedImage that is optimized for this system.
    private static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsConfiguration gfx = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        return gfx.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    private static BufferedImage toCompatibleImage(BufferedImage image) {
        //Create a new compatible image
        BufferedImage bimg = createCompatibleImage(image.getWidth(), image.getHeight());

        //Get the graphics of the image and paint the original image onto it.
        Graphics2D g = (Graphics2D) bimg.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        //Return the new, compatible image.
        return bimg;
    }

    private static boolean intersection(Rectangle r, Rectangle d) {
        int rect1x = r.x;
        int rect1y = r.y;
        int rect1w = r.width;
        int rect1h = r.height;

        int rect2x = d.x;
        int rect2y = d.y;
        int rect2w = d.width;
        int rect2h = d.height;

        return (rect1x + rect1w >= rect2x
                && rect1y + rect1h >= rect2y
                && rect1x <= rect2x + rect2w
                && rect1y <= rect2y + rect2h);
    }

    private static BufferedImage makeTransparent(BufferedImage img, final Color color) {
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
        ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        Image temp = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage bufImg = createCompatibleImage(img.getWidth(), img.getHeight());
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

    private static BufferedImage duplicateAndReverse(BufferedImage bimg) {
        BufferedImage temp = createCompatibleImage(bimg.getWidth() * 2, bimg.getHeight());

        int w = bimg.getWidth();
        int h = bimg.getHeight();

        Graphics2D g = temp.createGraphics();

        g.drawImage(bimg, 0, 0, null);
        g.drawImage(bimg, w, 0, w * 2, h, w, 0, 0, h, null);
        g.dispose();

        return temp;
    }

    @Override
    public AnimatedSprite clone() {
        try {
            return (AnimatedSprite) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Clone failed.");
            return null;
        }
    }

    /**
     * Starts the animation of the sprite. The user must then call
     * continueAnimation() in order to animate the sprite.
     */
    public void setAnimation(int sleep) {
        sleepTime = sleep;
        currentSleepFrame = 0;
        runAnim = true;
    }

    /**
     * @return true if this Sprite has an animation set, otherwise false.
     */
    public boolean isAnimating() {
        return runAnim;
    }

    /*
     * Stops the animation
     */
    public void stopAnimation() {
        runAnim = false;
    }

    /**
     * Paints the sprite. If splitSprite has been used, it will paint the
     * current frame.
     */
    public void paint(Graphics g) {
        g.drawImage(this.getImage(), this.getRealX(), this.getRealY(), null);
    }

    /**
     * Paints the specified frame at the specified position. Ignores reference
     * pixels.
     *
     * @param g The Graphics used to paint this Sprite on.
     * @param frame The frame you wish to paint.
     * @param x The x-position to paint the frame at.
     * @param y The y-position to paint the frame at.
     */
    public void paintFrame(Graphics g, int frame, int x, int y) {
        g.drawImage(animImg[frameSequence[frame]], x, y, null);
    }

    /**
     * Continues the animation if setAnimation() has been used.
     */
    public void continueAnimation() {
        if (isAnimating() && currentSleepFrame >= sleepTime) {
            currentSleepFrame = 0;
            this.nextFrame();
        } else {
            currentSleepFrame++;
        }
    }

    /**
     * Paints the original Sprite even if the Sprite already has been split.
     */
    public void paintOrig(Graphics g) {
        g.drawImage(spriteImg, x, y, null);
    }

    /**
     * Sets the position based on the parameters
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Defines which reference pixel (i.e where the image will be placed on the
     * x/y coordinates)
     *
     * @param x The x position of the reference pixel.
     * @param y The y-position of the reference pixel.
     */
    public void setRefPixel(int x, int y) {
        refX = x;
        refY = y;
    }

    /**
     * Splits this sprite into an array based on the parameters. Used to create
     * animations.
     *
     * @param cols The amount of columns to split the Sprite into.
     * @param rows The amount of rows to split the Sprite into.
     */
    public void splitSprite(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        animImg = splitImage(spriteImg, cols, rows);
        frameSequence = new int[animImg.length];

        for (int i = 0; i < animImg.length; i++) {
            frameSequence[i] = i;
        }
    }

    /**
     * Sets a new framesequence.
     *
     * @param sequence The new sequence.
     * @param name The name of this framesequence.
     */
    public void setFrameSequence(int[] sequence, String name) {
        frameSequence = sequence;
        currentFrame = 0;
        frameSequenceName = name;
    }

    /**
     * @return the name of the current framesequence.
     */
    public String getFrameSequence() {
        return frameSequenceName;
    }

    /**
     * Sets a new framesequence. The name of the framesequence will be set to
     * undefined.
     *
     * @param sequence The new sequence.
     */
    public void setFrameSequence(int[] sequence) {
        this.setFrameSequence(sequence, "UNDEFINED");
    }

    /**
     * @return the current framesequence.
     */
    public int[] getFrames() {
        return this.frameSequence;
    }

    /**
     * Manually continues the animation to the next frame.
     */
    public void nextFrame() {
        currentFrame++;
        if (currentFrame >= frameSequence.length) {
            currentFrame = 0;
        }
    }

    /**
     * @return the current frame.
     */
    public int getFrame() {
        return currentFrame;
    }

    /**
     * Manually sets the current frame.
     *
     * @param frame The frame to set this Sprite to.
     */
    public void setFrame(int frame) {
        currentFrame = frame;
    }

    /**
     * @return the max amount of frames if this Sprite has been split, otherwise
     * 1.
     */
    public int getSize() {
        if (animImg != null) {
            return animImg.length;
        } else {
            return 1;
        }
    }

    /**
     * Checks if a Sprite is colliding with another Sprite.
     *
     * @param otherSprite The Sprite to check a collission with.
     * @param pixelPerfect If true, it will use a pixel-perfect algorithm. If
     * false, it only checks its bounding box.
     * @return true if the Sprites collide, otherwise false.
     */
    public boolean collidesWith(AnimatedSprite otherSprite, boolean pixelPerfect) {
        boolean isColliding = false;

        Rectangle r1 = this.getBounds();
        Rectangle r2 = otherSprite.getBounds();

        r1.intersection(r2);

        if (intersection(r1, r2)) {
            if (pixelPerfect) {
                isColliding = pixelPerfectCollision(otherSprite, r1, r2);
            } else {
                isColliding = true;
            }
        }

        return isColliding;
    }

    /*
     *  pixelPerfectCollision(); first determines the area where the sprites collides
     *  AKA the collision-rectangle. It then grabs the pixels from both sprites
     *  which are inside the rectangle. It then checks every pixel from the arrays
     *  given by grabPixels();, and if 2 pixels at the same position are opaque,
     *  (alpha value over 0) it will return true. Otherwise it will return false.
     */
    private boolean pixelPerfectCollision(AnimatedSprite sprite, Rectangle r1, Rectangle r2) {
        /*
         * Get the X-values and Y-values for the two coordinates where the sprites collide
         */
        int cornerTopX = (r1.x > r2.x) ? r1.x : r2.x;
        int cornerBottomX = ((r1.x + r1.width) < (r2.x + r2.width)) ? (r1.x + r1.width) : (r2.x + r2.width);

        int cornerTopY = (r1.y > r2.y) ? r1.y : r2.y;
        int cornerBottomY = ((r1.y + r1.height) < (r2.y + r2.height)) ? (r1.y + r1.height) : (r2.y + r2.height);

        //Determine the width and height of the collision rectangle
        int width = cornerBottomX - cornerTopX;
        int height = cornerBottomY - cornerTopY;

        //Create arrays to hold the pixels
        int[] pixels1 = new int[width * height];
        int[] pixels2 = new int[width * height];

        //Create the pixel grabber and fill the arrays
        PixelGrabber pg1 = new PixelGrabber(getImage(), cornerTopX - getRealX(), cornerTopY - getRealY(), width, height, pixels1, 0, width);
        PixelGrabber pg2 = new PixelGrabber(sprite.getImage(), cornerTopX - sprite.getRealX(), cornerTopY - sprite.getRealY(), width, height, pixels2, 0, width);

        //Grab the pixels
        try {
            pg1.grabPixels();
            pg2.grabPixels();
        } catch (InterruptedException ex) {
            Logger.getLogger(AnimatedSprite.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Check if pixels at the same spot from both arrays are not transparent.
        for (int i = 0; i < pixels1.length; i++) {
            int a = (pixels1[i] >>> 24) & 0xff;
            int a2 = (pixels2[i] >>> 24) & 0xff;

            /* Awesome, we found two pixels in the same spot that aren't
             * completely transparent! Thus the sprites are colliding!
             */
            if (a > 0 && a2 > 0) {
                return true;
            }

        }

        return false;
    }

    /**
     * Makes the specified Color completely transparent.
     *
     * @param color The Color to make transparent.
     */
    public void invokeTransparency(Color color) {
        spriteImg = makeTransparent(spriteImg, color);

        if (this.cols > 0 & this.rows > 0) {
            this.splitSprite(this.cols, this.rows);
        }

    }

    /**
     * Alters the specified Color's transparency to the specified alpha value.
     *
     * @param color The Color to replace.
     * @param newAlphaValue A hex-value of the new alpha value.
     */
    public void invokeTransparency(Color color, int newAlphaValue) {
        spriteImg = makeTransparent(spriteImg, color, newAlphaValue);
        if (this.cols > 0 & this.rows > 0) {
            this.splitSprite(this.cols, this.rows);
        }
    }

    /**
     * Returns the width of the current sprite
     */
    public int getWidth() {
        return this.getImage().getWidth();
    }

    /**
     * Returns the height of the sprite
     */
    public int getHeight() {
        return this.getImage().getHeight();
    }

    /**
     * @return the current x-position of this Sprite.
     */
    public int getX() {
        return x;
    }

    /**
     * @return the current x-position of the reference pixel of this Sprite.
     */
    public int getRefX() {
        return refX;
    }

    /**
     * @return the current x-position of the top-left corner of this Sprite.
     */
    public int getRealX() {
        return x - refX;
    }

    /**
     * @return the current y-position of this Sprite.
     */
    public int getY() {
        return y;
    }

    /**
     * @return the current y-position of the reference pixel of this Sprite.
     */
    public int getRefY() {
        return refY;
    }

    /**
     * @return the current y-position of the top-left corner of this Sprite.
     */
    public int getRealY() {
        return y - refY;
    }

    /**
     * Returns the boundaries for the sprite, used for collision detection
     */
    public Rectangle getBounds() {
        if (this.bounds == null) {
            this.bounds = new Rectangle(this.getRealX(), this.getRealY(), this.getWidth(), this.getHeight());

            return this.bounds;
        }

        this.bounds.setBounds(this.getRealX(), this.getRealY(), this.getWidth(), this.getHeight());

        return this.bounds;
    }

    /**
     * @return the image of the current frame if it has been split, otherwise
     * the whole image.
     */
    public BufferedImage getImage() {
        if (animImg != null && currentFrame < frameSequence.length) {
            return animImg[frameSequence[currentFrame]];
        } else {
            return spriteImg;
        }
    }

    /**
     * @return the whole image even if this Sprite has been split.
     */
    public BufferedImage getOrigImage() {
        return spriteImg;
    }

    /**
     * Flips this sprite horizontally.
     */
    public void flipHorizontal() {
        int w = this.getOrigImage().getWidth();
        int h = this.getOrigImage().getHeight();

        BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bimg.createGraphics();

        g.drawImage(this.getOrigImage(), 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();

        this.spriteImg = toCompatibleImage(bimg);

        if (this.rows > 0 & this.cols > 0) {
            animImg = splitImage(spriteImg, cols, rows);
        }
    }

    /**
     * Flips this sprite horizontally.
     */
    public void flipVertical() {
        int w = this.getOrigImage().getWidth();
        int h = this.getOrigImage().getHeight();

        BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bimg.createGraphics();

        g.drawImage(this.getOrigImage(), 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();

        this.spriteImg = toCompatibleImage(bimg);

        if (this.rows > 0 & this.cols > 0) {
            animImg = splitImage(spriteImg, cols, rows);
        }
    }

    /**
     * Re-builds the current Sprite after it has been de-serialized.
     *
     * @param img The BufferedImage to rebuild the Sprite with.
     */
    public void reloadSprite(BufferedImage img) {
        this.spriteImg = img;
        if (this.rows > 0 & this.cols > 0) {
            animImg = splitImage(spriteImg, cols, rows);
        }
    }

    /**
     * @return the columns used to split this Sprite with.
     */
    public int getCols() {
        return this.cols;
    }

    /**
     * @return the columns used to split this Sprite with.
     */
    public int getRows() {
        return this.rows;
    }
}

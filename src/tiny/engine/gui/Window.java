package tiny.engine.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The Window class represents the main game window. Use this class to set up
 * your main window with a Canvas for rendering.
 *
 * @author Damian Strain
 */
public final class Window {

    private Dimension size = null;

    private JFrame frame = null;
    private Canvas canvas = null;

    private boolean hasGameCanvas = false;
    private boolean isFullScreen = false;

    /**
     * Initialises a new window with a title, size and whether it is re-sizable.
     *
     * @param title
     * @param isResizable
     * @param width
     * @param height
     */
    public void init(String title, boolean isResizable, int width, int height) {
        frame = new JFrame();
        frame.setIgnoreRepaint(true);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.setBackground(Color.BLACK);

        size = new Dimension();

        this.setTitle(title);
        this.setResizable(isResizable);
        this.setSize(width, height);
        this.addGameCanvas(canvas);
    }

    /**
     * Sets the window's title.
     *
     * @param title the window title to set
     */
    public void setTitle(String title) {
        if (title == null || title.length() == 0) {
            title = "TinyEngine";
        }
        frame.setTitle(title);
    }

    /**
     * Sets whether the window can be re-sized.
     *
     * @param isResizable true if re-sizable, false otherwise
     */
    public void setResizable(boolean isResizable) {
        frame.setResizable(isResizable);
    }

    /**
     * Sets the size of the window in pixels.
     *
     * @param width the width of the window
     * @param height the height of the window
     */
    public void setSize(int width, int height) {
        size.setSize(width, height);

        canvas.setSize(size);
        canvas.setMaximumSize(size);
        canvas.setMinimumSize(size);
        canvas.setPreferredSize(size);
    }

    /**
     * Sets the background color of the window.
     *
     * @param color the color to set for the background
     */
    public void setBackgroundColor(Color color) {
        canvas.setBackground(color);
    }

    /**
     * Adds a canvas to the window for game rendering.
     *
     * @param canvas the canvas to use for rendering
     */
    public void addGameCanvas(Canvas canvas) {
        if (!hasGameCanvas) {
            frame.add(canvas);
            hasGameCanvas = true;
        }
    }

    /**
     * Removes the canvas from the window if one has been set.
     *
     * @param canvas the canvas to remove
     */
    public void removeGameCanvas(Canvas canvas) {
        if (hasGameCanvas) {
            frame.remove(canvas);
            frame.invalidate();
            frame.validate();
            hasGameCanvas = false;
        }
    }

    /**
     * Returns whether this window is currently in full screen mode.
     *
     * @return true if currently full screen, false otherwise
     */
    public boolean isFullScreen() {
        return isFullScreen;
    }

    /**
     * Sets whether this window should be in full screen mode or not.
     *
     * @param isFullScreen true if this window should be in full screen mode,
     * false otherwise
     */
    public void setFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            // Go into full screen mode
        } else {
            // Exit full screen mode
        }
        this.isFullScreen = isFullScreen;
    }

    /**
     * Returns whether this window is currently visible.
     *
     * @return true if currently visible, false otherwise
     */
    public boolean isVisible() {
        return frame.isVisible();
    }

    /**
     * Sets whether this window should be displayed or not.
     *
     * @param isVisible true if this window should be displayed, false otherwise
     */
    public void setVisible(boolean isVisible) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(isVisible);
    }

    /**
     * Sets a menu bar on this window.
     *
     * @param menuBar the menu bar to set
     */
    public void setMenuBar(JMenuBar menuBar) {
        frame.setJMenuBar(menuBar);
    }

    /**
     * Returns the underlying frame of this window as a JFrame.
     *
     * @return the window's frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns the game canvas attached to this window.
     *
     * @return the window's canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
}

package tiny.engine.utils;

import javax.swing.*;
import java.awt.*;

/**
 * The DebugUtils provides convenience methods that provides information the
 * state of the game engine. You can display the current frames per second
 * (fps), or memory usage.
 *
 * @author Damian Strain
 */
public final class DebugUtils {

    private static int frameCount;
    private static long lastCount;     // Last time the fps was counted
    private static int currentFPS;    // The real fps achieved

    /**
     * Displays the current frame rate in frames per second (fps).
     *
     * @param g2d the Graphics context to render with
     */
    public static void showCurrentFps(Graphics2D g2d) {
        calculateFps();
        g2d.setColor(Color.CYAN);
        g2d.drawString("Fps: " + currentFPS, 20, 30);
    }

    /**
     * This method calculates the frame rate of the game in frames per second
     * (fps). This method should be called in either your update method, or draw
     * method depending on the game loop chosen.
     */
    private static void calculateFps() {
        frameCount++;
        if (System.currentTimeMillis() - lastCount > 1000L) {
            lastCount = System.currentTimeMillis();
            currentFPS = frameCount;
            frameCount = 0;
        }
    }

    /**
     * This method resets the frame count to zero, and sets the timer to the
     * current time.
     */
    public static void resetFps() {
        frameCount = 0;
        lastCount = System.currentTimeMillis();
    }

    /**
     * Displays the games current memory usage in megabytes (MB)
     *
     * @param g2d the Graphics context to render with
     */
    public static void showMemoryUsage(Graphics2D g2d) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long currentMemory = totalMemory - runtime.freeMemory();
        String memoryUsage = ((float) ((currentMemory * 10) >> 20) / 10) + " of " + ((float) ((totalMemory * 10) >> 20) / 10) + " MB";

        g2d.drawString("Memory: " + memoryUsage, 20, 50);
    }

    /**
     * Displays which loop the game is currently using.
     *
     * @param g2d the Graphics context to render with
     */
    public static void showLoopType(Graphics2D g2d, boolean isFixedStep) {
        String loopType = null;

        if (isFixedStep) {
            loopType = "Fixed";
        } else {
            loopType = "Variable";
        }
        g2d.drawString("Loop: " + loopType, 20, 70);
    }

    /**
     * This method determines whether it is called on the Event Dispatch Thread
     * (EDT) or not. Call this method anywhere in your class or method and the
     * result is output to the console.
     *
     * @param methodOrClassName The method or class name this method is called
     * from
     */
    public static void isEventDT(String methodOrClassName) {
        if (SwingUtilities.isEventDispatchThread()) {
            System.out.println(methodOrClassName + " is ON the EDT thread");
        } else {
            System.out.println(methodOrClassName + " is NOT on the EDT thread");
        }
    }
}

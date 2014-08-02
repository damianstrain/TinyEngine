package tiny.engine.utils;

import java.awt.*;

/**
 * The GraphicsUtil class provides methods that returns information or objects
 * specific to the graphics configuration of the host machine.
 *
 * @author Damian Strain
 * @version 1.0
 */
public final class GraphicsUtils {

    /**
     * Returns the default screen device.
     *
     * @return The default screen device
     */
    public static GraphicsDevice getGFXDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    /**
     * Returns the display mode for the default screen device.
     *
     * @return The display mode
     */
    public static DisplayMode getDisplayMode() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
    }

    /**
     * Returns the refresh rate of the display in hertz (hz).
     *
     * @return The refresh rate
     */
    public static int getRefreshRate() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
    }
}

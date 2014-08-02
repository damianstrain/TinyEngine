package tiny.engine.core;

import tiny.engine.gui.About;
import tiny.engine.gui.MenuBar;
import tiny.engine.gui.Preferences;
import tiny.engine.gui.Window;
import tiny.engine.gui.setup.MacSetup;
import tiny.engine.utils.OsUtils;

import java.net.URL;

/**
 * The GameGui class encapsulates and provides access to the underlying gui. It
 * provides methods to setup and initialise the main gui, and retrieve the
 * elements that make up the main gui.
 *
 * @author Damian Strain
 */
public final class GameGui {

    private final About about;
    private final MenuBar menubar;
    private final Preferences prefs;
    private final Window window;

    /**
     * Constructs the main game gui. Once instantiated you must call the init()
     * method to initialise the gui.
     */
    public GameGui() {
        about = new About();
        menubar = new MenuBar();
        prefs = new Preferences();
        window = new Window();
    }

    /**
     * Initialises the main gui with a title and whether the main window is
     * resizable. The width and height parameters sets the size of the main
     * window.
     * <p/>
     * Optionally, you can pass in the URL for your game's icon, it's version
     * number, and copyright notice or game author. This information is shown in
     * the about dialog.
     *
     * @param title the window title to set
     * @param isResizable true if the window is resizable, false otherwise
     * @param width the width of the main window
     * @param height the height of the main window
     * @param iconURL the URL for your game's icon
     * @param version the version of your game
     * @param copyright the game's copyright notice or author
     */
    public void init(String title, boolean isResizable, int width, int height, URL iconURL, String version, String copyright) {
        if (OsUtils.isOsx()) {
            MacSetup.setScreenMenuBarName(title);
            MacSetup.setScreenMenuBar();
            MacSetup.setAquaBorders();
            MacSetup.initMenuHandlers(window, about, prefs);
            MacSetup.setDockIcon(iconURL);
            // MacSetup.enableFullScreenMode(window);
        }
        OsUtils.setLookAndFeel();

        about.init(iconURL, title, version, copyright);
        menubar.init();
        prefs.init();
        window.init(title, isResizable, width, height);
    }

    /**
     * Sets whether the gui should be displayed or not.
     *
     * @param isVisible true if the gui should be displayed, false otherwise
     */
    public void setVisible(boolean isVisible) {
        window.setVisible(isVisible);
    }

    /**
     * Returns the game's about dialog.
     *
     * @return the about dialog
     */
    public About getAbout() {
        return about;
    }

    /**
     * Returns the game's menu bar.
     *
     * @return the menu bar
     */
    public MenuBar getMenuBar() {
        return menubar;
    }

    /**
     * Returns the game's preferences dialog.
     *
     * @return the preferences dialog
     */
    public Preferences getPrefs() {
        return prefs;
    }

    /**
     * Returns the game's main window.
     *
     * @return the main window
     */
    public Window getWindow() {
        return window;
    }
}

package tiny.engine.gui.setup;

import com.apple.eawt.Application;
import com.apple.eawt.FullScreenUtilities;
import tiny.engine.gui.About;
import tiny.engine.gui.Preferences;
import tiny.engine.gui.Window;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Initialises settings specific to Mac OS X for better OS integration.
 *
 * @author Damian Strain
 */
public final class MacSetup {

    /**
     * Sets the title of the Mac OS X screen menu bar. If no title is set, then
     * a default title will be used.
     *
     * @param title the title to set for the Mac OS X screen menu bar
     */
    public static void setScreenMenuBarName(String title) {
        if (title == null || title.length() == 0) {
            title = "TinyEngine";
        }
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);
    }

    /**
     * Puts the Swing menu bar on the Mac OS X screen menu bar. Note that Swing
     * menu bars in dialogs are not moved to the Mac OS X menu bar.
     */
    public static void setScreenMenuBar() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    /**
     * Sets the Swing "Titled" and "Inset" borders to use Mac OSX native aqua
     * variant.
     */
    public static void setAquaBorders() {
        try {
            UIManager.put("TitledBorder.border", UIManager.getBorder("TitledBorder.aquaVariant"));
            UIManager.put("InsetBorder.border", UIManager.getBorder("InsetBorder.aquaVariant"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialises and registers the appropriate menu handlers for Mac OS X's
     * screen menu bar.
     *
     * @param window
     * @param about the about dialog to display
     * @param prefs the preferences dialog to display
     */
    public static void initMenuHandlers(Window window, About about, Preferences prefs) {
        new MacMenuHandler(window, about, prefs);
    }

    /**
     * Sets the dock icon to use. If no icon is set, then the default TinyEngine
     * icon will be used.
     *
     * @param url the url of the application icon to use
     */
    public static void setDockIcon(URL url) {
        if (url == null) {
            url = Application.getApplication().getClass().getResource("/icon.png");
        }
        Image icon = Toolkit.getDefaultToolkit().getImage(url);
        Application.getApplication().setDockIconImage(icon);
    }

    /**
     * Enables native Mac OS X Lion full screen support. If the OS X version is
     * < 10.7 this will be ignored.
     *
     * @param window the window to enable full screen mode for
     */
    public static void enableFullScreenMode(Window window) {
        // TODO: Do a version check before enabling full screen mode

        FullScreenUtilities.setWindowCanFullScreen(window.getFrame(), true);
        // Add full screen listener and implement methods
    }

    /**
     * Set dialogs that are to be modal, to the Mac OS X native document modal
     * "sheet" variant. This setting has no effect on other platforms.
     *
     * @param dialog the modal dialog to be set as a document modal sheet
     */
    public static void setDocumentModalDialog(JDialog dialog) {
        try {
            dialog.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
            dialog.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package tiny.engine.gui.setup;

import com.apple.eawt.*;
import tiny.engine.gui.About;
import tiny.engine.gui.Preferences;
import tiny.engine.gui.Window;

/**
 * The MacMenuHandler initialises and registers the required handlers in order
 * to make use of the Mac OS X screen menu bar.
 *
 * @author Damian Strain
 */
public final class MacMenuHandler implements AboutHandler, QuitHandler, PreferencesHandler {

    private final Window window;
    private final About about;
    private final Preferences prefs;

    /**
     * Constructs a new MacMenuHandler, registering the appropriate handlers for
     * the Mac OS X screen menu bar.
     *
     * @param about the about dialog to display
     * @param prefs the preferences dialog to display
     */
    public MacMenuHandler(Window window, About about, Preferences prefs) {
        this.window = window;
        this.about = about;
        this.prefs = prefs;

        Application.getApplication().setAboutHandler(this);
        Application.getApplication().setQuitHandler(this);
        Application.getApplication().setPreferencesHandler(this);
    }

    @Override
    public void handleAbout(AppEvent.AboutEvent aboutEvent) {
        about.setVisible(true);
    }

    @Override
    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
        quitResponse.performQuit();
    }

    @Override
    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
        prefs.setVisible(true);
    }
}

package tiny.engine.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The Preferences class contains and manages all settings related to
 * TinyEngine.
 *
 * @author Damian Strain
 */
public final class Preferences {
    //TODO: Implement the preferences window

    private JDialog dialog = null;

    /**
     * Initialises a new preferences window with no title or size.
     */
    public void init() {
        dialog = new JDialog();
        dialog.setTitle("Preferences");
        dialog.setResizable(false);
        dialog.setPreferredSize(new Dimension(280, 175));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Sets whether this preferences window should be displayed or not.
     *
     * @param isVisible true if this preferences window should be displayed,
     * false otherwise
     */
    public void setVisible(boolean isVisible) {
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(isVisible);
    }

    /**
     * Returns the underlying dialog of the preferences window as a JDialog.
     *
     * @return the preference window's dialog
     */
    public JDialog getDialog() {
        return dialog;
    }
}

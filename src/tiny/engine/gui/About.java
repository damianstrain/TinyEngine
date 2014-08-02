package tiny.engine.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * The About class displays a simple dialog containing information on TinyEngine
 * such as, the author, version, and copyright notice.
 *
 * @author Damian Strain
 */
public final class About {

    private JDialog dialog = null;

    /**
     * Initialises a new about dialog with no title or size.
     */
    public void init(URL icon, String name, String version, String copyright) {
        dialog = new JDialog();
        dialog.setTitle("About");
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(280, 175));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 9, 10));// top,left,bottom,right
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(getIcon(icon));
        content.add(getName(name));
        content.add(getVersion(version));
        content.add(getCopyright(copyright));

        dialog.setContentPane(content);
    }

    /**
     * Sets whether this about dialog should be displayed or not.
     *
     * @param isVisible true if this about dialog should be displayed, false
     * otherwise
     */
    public void setVisible(boolean isVisible) {
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(isVisible);
    }

    /**
     * Returns the underlying dialog of the about window as a JDialog.
     *
     * @return the about window's dialog
     */
    public JDialog getDialog() {
        return dialog;
    }

    /**
     * Returns the application icon to display in this about dialog.
     *
     * @return the application icon to display
     */
    private JLabel getIcon(URL iconURL) {
        if (iconURL == null) {
            iconURL = this.getClass().getResource("/gui/res/icon.png");
        }
        ImageIcon imageIcon = new ImageIcon(iconURL);
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setIcon(imageIcon);

        return label;
    }

    /**
     * Returns the title of the game to display in this about dialog.
     *
     * @return the game title to display
     */
    private JLabel getName(String name) {
        if (name == null || name.length() == 0) {
            name = "TinyEngine";
        }
        JLabel label = new JLabel(name);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 9, 10));// top,left,bottom,right

        return label;
    }

    /**
     * Returns the version of the game to display in this about dialog.
     *
     * @return the game version to display
     */
    private JLabel getVersion(String version) {
        if (version == null || version.length() == 0) {
            version = "Version 1.0.0";
        }
        JLabel label = new JLabel(version);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 10));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 9, 10));// top,left,bottom,right

        return label;
    }

    /**
     * Returns the copyright notice to display in this about dialog.
     *
     * @return the copyright notice to display
     */
    private JLabel getCopyright(String copyright) {
        if (copyright == null || copyright.length() == 0) {
            copyright = "© 2013 Damian Strain. All rights reserved.";
        }
        JLabel label = new JLabel(copyright);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 10));

        return label;
    }
}

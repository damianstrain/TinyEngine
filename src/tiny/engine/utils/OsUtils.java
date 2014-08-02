package tiny.engine.utils;

import javax.swing.*;

/**
 * The OsUtil class provides many convenience methods that provide information
 * on the host Operating System.
 *
 * @author Damian Strain
 */
public final class OsUtils {

    // All platform look and feels
    public static final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
    public static final String APPLE = "com.apple.laf.AquaLookAndFeel";
    public static final String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    public static final String NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    public static final String MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    public static final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    /**
     * Returns whether Mac OS X is the host Operating System.
     *
     * @return true if Mac OS X, false otherwise
     */
    public static boolean isOsx() {
        final String osName = System.getProperty("os.name");
        return osName.contains("OS X");
    }

    /**
     * Returns whether Microsoft Windows is the host Operating System.
     *
     * @return true if Microsoft Windows, false otherwise
     */
    public static boolean isWin() {
        final String osName = System.getProperty("os.name");
        return osName.contains("WIN");
    }

    /**
     * Sets the look and feel to the host systems look and feel.
     */
    public static void setLookAndFeel() {
        try {
            String laf = null;

            if (isOsx()) {
                laf = APPLE;
            } else if (isWin()) {
                laf = WINDOWS;
            } else {
                // Use Nimbus on Unix systems
                laf = NIMBUS;
            }
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method outputs the host system's environmental properties to the
     * console. The following is a list of the current properties that are
     * outputted depending on the host Operating System.
     * <p/>
     * 1: File separator 2: Line separator 3: Path separator 4: Java home 5:
     * Java vendor 6: Java vendor URL 7: Java version 8: Java classpath 9:
     * Operating System name 10: Operating System version 11: Operating System
     * architecture 12: Currently logged in user 13: User home 14: User
     * directory
     * <p/>
     * More may be added in the future.
     */
    public static void displaySystemProperties() {
        System.out.println("File separator  : " + System.getProperty("file.separator"));
        System.out.println("Line separator  : " + System.getProperty("line.separator"));
        System.out.println("Path separator  : " + System.getProperty("path.separator"));

        System.out.println();
        System.out.println("Java home       : " + System.getProperty("java.home"));
        System.out.println("Java vendor     : " + System.getProperty("java.vendor"));
        System.out.println("Java vendor url : " + System.getProperty("java.vendor.url"));
        System.out.println("Java version    : " + System.getProperty("java.version"));
        System.out.println("Java class path : " + System.getProperty("java.class.path"));

        System.out.println();
        System.out.println("Os name         : " + System.getProperty("os.name"));
        System.out.println("Os version      : " + System.getProperty("os.version"));
        System.out.println("Os architecture : " + System.getProperty("os.arch"));

        System.out.println();
        System.out.println("User name       : " + System.getProperty("user.name"));
        System.out.println("User home       : " + System.getProperty("user.home"));
        System.out.println("User directory  : " + System.getProperty("user.dir"));
    }
}

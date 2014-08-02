package tiny.engine.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A file loading location that searches somewhere on the class path.
 *
 * @author Damian Strain
 */
public final class FileSystemLocation implements Location {

    private final File root;

    /**
     * Constructs a new resource location based on the host file system.
     *
     * @param root the root of the file system to search
     */
    public FileSystemLocation(File root) {
        this.root = root;
    }

    /**
     * Returns the specified resource as an InputStream.
     *
     * @param ref the reference to the resource
     * @return an InputStream for the given reference
     */
    public InputStream getResourceAsStream(String ref) {
        try {
            File file = new File(root, ref);

            if (!file.exists()) {
                file = new File(ref);
            }
            return new FileInputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns the specified resource as a URL.
     *
     * @param ref the reference to the resource
     * @return a URL for the given reference
     */
    public URL getResource(String ref) {
        try {
            File file = new File(root, ref);

            if (!file.exists()) {
                file = new File(ref);
            }
            if (!file.exists()) {
                return null;
            }
            return file.toURI().toURL();
        } catch (IOException e) {
            return null;
        }
    }
}

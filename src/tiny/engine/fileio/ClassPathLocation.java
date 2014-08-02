package tiny.engine.fileio;

import java.io.InputStream;
import java.net.URL;

/**
 * A file location that searches the class path.
 *
 * @author Damian Strain
 */
public final class ClassPathLocation implements Location {

    /**
     * Returns the specified resource as an InputStream.
     *
     * @param ref the reference to the resource
     * @return an InputStream for the given reference
     */
    @Override
    public InputStream getResourceAsStream(String ref) {
        String classPathRef = ref.replace('\\', '/');
        return LoadFile.class.getClassLoader().getResourceAsStream(classPathRef);
    }

    /**
     * Returns the specified resource as a URL.
     *
     * @param ref the reference to the resource
     * @return a URL for the given reference
     */
    @Override
    public URL getResource(String ref) {
        String classPathRef = ref.replace('\\', '/');
        return LoadFile.class.getClassLoader().getResource(classPathRef);
    }
}

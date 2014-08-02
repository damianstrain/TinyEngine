package tiny.engine.fileio;

import java.io.InputStream;
import java.net.URL;

/**
 * @author Damian Strain
 */
public interface Location {

    /**
     * Returns the specified resource as an InputStream.
     *
     * @param ref the reference to the resource
     * @return an InputStream for the given reference
     */
    public InputStream getResourceAsStream(String ref);

    /**
     * Returns the specified resource as a URL.
     *
     * @param ref the reference to the resource
     * @return a URL for the given reference
     */
    public URL getResource(String ref);
}

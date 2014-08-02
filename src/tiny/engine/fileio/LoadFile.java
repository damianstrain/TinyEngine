package tiny.engine.fileio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple wrapper around file loading.
 *
 * @author Damian Strain
 */
public final class LoadFile {

    private final List<Location> locations = new ArrayList<>();

    /**
     * Constructs a new file loader instance and initialises the locations to
     * search for files and resources.
     */
    public LoadFile() {
        locations.add(new ClassPathLocation());
        locations.add(new FileSystemLocation(new File(".")));
    }

    /**
     * Adds a location that will be searched for resources.
     *
     * @param location a new location that will be searched
     */
    public void addResourceLocation(Location location) {
        locations.add(location);
    }

    /**
     * Removes a location that will no longer be searched for resources.
     *
     * @param location the location that will be removed from the search list
     */
    public void removeResourceLocation(Location location) {
        locations.remove(location);
    }

    /**
     * Removes all locations. No resources will be found until new locations
     * have been added.
     */
    public void removeAllResourceLocations() {
        locations.clear();
    }

    /**
     * Checks if a resource is available from any given resource location.
     *
     * @param ref a reference to the resource that should be checked
     * @return true if the resource can be located, false otherwise
     */
    public boolean resourceExists(String ref) {
        URL url;

        for (Location location : locations) {
            url = location.getResource(ref);
            if (url != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the specified resource as an InputStream.
     *
     * @param ref the reference to the resource
     * @return an InputStream for the given reference
     */
    public InputStream getResourceAsStream(String ref) {
        InputStream in = null;

        for (Location location : locations) {
            in = location.getResourceAsStream(ref);
            if (in != null) {
                break;
            }
        }

        if (in == null) {
            throw new RuntimeException("Resource not found: " + ref);
        }
        return new BufferedInputStream(in);
    }

    /**
     * Returns the specified resource as a URL.
     *
     * @param ref the reference to the resource
     * @return a URL for the given reference
     */
    public URL getResource(String ref) {
        URL url = null;

        for (Location location : locations) {
            url = location.getResource(ref);
            if (url != null) {
                break;
            }
        }

        if (url == null) {
            throw new RuntimeException("Resource not found: " + ref);
        }
        return url;
    }
}

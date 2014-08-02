package tiny.engine.core;

import tiny.engine.fileio.LoadFile;
import tiny.engine.fileio.ReadFile;
import tiny.engine.fileio.WriteFile;

/**
 * The GameFileIO class encapsulates and provides access to the underlying
 * fileIO component. It provides methods to retrieve file resources such as
 * text, properties, XML, or any other files from either the class path, or file
 * system.
 *
 * @author Damian Strain
 */
public final class GameFileIO {

    private final LoadFile load;
    private final ReadFile read;
    private final WriteFile write;

    /**
     * Constructs and initialises the fileIO component.
     */
    public GameFileIO() {
        load = new LoadFile();
        read = new ReadFile();
        write = new WriteFile();
    }

    /**
     * Returns a LoadFile object for retrieving file resources from the file
     * system or class path.
     *
     * @return a LoadFile object
     */
    public LoadFile loadFile() {
        return load;
    }

    /**
     * Returns a ReadFile object for parsing a file and returning it as one of
     * many data structures.
     *
     * @return a ReadFile object
     */
    public ReadFile readFile() {
        return read;
    }

    /**
     * Returns a WriteFile object which can be used to write data to a file such
     * as game save data.
     *
     * @return a WriteFile object
     */
    public WriteFile writeFile() {
        return write;
    }
}

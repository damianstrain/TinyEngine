package tiny.engine.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A simple wrapper around file writing.
 *
 * @author Damian Strain
 */
public final class WriteFile {

    /**
     * Saves the given text to the specified file.
     *
     * @param canonicalFilename like /Users/al/foo/bar.txt
     * @param text all the text you want to save to the file as one String.
     */
    public static void writeFile(String canonicalFilename, String text) {
        File file = new File(canonicalFilename);
        BufferedWriter out;

        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(text);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

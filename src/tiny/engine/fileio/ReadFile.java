package tiny.engine.fileio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * A simple wrapper around file reading.
 *
 * @author Damian Strain
 */
public final class ReadFile {

    /**
     * Reads the given InputStream and returns the contents as a String.
     *
     * @param inStream the resource to read
     * @return a String containing the file contents
     */
    public String readFileAsString(InputStream inStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder builder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * Reads the given InputStream and returns the contents as a List<String>.
     *
     * @param inStream the resource to read
     * @return a list containing the file contents
     */
    public List<String> readFileAsList(InputStream inStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        List<String> lineList = new ArrayList<>();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

    /**
     * Reads the given InputStream and returns it as a Properties object.
     *
     * @param inStream the resource to read
     * @return a properties object containing the file contents
     */
    public Properties readPropertiesFile(InputStream inStream) {
        Properties properties = new Properties();

        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Reads the given InputStream and returns the contents as a Map<String,
     * String>.
     *
     * @param inStream the resource to read
     * @param delimiter
     * @return a map containing the file contents
     */
    public Map<String, String> readPropertiesFileAsMap(InputStream inStream, String delimiter) {
        Map<String, String> map = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;
                }
                int delimiterPosition = line.indexOf(delimiter);
                String key = line.substring(0, delimiterPosition - 1).trim();
                String value = line.substring(delimiterPosition + 1).trim();
                map.put(key, value);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}

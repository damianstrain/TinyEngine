/*
 * Copyright (c) 2012, Finn Kuusisto
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *     
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package tiny.engine.audio;

import tiny.engine.audio.internal.*;
import tiny.engine.audio.internal.Mixer;

import javax.sound.sampled.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Audio is the main class of the Audio system. In order to use the Audio
 * system, it must be initialized. After that, Music and Sound objects can be
 * loaded and used. When finished with the Audio system, it must be shutdown.
 *
 * @author Finn Kuusisto
 * @version 1.1.0
 */
public final class Audio {

    /**
     * The internal format used by Audio.
     */
    public static final AudioFormat FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, // linear signed PCM
            44100, // 44.1kHz sampling rate
            16, // 16-bit
            2, // 2 channels fool
            4, // frame size 4 bytes (16-bit, 2 channel)
            44100, // same as sampling rate
            false // little-endian
    );

    // The system has only one mixer for both music and sounds
    private static Mixer mixer;

    // Need a line to the speakers
    private static SourceDataLine outLine;

    // See if the system has been initialised
    private static boolean isInitialised = false;

    // Auto-updater for the system
    private static UpdateRunner autoUpdater;

    // Counter for unique sound IDs
    private static int soundCount = 0;

    /**
     * Initialises the Audio system. This must be called before loading any
     * audio.
     */
    public void init() {
        if (Audio.isInitialised) {
            return;
        }

        // Try to open a line to the speakers
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, Audio.FORMAT);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Unsupported output format!");
            return;
        }

        Audio.outLine = tryGetLine();

        if (Audio.outLine == null) {
            System.err.println("Output line unavailable!");
            return;
        }

        // Start the line and finish initialization
        Audio.outLine.start();
        finishInit();
    }

    /**
     * Alternative function to initialise Audio which should only be used by
     * those very familiar with the Java Sound API. This function allows the
     * line that is used for sound playback to be opened on a specific Mixer.
     *
     * @param info the Mixer.Info representing the desired Mixer
     * @throws LineUnavailableException if a Line is not available from the
     * specified Mixer
     * @throws SecurityException if the specified Mixer or Line are unavailable
     * due to security restrictions
     * @throws IllegalArgumentException if the specified Mixer is not installed
     * on the system
     */
    public void init(javax.sound.sampled.Mixer.Info info) throws LineUnavailableException, SecurityException, IllegalArgumentException {
        if (Audio.isInitialised) {
            return;
        }

        // Try to open a line to the speakers
        javax.sound.sampled.Mixer mixer = AudioSystem.getMixer(info);
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, Audio.FORMAT);
        Audio.outLine = (SourceDataLine) mixer.getLine(lineInfo);
        Audio.outLine.open(Audio.FORMAT);

        // Start the line and finish initialization
        Audio.outLine.start();
        finishInit();
    }

    /**
     * Initialises the mixer and updater, and marks Audio as initialised.
     */
    private void finishInit() {
        // Now initialise the mixer
        Audio.mixer = new Mixer();

        // Initialise and start the updater
        Audio.autoUpdater = new UpdateRunner(Audio.mixer, Audio.outLine);
        Thread updateThread = new Thread(Audio.autoUpdater);

        try {
            updateThread.setDaemon(true);
            updateThread.setPriority(Thread.MAX_PRIORITY);
        } catch (Exception e) {
        }
        Audio.isInitialised = true;
        updateThread.start();

        // Yield to potentially give the updater a chance
        Thread.yield();
    }

    /**
     * Shutdown Audio.
     */
    public void shutdown() {
        if (!Audio.isInitialised) {
            return;
        }
        Audio.isInitialised = false;

        // Stop the auto-updater if running
        Audio.autoUpdater.stop();
        Audio.autoUpdater = null;
        Audio.outLine.stop();
        Audio.outLine.flush();
        Audio.mixer.clearMusic();
        Audio.mixer.clearSounds();
        Audio.mixer = null;
    }

    /**
     * Determine if Audio is initialised and ready for use.
     *
     * @return true if Audio is initialised, false if Audio has not been
     * initialized or has subsequently been shutdown
     */
    public boolean isInitialized() {
        return Audio.isInitialised;
    }

    /**
     * Get the global volume for all sound.
     *
     * @return the global volume for all sound, -1.0 if Audio has not been
     * initialized or has subsequently been shutdown
     */
    public double getGlobalVolume() {
        if (!Audio.isInitialised) {
            return -1.0;
        }
        return Audio.mixer.getVolume();
    }

    /**
     * Set the global volume. This is an extra multiplier, not a replacement,
     * for all Music and Sound volume settings. It starts at 1.0.
     *
     * @param volume the global volume to set
     */
    public void setGlobalVolume(double volume) {
        if (!Audio.isInitialised) {
            return;
        }
        Audio.mixer.setVolume(volume);
    }

    /**
     * Load a Music resource by name. The resource must be on the classpath for
     * this to work. This will store sound data in memory.
     *
     * @param name name of the Music resource
     * @return a Music resource as specified, null if not found/loaded
     */
    public Music loadMusic(String name) {
        return loadMusic(name, false);
    }

    /**
     * Load a Music resource by name. The resource must be on the classpath for
     * this to work.
     *
     * @param name name of the Music resource
     * @param streamFromFile true if this Music resource should be streamed from
     * a temporary file to reduce memory overhead
     * @return a Music resource as specified, null if not found/loaded
     */
    public Music loadMusic(String name, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }
        // Check for failure
        if (name == null) {
            return null;
        }
        // Check for correct naming
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        URL url = Audio.class.getResource(name);

        // Check for failure to find resource
        if (url == null) {
            System.err.println("Unable to find resource " + name + "!");
            return null;
        }
        return loadMusic(url, false);
    }

    /**
     * Load a Music resource by via a File. This will store sound data in
     * memory.
     *
     * @param file the Music file to load
     * @return a Music resource from a file as specified, null if not
     * found/loaded
     */
    public Music loadMusic(File file) {
        return loadMusic(file, false);
    }

    /**
     * Load a Music resource via a File.
     *
     * @param file the Music file to load
     * @param streamFromFile true if this Music should be streamed from a
     * temporary file to reduce memory overhead
     * @return a Music resource from a file as specified, null if not
     * found/loaded
     */
    public Music loadMusic(File file, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }

        // Check for failure
        if (file == null) {
            return null;
        }

        URL url = null;

        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            System.err.println("Unable to find file " + file + "!");
            return null;
        }
        return loadMusic(url, streamFromFile);
    }

    /**
     * Load a Music resource via a URL. This will store sound data in memory.
     *
     * @param url the URL of the Music resource
     * @return a Music resource from the URL as specified, null if not
     * found/loaded
     */
    public Music loadMusic(URL url) {
        return loadMusic(url, false);
    }

    /**
     * Load a Music resource via a URL.
     *
     * @param url the URL of the Music resource
     * @param streamFromFile true if this Music resource should be streamed from
     * a temporary file to reduce memory overhead
     * @return a Music resource from the URL as specified, null if not
     * found/loaded
     */
    public Music loadMusic(URL url, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }

        // Check for failure
        if (url == null) {
            return null;
        }

        // Get a valid stream of sound data
        AudioInputStream audioStream = getValidAudioStream(url);

        // Check for failure
        if (audioStream == null) {
            return null;
        }

        // Try to read all the bytes
        byte[][] data = readAllBytes(audioStream);

        // Check for failure
        if (data == null) {
            return null;
        }

        // Handle differently if streaming from a file
        if (streamFromFile) {
            StreamInfo info = createFileStream(data);

            // Check for failure
            if (info == null) {
                return null;
            }

            // Try to create it
            StreamMusic sm = null;
            try {
                sm = new StreamMusic(info.URL, info.NUM_BYTES_PER_CHANNEL, Audio.mixer);
            } catch (IOException e) {
                System.err.println("Failed to create StreamMusic!");
            }
            return sm;
        }
        // Construct the Music object and register it with the mixer
        return new MemMusic(data[0], data[1], Audio.mixer);
    }

    /**
     * Load a Sound resource by name. The resource must be on the classpath for
     * this to work. This will store sound data in memory.
     *
     * @param name name of the Sound resource
     * @return a Sound resource as specified, null if not found/loaded
     */
    public Sound loadSound(String name) {
        return loadSound(name, false);
    }

    /**
     * Load a Sound resource by name. The resource must be on the classpath for
     * this to work.
     *
     * @param name name of the Sound resource
     * @param streamFromFile true if this Music resource should be streamed from
     * a temporary file to reduce memory overhead
     * @return a Sound resource as specified, null if not found/loaded
     */
    public Sound loadSound(String name, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }

        // Check for failure
        if (name == null) {
            return null;
        }

        // Check for correct naming
        if (!name.startsWith("/")) {
            name = "/" + name;
        }

        URL url = Audio.class.getResource(name);

        // Check for failure to find resource
        if (url == null) {
            System.err.println("Unable to find resource " + name + "!");
            return null;
        }
        return loadSound(url, streamFromFile);
    }

    /**
     * Load a Sound resource via a File. This will store sound data in memory.
     *
     * @param file the Sound file to load
     * @return a Sound resource from the file as specified, null if not
     * found/loaded
     */
    public Sound loadSound(File file) {
        return loadSound(file, false);
    }

    /**
     * Load a Sound resource via a File.
     *
     * @param file the Sound file to load
     * @param streamFromFile true if this Music should be streamed from a
     * temporary file to reduce memory overhead
     * @return a Sound resource from the file as specified, null if not
     * found/loaded
     */
    public Sound loadSound(File file, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }

        // Check for failure
        if (file == null) {
            return null;
        }

        URL url = null;

        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            System.err.println("Unable to find file " + file + "!");
            return null;
        }
        return loadSound(url, streamFromFile);
    }

    /**
     * Load a Sound resource via a URL. This will store sound data in memory.
     *
     * @param url the URL of the Sound
     * @return a Sound resource from the URL as specified, null if not
     * found/loaded
     */
    public Sound loadSound(URL url) {
        return loadSound(url, false);
    }

    /**
     * Load a Sound resource via a URL. This will store sound data in memory.
     *
     * @param url the URL of the Sound
     * @param streamFromFile true if this Music resource should be streamed from
     * a temporary file to reduce memory overhead
     * @return a Sound resource from the URL as specified, null if not
     * found/loaded
     */
    public Sound loadSound(URL url, boolean streamFromFile) {
        // Check if the system is initialised
        if (!Audio.isInitialised) {
            System.err.println("Audio not initialized!");
            return null;
        }

        // Check for failure
        if (url == null) {
            return null;
        }

        // Get a valid stream of sound data
        AudioInputStream audioStream = getValidAudioStream(url);

        // Check for failure
        if (audioStream == null) {
            return null;
        }

        // Try to read all the bytes
        byte[][] data = readAllBytes(audioStream);

        // Check for failure
        if (data == null) {
            return null;
        }

        // Handle differently if streaming from file
        if (streamFromFile) {
            StreamInfo info = createFileStream(data);

            // Check for failure
            if (info == null) {
                return null;
            }

            // Try to create it
            StreamSound ss = null;

            try {
                ss = new StreamSound(info.URL, info.NUM_BYTES_PER_CHANNEL, Audio.mixer, Audio.soundCount);
                Audio.soundCount++;
            } catch (IOException e) {
                System.err.println("Failed to create StreamSound!");
            }
            return ss;
        }
        // Construct the Sound object
        Audio.soundCount++;
        return new MemSound(data[0], data[1], Audio.mixer, Audio.soundCount);
    }

    /**
     * Reads all of the bytes from an AudioInputStream.
     *
     * @param stream the stream to read
     * @return all bytes from the stream, null if error
     */
    private byte[][] readAllBytes(AudioInputStream stream) {
        // Left and right channels
        byte[][] data = null;
        int numChannels = stream.getFormat().getChannels();

        // Handle 1-channel (if) & 2-channel (else)
        if (numChannels == 1) {
            byte[] left = readAllBytesOneChannel(stream);

            // Check failure
            if (left == null) {
                return null;
            }
            data = new byte[2][];
            data[0] = left;
            data[1] = left; // Don't copy for the right channel
        } else if (numChannels == 2) {
            data = readAllBytesTwoChannel(stream);
        } else {
            System.err.println("Unable to read " + numChannels + " channels!");
        }
        return data;
    }

    /**
     * Reads all of the bytes from a 1-channel AudioInputStream.
     *
     * @param stream the stream to read
     * @return all bytes from the stream, null if error
     */
    private byte[] readAllBytesOneChannel(AudioInputStream stream) {
        // Read all the bytes (assuming 1-channel)
        byte[] data = null;

        try {
            data = getBytes(stream);
        } catch (IOException e) {
            System.err.println("Error reading all bytes from stream!");
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        return data;
    }

    /**
     * Reads all of the bytes from a 2-channel AudioInputStream.
     *
     * @param stream the stream to read
     * @return all bytes from the stream, null if error
     */
    private byte[][] readAllBytesTwoChannel(AudioInputStream stream) {
        // Read all the bytes (assuming 16-bit, 2-channel)
        byte[][] data = null;
        try {
            byte[] allBytes = getBytes(stream);
            byte[] left = new byte[allBytes.length / 2];
            byte[] right = new byte[allBytes.length / 2];

            for (int i = 0, j = 0; i < allBytes.length; i += 4, j += 2) {
                // Interleaved left then right
                left[j] = allBytes[i];
                left[j + 1] = allBytes[i + 1];
                right[j] = allBytes[i + 2];
                right[j + 1] = allBytes[i + 3];
            }
            data = new byte[2][];
            data[0] = left;
            data[1] = right;
        } catch (IOException e) {
            System.err.println("Error reading all bytes from stream!");
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        return data;
    }

    /**
     * Gets and AudioInputStream in the Audio system format.
     *
     * @param url URL of the resource
     * @return the specified stream as an AudioInputStream stream, null if
     * failure
     */
    private AudioInputStream getValidAudioStream(URL url) {
        AudioInputStream audioStream = null;
        try {
            audioStream = AudioSystem.getAudioInputStream(url);
            AudioFormat streamFormat = audioStream.getFormat();

            // 1-channel can also be treated as stereo
            AudioFormat mono16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);

            // 1 or 2 channel 8-bit may be easy to convert
            AudioFormat mono8 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 1, 1, 44100, false);
            AudioFormat stereo8 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 2, 2, 44100, false);

            // Now check formats (attempt conversion as needed)
            if (streamFormat.matches(Audio.FORMAT) || streamFormat.matches(mono16)) {
                return audioStream;
            } else if (AudioSystem.isConversionSupported(Audio.FORMAT, streamFormat)) {
                // Check conversion to Audio format
                audioStream = AudioSystem.getAudioInputStream(Audio.FORMAT, audioStream);
            } else if (AudioSystem.isConversionSupported(mono16, streamFormat)) {
                // Check conversion to mono alternate
                audioStream = AudioSystem.getAudioInputStream(mono16, audioStream);
            } else if (streamFormat.matches(stereo8) || AudioSystem.isConversionSupported(stereo8, streamFormat)) {
                // Try to convert from 8-bit, 2-channel

                // Convert to 8-bit stereo first?
                if (!streamFormat.matches(stereo8)) {
                    audioStream = AudioSystem.getAudioInputStream(stereo8, audioStream);
                }
                audioStream = convertStereo8Bit(audioStream);
            } else if (streamFormat.matches(mono8) || AudioSystem.isConversionSupported(mono8, streamFormat)) {
                // Try to convert from 8-bit, 1-channel
                // Convert to 8-bit mono first?
                if (!streamFormat.matches(mono8)) {
                    audioStream = AudioSystem.getAudioInputStream(mono8, audioStream);
                }
                audioStream = convertMono8Bit(audioStream);
            } else {
                // It's time to give up
                System.err.println("Unable to convert sound resource!");
                System.err.println(url);
                System.err.println(streamFormat);
                audioStream.close();
                return null;
            }

            // Check the frame length
            long frameLength = audioStream.getFrameLength();

            //Too long
            if (frameLength > Integer.MAX_VALUE) {
                System.err.println("core.Audio resource too long!");
                return null;
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported sound resource!\n" + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error getting resource stream!\n" + e.getMessage());
            return null;
        }
        return audioStream;
    }

    /**
     * Converts an 8-bit, signed, 1-channel AudioInputStream to 16-bit, signed,
     * 1-channel.
     *
     * @param stream the stream to convert
     * @return the converted stream
     */
    private AudioInputStream convertMono8Bit(AudioInputStream stream) {
        // Assuming 8-bit, 1-channel to 16-bit, 1-channel
        byte[] newData = null;

        try {
            byte[] data = getBytes(stream);
            int newNumBytes = data.length * 2;

            // Check if size overflowed
            if (newNumBytes < 0) {
                System.err.println("core.Audio resource too long!");
                return null;
            }
            newData = new byte[newNumBytes];

            // Convert bytes one-by-one to int, and then to 16-bit
            for (int i = 0, j = 0; i < data.length; i++, j += 2) {
                // Convert it to a double
                double floatVal = (double) data[i];
                floatVal /= (floatVal < 0) ? 128 : 127;

                if (floatVal < -1.0) {
                    // Just in case
                    floatVal = -1.0;
                } else if (floatVal > 1.0) {
                    floatVal = 1.0;
                }

                // Convert it to an int and then to 2 bytes
                int val = (int) (floatVal * Short.MAX_VALUE);
                newData[j + 1] = (byte) ((val >> 8) & 0xFF);    //MSB
                newData[j] = (byte) (val & 0xFF);               //LSB
            }
        } catch (IOException e) {
            System.err.println("Error reading all bytes from stream!");
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        AudioFormat mono16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
        return new AudioInputStream(new ByteArrayInputStream(newData), mono16, newData.length / 2);
    }

    /**
     * Converts an 8-bit, signed, 2-channel AudioInputStream to 16-bit, signed,
     * 2-channel.
     *
     * @param stream the stream to convert
     * @return the converted stream
     */
    private AudioInputStream convertStereo8Bit(AudioInputStream stream) {
        // Assuming 8-bit, 2-channel to 16-bit, 2-channel
        byte[] newData = null;
        try {
            byte[] data = getBytes(stream);
            int newNumBytes = data.length * 2 * 2;

            // Check if size overflowed
            if (newNumBytes < 0) {
                System.err.println("core.Audio resource too long!");
                return null;
            }
            newData = new byte[newNumBytes];
            for (int i = 0, j = 0; i < data.length; i += 2, j += 4) {
                // Convert them to doubles
                double leftFloatVal = (double) data[i];
                double rightFloatVal = (double) data[i + 1];
                leftFloatVal /= (leftFloatVal < 0) ? 128 : 127;
                rightFloatVal /= (rightFloatVal < 0) ? 128 : 127;

                if (leftFloatVal < -1.0) {
                    // Just in case
                    leftFloatVal = -1.0;
                } else if (leftFloatVal > 1.0) {
                    leftFloatVal = 1.0;
                }
                if (rightFloatVal < -1.0) {
                    // Just in case
                    rightFloatVal = -1.0;
                } else if (rightFloatVal > 1.0) {
                    rightFloatVal = 1.0;
                }

                // Convert them to ints and then to 2 bytes each
                int leftVal = (int) (leftFloatVal * Short.MAX_VALUE);
                int rightVal = (int) (rightFloatVal * Short.MAX_VALUE);

                // Left channel bytes
                newData[j + 1] = (byte) ((leftVal >> 8) & 0xFF);    //MSB
                newData[j] = (byte) (leftVal & 0xFF);               //LSB

                // Then right channel bytes
                newData[j + 3] = (byte) ((rightVal >> 8) & 0xFF);   //MSB
                newData[j + 2] = (byte) (rightVal & 0xFF);          //LSB
            }
        } catch (IOException e) {
            System.err.println("Error reading all bytes from stream!");
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        AudioFormat stereo16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        return new AudioInputStream(new ByteArrayInputStream(newData), stereo16, newData.length / 4);
    }

    /**
     * Read all of the bytes from an AudioInputStream.
     *
     * @param stream the stream from which to read bytes
     * @return all bytes read from the AudioInputStream
     * @throws IOException
     */
    private byte[] getBytes(AudioInputStream stream) throws IOException {
        // Buffer 1-sec at a time
        int bufSize = (int) Audio.FORMAT.getSampleRate() * Audio.FORMAT.getChannels() * Audio.FORMAT.getFrameSize();
        byte[] buf = new byte[bufSize];

        ByteList list = new ByteList(bufSize);

        int numRead = 0;
        while ((numRead = stream.read(buf)) > -1) {
            for (int i = 0; i < numRead; i++) {
                list.add(buf[i]);
            }
        }
        return list.asArray();
    }

    /**
     * Dumps sound data to a temporary file for streaming and returns a
     * StreamInfo for the stream.
     *
     * @param data the sound data to write to the temporary file
     * @return a StreamInfo Object for the stream
     */
    private StreamInfo createFileStream(byte[][] data) {
        // First try to create a file for the data to live in
        File temp = null;
        try {
            temp = File.createTempFile("tiny", "sound");

            // Make sure this file will be deleted on exit
            temp.deleteOnExit();
        } catch (IOException e) {
            System.err.println("Failed to create file for streaming!");
            return null;
        }

        // See if we can get the URL for this file
        URL url = null;
        try {
            url = temp.toURI().toURL();
        } catch (MalformedURLException e1) {
            System.err.println("Failed to get URL for stream file!");
            return null;
        }

        // We have the file, now we want to be able to write to it
        OutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(temp), (512 * 1024)); // Buffer 512kb
        } catch (FileNotFoundException e) {
            System.err.println("Failed to open stream file for writing!");
            return null;
        }

        // Write the bytes to the file
        try {
            // Write two at a time from each channel
            for (int i = 0; i < data[0].length; i += 2) {
                try {
                    // First left
                    out.write(data[0], i, 2);

                    // Then right
                    out.write(data[1], i, 2);
                } catch (IOException e) {
                    // Hmm
                    System.err.println("Failed writing bytes to stream file!");
                    return null;
                }
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                System.err.println("Failed closing stream file after writing!");
            }
        }
        return new StreamInfo(url, data[0].length);
    }

    /**
     * Iterates through available JavaSound Mixers looking for one that can
     * provide a line to the speakers.
     *
     * @return an opened SourceDataLine to the speakers
     */
    private SourceDataLine tryGetLine() {
        // First build our line info and get all available mixers
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, Audio.FORMAT);
        javax.sound.sampled.Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        // Iterate through the mixers trying to find a line
        for (int i = 0; i < mixerInfos.length; i++) {
            javax.sound.sampled.Mixer mixer = null;
            try {
                // First try to actually get the mixer
                mixer = AudioSystem.getMixer(mixerInfos[i]);
            } catch (SecurityException e) {
                // Not much we can do here
            } catch (IllegalArgumentException e) {
                // This should never happen since we were told the mixer exists
            }

            // Check if we got a mixer and our line is supported
            if (mixer == null || !mixer.isLineSupported(lineInfo)) {
                continue;
            }

            // See if we can actually get a line
            SourceDataLine line = null;

            try {
                line = (SourceDataLine) mixer.getLine(lineInfo);

                // Don't try to open if already open
                if (!line.isOpen()) {
                    line.open(Audio.FORMAT);
                }
            } catch (LineUnavailableException e) {
                // We either failed to get or open
                // Should we do anything here?
            } catch (SecurityException e) {
                // Not much we can do here
            }
            // Check if we succeeded
            if (line != null && line.isOpen()) {
                return line;
            }
        }
        // No good
        return null;
    }
}

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
package tiny.engine.audio.internal;

import tiny.engine.audio.Audio;

import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The UpdateRunner class implements Runnable and is what performs automatic
 * updates of the Audio system. UpdateRunner is an internal class of the Audio
 * system and should be of no real concern to the average user of Audio.
 *
 * @author Finn Kuusisto
 */
public final class UpdateRunner implements Runnable {

    private AtomicBoolean running;
    private SourceDataLine outLine;
    private Mixer mixer;

    /**
     * Constructs a new UpdateRunner to update the Audio system.
     *
     * @param mixer the mixer to read sound data from
     * @param outLine the line to write sound data to
     */
    public UpdateRunner(Mixer mixer, SourceDataLine outLine) {
        this.running = new AtomicBoolean();
        this.mixer = mixer;
        this.outLine = outLine;
    }

    /**
     * Stop this UpdateRunner from updating the Audio system.
     */
    public void stop() {
        this.running.set(false);
    }

    @Override
    public void run() {
        // Mark the updater as running
        this.running.set(true);

        // 1-sec buffer
        int bufSize = (int) Audio.FORMAT.getFrameRate() * Audio.FORMAT.getFrameSize();
        byte[] audioBuffer = new byte[bufSize];

        // Only buffer some maximum number of frames each update (25ms)
        int maxFramesPerUpdate = (int) ((Audio.FORMAT.getFrameRate() / 1000) * 25);
        int numBytesRead = 0;
        double framesAccrued = 0;
        long lastUpdate = System.nanoTime();

        // Keep running until told to stop
        while (this.running.get()) {
            // Check the time
            long currTime = System.nanoTime();

            // Accrue frames
            double delta = currTime - lastUpdate;
            double secDelta = (delta / 1000000000L);
            framesAccrued += secDelta * Audio.FORMAT.getFrameRate();

            // Read frames if needed
            int framesToRead = (int) framesAccrued;
            int framesToSkip = 0;

            // Check if we need to skip frames to catch up
            if (framesToRead > maxFramesPerUpdate) {
                framesToSkip = framesToRead - maxFramesPerUpdate;
                framesToRead = maxFramesPerUpdate;
            }

            // Skip frames
            if (framesToSkip > 0) {
                int bytesToSkip = framesToSkip * Audio.FORMAT.getFrameSize();
                this.mixer.skip(bytesToSkip);
            }

            // Read frames
            if (framesToRead > 0) {
                // Read from the mixer
                int bytesToRead = framesToRead * Audio.FORMAT.getFrameSize();
                int tmpBytesRead = this.mixer.read(audioBuffer, numBytesRead, bytesToRead);
                numBytesRead += tmpBytesRead; // Mark how many read

                // Fill rest with zeroes
                int remaining = bytesToRead - tmpBytesRead;
                for (int i = 0; i < remaining; i++) {
                    audioBuffer[numBytesRead + i] = 0;
                }
                numBytesRead += remaining; // Mark zeroes read
            }

            // Mark frames read and skipped
            framesAccrued -= (framesToRead + framesToSkip);

            // Write to speakers
            if (numBytesRead > 0) {
                this.outLine.write(audioBuffer, 0, numBytesRead);
                numBytesRead = 0;
            }

            // Mark last update
            lastUpdate = currTime;

            // Give the CPU back to the OS for a bit
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }
}

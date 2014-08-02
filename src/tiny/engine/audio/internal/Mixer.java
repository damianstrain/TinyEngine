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

import java.util.ArrayList;
import java.util.List;

/**
 * The Mixer class is what does the sound data mixing for the Audio system.
 * Mixer is an internal class of the Audio system and should be of no real
 * concern to the average user of Audio.
 *
 * @author Finn Kuusisto
 */
public final class Mixer {

    private List<MusicReference> musics;
    private List<SoundReference> sounds;
    private double globalVolume;
    private int[] dataBuf;       // Buffer for reading sound data

    /**
     * Construct a new Mixer for Audio system.
     */
    public Mixer() {
        this.musics = new ArrayList<MusicReference>();
        this.sounds = new ArrayList<SoundReference>();
        this.globalVolume = 1.0;
        this.dataBuf = new int[2];              // 2-channel
    }

    /**
     * Get the global volume for this Mixer.
     *
     * @return the global volume
     */
    public synchronized double getVolume() {
        return this.globalVolume;
    }

    /**
     * Set the global volume for this Mixer.
     *
     * @param volume the global volume to set
     */
    public synchronized void setVolume(double volume) {
        if (volume >= 0.0) {
            this.globalVolume = volume;
        }
    }

    /**
     * Registers a MusicReference with this Mixer.
     *
     * @param music MusicReference to be registered
     */
    public synchronized void registerMusicReference(MusicReference music) {
        this.musics.add(music);
    }

    /**
     * Registers a SoundReference with this Mixer.
     *
     * @param sound SoundReference to be registered
     */
    public synchronized void registerSoundReference(SoundReference sound) {
        this.sounds.add(sound);
    }

    /**
     * Un-registers a MusicReference with this Mixer.
     *
     * @param music MusicReference to be unregistered
     */
    public synchronized void unRegisterMusicReference(MusicReference music) {
        this.musics.remove(music);
    }

    /**
     * Un-registers all SoundReferences with a given soundID.
     *
     * @param soundID ID of SoundReferences to be unregistered
     */
    public synchronized void unRegisterSoundReference(int soundID) {
        // Removal working backward is easier
        for (int i = this.sounds.size() - 1; i >= 0; i--) {
            if (this.sounds.get(i).getSoundID() == soundID) {
                this.sounds.remove(i).dispose();
            }
        }
    }

    /**
     * Un-register all Music registered with this Mixer.
     */
    public synchronized void clearMusic() {
        this.musics.clear();
    }

    /**
     * Un-register all Sounds registered with this Mixer.
     */
    public synchronized void clearSounds() {
        for (SoundReference s : this.sounds) {
            s.dispose();
        }
        this.sounds.clear();
    }

    /**
     * Read bytes from this Mixer.
     *
     * @param data the buffer to read the bytes into
     * @param offset the start index to read bytes into
     * @param length the maximum number of bytes that should be read
     * @return number of bytes read into buffer
     */
    public synchronized int read(byte[] data, int offset, int length) {
        // Assume little-endian, stereo, 16-bit, signed PCM
        int numRead = 0;
        boolean bytesRead = true; // Terminate early if out of bytes

        for (int i = offset; i < (length + offset) && bytesRead; i += 4) {
            // First assume we are done
            bytesRead = false;

            // Need to track value across sound sources
            double leftValue = 0.0;
            double rightValue = 0.0;

            // Go through all the music first
            for (int m = 0; m < this.musics.size(); m++) {
                MusicReference music = this.musics.get(m);

                // Is the music playing and are there bytes available
                if (music.getPlaying() && music.bytesAvailable() > 0) {
                    // Add this music to the mix by volume (and global volume)
                    music.nextTwoBytes(this.dataBuf, false);
                    double volume = music.getVolume() * this.globalVolume;
                    double leftCurr = (this.dataBuf[0] * volume);
                    double rightCurr = (this.dataBuf[1] * volume);

                    // Do panning
                    double pan = music.getPan();

                    if (pan != 0.0) {
                        double ll = (pan <= 0.0) ? 1.0 : (1.0 - pan);
                        double lr = (pan <= 0.0) ? Math.abs(pan) : 0.0;
                        double rl = (pan >= 0.0) ? pan : 0.0;
                        double rr = (pan >= 0.0) ? 1.0 : (1.0 - Math.abs(pan));
                        double tmpL = (ll * leftCurr) + (lr * rightCurr);
                        double tmpR = (rl * leftCurr) + (rr * rightCurr);
                        leftCurr = tmpL;
                        rightCurr = tmpR;
                    }

                    // Update the final left and right channels
                    leftValue += leftCurr;
                    rightValue += rightCurr;

                    // We know we aren't done yet now
                    bytesRead = true;
                }
            }

            // Then go through all the sounds (backwards to remove completed)
            for (int s = this.sounds.size() - 1; s >= 0; s--) {
                SoundReference sound = this.sounds.get(s);

                // Are there bytes available
                if (sound.bytesAvailable() > 0) {
                    // Add this sound to the mix by volume (and global volume)
                    sound.nextTwoBytes(this.dataBuf, false);
                    double volume = sound.getVolume() * this.globalVolume;
                    double leftCurr = (this.dataBuf[0] * volume);
                    double rightCurr = (this.dataBuf[1] * volume);

                    // Do panning
                    double pan = sound.getPan();

                    if (pan != 0.0) {
                        double ll = (pan <= 0.0) ? 1.0 : (1.0 - pan);
                        double lr = (pan <= 0.0) ? Math.abs(pan) : 0.0;
                        double rl = (pan >= 0.0) ? pan : 0.0;
                        double rr = (pan >= 0.0) ? 1.0 : (1.0 - Math.abs(pan));
                        double tmpL = (ll * leftCurr) + (lr * rightCurr);
                        double tmpR = (rl * leftCurr) + (rr * rightCurr);
                        leftCurr = tmpL;
                        rightCurr = tmpR;
                    }

                    // Update the final left and right channels
                    leftValue += leftCurr;
                    rightValue += rightCurr;

                    // We know we aren't done yet now
                    bytesRead = true;

                    // Remove the reference if done
                    if (sound.bytesAvailable() <= 0) {
                        this.sounds.remove(s).dispose();
                    }
                } else {
                    // Otherwise remove this reference
                    this.sounds.remove(s).dispose();
                }
            }

            // If we actually read bytes, store in the buffer
            if (bytesRead) {
                int finalLeftValue = (int) leftValue;
                int finalRightValue = (int) rightValue;

                // Clipping
                if (finalLeftValue > Short.MAX_VALUE) {
                    finalLeftValue = Short.MAX_VALUE;
                } else if (finalLeftValue < Short.MIN_VALUE) {
                    finalLeftValue = Short.MIN_VALUE;
                }
                if (finalRightValue > Short.MAX_VALUE) {
                    finalRightValue = Short.MAX_VALUE;
                } else if (finalRightValue < Short.MIN_VALUE) {
                    finalRightValue = Short.MIN_VALUE;
                }

                // Left channel bytes
                data[i + 1] = (byte) ((finalLeftValue >> 8) & 0xFF);    //MSB
                data[i] = (byte) (finalLeftValue & 0xFF);               //LSB

                // Then right channel bytes
                data[i + 3] = (byte) ((finalRightValue >> 8) & 0xFF);   //MSB
                data[i + 2] = (byte) (finalRightValue & 0xFF);          //LSB
                numRead += 4;
            }
        }
        return numRead;
    }

    /**
     * Skip specified number of bytes of all sound in this Mixer.
     *
     * @param numBytes the number of bytes to skip
     */
    public synchronized void skip(int numBytes) {
        // Go through all the music first
        for (int m = 0; m < this.musics.size(); m++) {
            MusicReference music = this.musics.get(m);

            // Is the music playing and are there bytes available
            if (music.getPlaying() && music.bytesAvailable() > 0) {
                // Skip the bytes
                music.skipBytes(numBytes);
            }
        }

        // Then go through all the sounds (backwards to remove completed)
        for (int s = this.sounds.size() - 1; s >= 0; s--) {
            SoundReference sound = this.sounds.get(s);

            // Are there bytes available
            if (sound.bytesAvailable() > 0) {
                // Skip the bytes
                sound.skipBytes(numBytes);
                // Remove the reference if done
                if (sound.bytesAvailable() <= 0) {
                    this.sounds.remove(s).dispose();
                }
            } else {
                // Otherwise remove this reference
                this.sounds.remove(s).dispose();
            }
        }
    }
}

package tiny.engine.utils;

/**
 * The TimeUtil class provides several convenience methods which provides access
 * to the System clock or time. You can use these methods to calculate the
 * elapsed time.
 *
 * @author Damian Strain
 */
public final class TimeUtils {

    /**
     * Returns the current value of the most precise available system timer, in
     * nanoseconds (ns). This method can only be used to measure elapsed time
     * and is not related to any other notion of system or wall-clock time.
     *
     * @return the current value of the System timer in nanoseconds
     */
    public static double getTimeNanos() {
        return (double) System.nanoTime();
    }

    /**
     * Returns the current value of the most precise available system timer, in
     * microseconds (us). This method can only be used to measure elapsed time
     * and is not related to any other notion of system or wall-clock time.
     *
     * @return the current value of the System timer in microseconds
     */
    public static double getTimeMicros() {
        return getTimeSecs() / 1000000.0D;
    }

    /**
     * Returns the current value of the most precise available system timer, in
     * milliseconds (ms). This method can only be used to measure elapsed time
     * and is not related to any other notion of system or wall-clock time.
     *
     * @return the current value of the System timer in milliseconds
     */
    public static double getTimeMillis() {
        return (double) System.nanoTime() / 1000000.0D;
    }

    /**
     * Returns the current value of the most precise available system timer, in
     * centiseconds (cs). This method can only be used to measure elapsed time
     * and is not related to any other notion of system or wall-clock time.
     *
     * @return the current value of the System timer in centiseconds
     */
    public static double getTimeCentis() {
        return getTimeSecs() / 100.0D;
    }

    /**
     * Returns the current value of the most precise available system timer, in
     * seconds (s). This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     *
     * @return the current value of the System timer in seconds
     */
    public static double getTimeSecs() {
        return (double) System.nanoTime() / 1000000000.0D;
    }
}

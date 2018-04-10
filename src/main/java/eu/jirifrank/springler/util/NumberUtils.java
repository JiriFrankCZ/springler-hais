package eu.jirifrank.springler.util;

public final class NumberUtils {

    /**
     * converts as follows:
     * 1.1  -> 1.0
     * 1.3  -> 1.5
     * 2.1  -> 2.0
     * 2.25 -> 2.5
     */
    public static double roundToHalf(Double f) {
        return Math.round(f * 2) / 2.0f;
    }

    private NumberUtils() {
    }
}

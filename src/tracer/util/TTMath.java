package tracer.util;

/**
 * Class to hold some methods not contained in the {@link Math} class.
 *
 * @author Pedro Henrique
 */
public final class TTMath {

    /**
     * Prevents instantiation.
     */
    private TTMath() {
    }

    /**
     * Returns the interpolation between a and b using the received gradient.
     * <p>
     * If g == 0, returns a, else if g == 1, returns b.
     *
     * @param a the starting point
     * @param b the finishing point
     * @param g the interpolation value
     * @return the result of the interpolation
     */
    public static float interpolate(float a, float b, float g) {
        return a * (1 - g) + b * g;
    }

    /**
     * Returns the received value clamped between the min and max received values (inclusive). If min is greater than
     * max, this method will returns the min or max depending of the v.
     *
     * @param v   the value to clamp
     * @param min the min value
     * @param max the max value
     * @return the value clamped
     */
    public static float clamp(float v, float min, float max) {
        return v < min ? min : v > max ? max : v;
    }

    /**
     * Returns the received value clamped between 0 and 1 (inclusive).
     *
     * @param v the value to clamp
     * @return the value clamped
     */
    public static float clamp01(float v) {
        return v < 0 ? 0 : v > 1 ? 1 : v;
    }
}

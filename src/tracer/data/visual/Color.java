package tracer.data.visual;

import tracer.util.Copyable;

/**
 * Represents a color with red, green and blue components.
 * <p>
 * About the methods in this class:
 * <p>
 * The instance methods that returns a Color (except for the {@link #copy()} method) will always change the instance
 * that calls the method and returns itself.
 * <p>
 * For each instance method that returns a Color, exists an equivalent static method that returns a modified copy,
 * except for the current methods:
 * <p>
 * {@link #copy()}, all {@link #set}s and {@link #subI(Color)}
 *
 * @author Pedro Henrique
 */
public class Color implements Copyable<Color> {

    /**
     * The red component of this color.
     */
    public float r;

    /**
     * The green component of this color.
     */
    public float g;

    /**
     * The blue component of this color.
     */
    public float b;

    /**
     * Creates a color with 0 in all components (transparent black).
     */
    public Color() {
        r = 0;
        g = 0;
        b = 0;
    }

    /**
     * Creates a color with the received components.
     *
     * @param r the red component value
     * @param g ths green component value
     * @param b the blue component value
     */
    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Float.compare(color.r, r) == 0 && Float.compare(color.g, g) == 0 && Float.compare(color.b, b) == 0;
    }

    @Override
    public int hashCode() {
        int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
        result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
        result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" + r + ", " + g + ", " + b + "}";
    }

    @Override
    public Color copy() {
        return new Color(r, g, b);
    }

    /**
     * Returns an int value of color in the ARGB color system.
     *
     * @return the int value of the color
     */
    public int getIntValue() {
        return 0xff000000
                | ((int) (r * 255)) << 16
                | ((int) (g * 255)) << 8
                | ((int) (b * 255));
    }

    /**
     * Sets the received values in the color and returns this color modified.
     *
     * @param r the red component value
     * @param g the green component value
     * @param b the blue component value
     * @return this color modified
     */
    public Color set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    // Color operations

    /**
     * Sums the received color in this and return this color modified. The received color is not modified.
     *
     * @param other the color to sum with this
     * @return this color modified
     */
    public Color sum(Color other) {
        r = r + other.r;
        g = g + other.g;
        b = b + other.b;
        return this;
    }

    /**
     * Subtracts the received color of this and return this color modified. The received color is not modified.
     *
     * @param other the color to subtract from this
     * @return this color modified
     * @see #subI(Color)
     */
    public Color sub(Color other) {
        r = r - other.r;
        g = g - other.g;
        b = b - other.b;
        return this;
    }

    /**
     * Subtracts this color from the received (inverted subtraction), but saving in this. The received color is not
     * modified.
     *
     * @param other the color to be subtracted from this
     * @return this color modified
     * @see #sub(Color)
     */
    public Color subI(Color other) {
        r = other.r - r;
        g = other.g - g;
        b = other.b - b;
        return this;
    }

    /**
     * Multiplies each component of the received color with the components of this colors. The received color is not
     * modified.
     *
     * @param other the color to multiply this
     * @return this color modified
     * @see #scale(float)
     */
    public Color mul(Color other) {
        r *= other.r;
        g *= other.g;
        b *= other.b;
        return this;
    }

    /**
     * Scales each component of this color with the received scalar.
     *
     * @param scalar the value to scale the components
     * @return the color scaled by the received scalar
     * @see #mul(Color)
     */
    public Color scale(float scalar) {
        r = r * scalar;
        g = g * scalar;
        b = b * scalar;
        return this;
    }

    /**
     * Returns the interpolation between the received colors, This method too can be used to extrapolate colors.
     * <p>
     * If the gradient is equals to 0, this color is the result
     * <p>
     * If th gradient is equals to 1, the received color is the result
     * <p>
     * If the gradient is less than 0 or greater than 1, the color is outside the interval between this and the
     * received color.
     *
     * @param other    the color to interpolate with this
     * @param gradient the interpolation value
     * @return this color modified
     */
    public Color interpolate(Color other, float gradient) {
        r = r * (1 - gradient) + other.r * gradient;
        g = g * (1 - gradient) + other.g * gradient;
        b = b * (1 - gradient) + other.b * gradient;
        return this;
    }

    // Static color operations

    /**
     * This method has the same behaviour of the instance method {@link #sum(Color)}, but does not modify the received
     * colors.
     *
     * @param v1 the color to operate
     * @param v2 the color to operate
     * @return the sum between the received colors
     * @see #sum(Color)
     */
    public static Color sum(Color v1, Color v2) {
        return v1.copy().sum(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #sub(Color)}, but does not modify the received
     * colors.
     *
     * @param v1 the color to operate
     * @param v2 the color to operate
     * @return the subtraction of the first over the second color
     * @see #sub(Color)
     */
    public static Color sub(Color v1, Color v2) {
        return v1.copy().sub(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #mul(Color)}, but does not modify the received
     * colors.
     *
     * @param v1 the color to operate
     * @param v2 the color to operate
     * @return the component multiplication between the received colors
     * @see #mul(Color)
     */
    public static Color mul(Color v1, Color v2) {
        return v1.copy().mul(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #scale(float)}, but does not modify the received
     * color.
     *
     * @param v the color to scale
     * @param s the scalar
     * @return a copy of the received color scaled
     * @see #scale(float)
     */
    public static Color scale(Color v, float s) {
        return v.copy().scale(s);
    }

    /**
     * This method has the same behaviour of the instance method {@link #interpolate(Color, float)}, but does not modify
     * the received colors.
     *
     * @param v1 the color to operate
     * @param v2 the color to operate
     * @param g  the gradient value
     * @return the interpolation (or extrapolation) of the received colors and the gradient
     * @see #interpolate(Color, float)
     */
    public static Color interpolate(Color v1, Color v2, float g) {
        return v1.copy().interpolate(v2, g);
    }

    /**
     * Calulates and returns the mean of the received colors. This method does not have a instance version.
     *
     * @param cs the colors to calculate the mean
     * @return the mean of the received colors
     */
    public static Color mean(Color... cs) {
        if (cs == null || cs.length == 0) {
            throw new IllegalArgumentException("At leas one color is required");
        }
        float r = 0;
        float g = 0;
        float b = 0;
        for (Color c : cs) {
            r += c.r;
            g += c.g;
            b += c.b;
        }
        return new Color(r / cs.length, g / cs.length, b / cs.length);
    }

    // Static default colors

    /**
     * Returns the black color.
     *
     * @return the black color.
     */
    public static Color black() {
        return new Color(0, 0, 0);
    }

    /**
     * Returns the white color.
     *
     * @return the white color.
     */
    public static Color white() {
        return new Color(1, 1, 1);
    }

    /**
     * Returns the light gray (75% white) color.
     *
     * @return the light gray color.
     */
    public static Color lightGray() {
        return new Color(0.75f, 0.75f, 0.75f);
    }

    /**
     * Returns the gray (50% white) color.
     *
     * @return the gray color.
     */
    public static Color gray() {
        return new Color(0.5f, 0.5f, 0.5f);
    }

    /**
     * Returns the dark gray (25% white) color.
     *
     * @return the dark gray color.
     */
    public static Color darkGray() {
        return new Color(0.25f, 0.25f, 0.25f);
    }

    /**
     * Returns the red color.
     *
     * @return the red color.
     */
    public static Color red() {
        return new Color(1, 0, 0);
    }

    /**
     * Returns the green color.
     *
     * @return the green color.
     */
    public static Color green() {
        return new Color(0, 1, 0);
    }

    /**
     * Returns the blue color.
     *
     * @return the blue color.
     */
    public static Color blue() {
        return new Color(0, 0, 1);
    }

    /**
     * Returns the yellow color.
     *
     * @return the yellow color.
     */
    public static Color yellow() {
        return new Color(1, 1, 0);
    }

    /**
     * Returns the magenta color.
     *
     * @return the magenta color.
     */
    public static Color magenta() {
        return new Color(1, 0, 1);
    }

    /**
     * Returns the cyan color.
     *
     * @return the cyan color.
     */
    public static Color cyan() {
        return new Color(0, 1, 1);
    }

    /**
     * Returns the orange color.
     *
     * @return the orange color.
     */
    public static Color orange() {
        return new Color(1, 0.78f, 0);
    }

    /**
     * Returns the pink color.
     *
     * @return the pink color.
     */
    public static Color pink() {
        return new Color(1, 0.69f, 0.69f);
    }
}

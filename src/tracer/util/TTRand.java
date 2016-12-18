package tracer.util;

import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;

import java.util.Random;

/**
 * Contains helper methods to generate random values.
 *
 * @author Pedro Henrique
 */
public class TTRand {

    /**
     * The internal pseudo-random generator.
     */
    private static final Random PRNG = new Random();

    /**
     * Prevents instantiation.
     */
    private TTRand() {
    }

    /**
     * Returns (approximately) uniformly distributed random values between 0 (inclusive) and 1 (inclusive).
     *
     * @return a random value
     */
    public static float value() {
        return PRNG.nextFloat();
    }

    /**
     * Returns (approximately) uniformly distributed random int values between the received minimum and maximum values.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum values (inclusive)
     * @return the generated value
     */
    public static int range(int min, int max) {
        return PRNG.nextInt(max - min + 1) + min;
    }

    /**
     * Returns (approximately) uniformly distributed random float values between the received minimum and maximum
     * values.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum values (inclusive)
     * @return the generated value
     */
    public static float range(float min, float max) {
        return PRNG.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a random {@link Vector3} where the x y and z can be between -1 and 1 (the vector magnitude max can be 3).
     *
     * @return a random vector
     */
    public static Vector3 vector3() {
        return new Vector3(PRNG.nextFloat(), PRNG.nextFloat(), PRNG.nextFloat()).scale(2).sub(Vector3.one());
    }

    /**
     * Returns a {@link Vector3} between the received vectors (in line formed by the vectors).
     *
     * @param point1 the first point
     * @param point2 the second point
     * @return a random vector
     */
    public static Vector3 betweenLine(Vector3 point1, Vector3 point2) {
        return Vector3.interpolate(point1, point2, PRNG.nextFloat());
    }

    /**
     * Returns a {@link Vector3} between the received vectors (in cube formed by the vectors).
     *
     * @param point1 the first point
     * @param point2 the second point
     * @return a random vector
     */
    public static Vector3 betweenCube(Vector3 point1, Vector3 point2) {
        return new Vector3(
                TTMath.interpolate(point1.x, point2.x, PRNG.nextFloat()),
                TTMath.interpolate(point1.y, point2.y, PRNG.nextFloat()),
                TTMath.interpolate(point1.y, point2.y, PRNG.nextFloat())
        );
    }

    /**
     * Returns a uniformly distributed point in the surface of the unit sphere.
     *
     * @return a random point on the unit sphere
     */
    public static Vector3 onUniformSphere() {
        float longitude = 2 * (float) Math.PI * PRNG.nextFloat();
        float latitude = (float) Math.acos(2 * PRNG.nextFloat() - 1);
        float cosLon = (float) Math.cos(longitude);
        float sinLon = (float) Math.sin(longitude);
        float cosLat = (float) Math.cos(latitude);
        float sinLat = (float) Math.sin(latitude);
        return new Vector3(sinLat * cosLon, sinLat * sinLon, cosLat);
    }

    /**
     * Returns a polar distributed point in the surface of the unit sphere (point concentration next to the z axis).
     *
     * @return a random point on the unit sphere
     */
    public static Vector3 onPolarSphere() {
        float longitude = 2 * (float) Math.PI * PRNG.nextFloat();
        float latitude = 2 * PRNG.nextFloat() - 1;
        float cosLon = (float) Math.cos(longitude);
        float sinLon = (float) Math.sin(longitude);
        float cosLat = (float) Math.cos(latitude);
        float sinLat = (float) Math.sin(latitude);
        return new Vector3(sinLat * cosLon, sinLat * sinLon, cosLat);
    }

    /**
     * Returns a uniformly distributed point in the unit sphere.
     *
     * @return a random point on the unit sphere
     */
    public static Vector3 insideUniformSphere() {
        float longitude = 2 * (float) Math.PI * PRNG.nextFloat();
        float latitude = (float) Math.acos(2 * PRNG.nextFloat() - 1);
        float radius = (float) Math.pow(PRNG.nextFloat(), 1f / 3f);
        float cosLon = (float) Math.cos(longitude);
        float sinLon = (float) Math.sin(longitude);
        float cosLat = (float) Math.cos(latitude);
        float sinLat = (float) Math.sin(latitude);
        return new Vector3(sinLat * cosLon, sinLat * sinLon, cosLat).scale(radius);
    }

    /**
     * Returns a polar distributed point in the unit sphere (point concentration next to the z axis).
     *
     * @return a random point on the unit sphere
     */
    public static Vector3 insidePolarSphere() {
        float longitude = 2 * (float) Math.PI * PRNG.nextFloat();
        float latitude = 2 * PRNG.nextFloat() - 1;
        float radius = (float) Math.pow(PRNG.nextFloat(), 1f / 3f);
        float cosLon = (float) Math.cos(longitude);
        float sinLon = (float) Math.sin(longitude);
        float cosLat = (float) Math.cos(latitude);
        float sinLat = (float) Math.sin(latitude);
        return new Vector3(sinLat * cosLon, sinLat * sinLon, cosLat).scale(radius);
    }

    /**
     * Returns a point in the hemisphere surface oriented by the received orientation vector.
     *
     * @param orientation the vector that orients the hemisphere
     * @return a random vector
     */
    public static Vector3 onUniformHemisphere(Vector3 orientation) {
        Vector3 randomOnUniformSphere = onUniformSphere();
        if (orientation.dot(randomOnUniformSphere) < 0) {
            randomOnUniformSphere.negate();
        }
        return randomOnUniformSphere;
    }

    /**
     * Returns a point the the hemisphere surface oriented by the received orientation vector using polar distribution
     * (point concentration next to the orientation vector).
     *
     * @param orientation the vector that orients the hemisphere
     * @return a random vector
     */
    public static Vector3 onPolarHemisphere(Vector3 orientation) {
        return Matrix4.rotationAround(
                Vector3.cross(orientation, Vector3.forward()),
                orientation.angle(Vector3.forward())
        ).transformAsDirection(onPolarSphere());
    }

    /**
     * Returns a point in the hemisphere oriented by the received orientation vector.
     *
     * @param orientation the vector that orients the hemisphere
     * @return a random vector
     */
    public static Vector3 insideUniformHemisphere(Vector3 orientation) {
        Vector3 randomInsideUniformSphere = insideUniformSphere();
        if (orientation.dot(randomInsideUniformSphere) < 0) {
            randomInsideUniformSphere.negate();
        }
        return randomInsideUniformSphere;
    }

    /**
     * Returns a point the the hemisphere oriented by the received orientation vector using polar distribution (point
     * concentration next to the orientation vector).
     *
     * @param orientation the vector that orients the hemisphere
     * @return a random vector
     */
    public static Vector3 insidePolarHemisphere(Vector3 orientation) {
        return Matrix4.rotationAround(
                Vector3.cross(orientation, Vector3.forward()),
                orientation.angle(Vector3.forward())
        ).transformAsDirection(insidePolarSphere());
    }
}

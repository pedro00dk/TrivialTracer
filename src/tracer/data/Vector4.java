package tracer.data;

import tracer.util.Copyable;

/**
 * Represents a 4-dimension vector used to store positions and transforms.
 * <p>
 * About the methods in this class:
 * <p>
 * The instance methods that returns a Vector4 (except for the {@link #copy()} method) will always change the instance
 * that calls the method and returns itself.
 * <p>
 * For each instance method that returns a Vector4, exists an equivalent static method that returns a modified copy,
 * except for the current methods:
 * <p>
 * {@link #copy()}, the {@link #set(float, float, float, float)}, {@link #subI(Vector4)}, {@link #projectI(Vector4)},
 * {@link #rejectI(Vector4)} and {@link #orientateI(Vector4, float)}
 *
 * @author Pedro Henrique
 */
public class Vector4 implements Copyable<Vector4> {

    /**
     * The X component of this vector.
     */
    public float x;

    /**
     * The Y component of this vector.
     */
    public float y;

    /**
     * The Z component of this vector.
     */
    public float z;

    /**
     * The w component of this vector.
     */
    public float w;


    /**
     * Creates a vector with 0 in all components.
     */
    public Vector4() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    /**
     * Creates a vector with the received components.
     *
     * @param x the X component value
     * @param y ths Y component value
     * @param z the Z component value
     * @param w the W component value
     */
    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector4 vector4 = (Vector4) o;
        return Float.compare(vector4.x, x) == 0 && Float.compare(vector4.y, y) == 0 && Float.compare(vector4.z, z) == 0
                && Float.compare(vector4.w, w) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + (w != +0.0f ? Float.floatToIntBits(w) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public Vector4 copy() {
        return new Vector4(x, y, z, w);
    }

    /**
     * Sets the received values in the vector and returns this vector modified.
     *
     * @param x the X component value
     * @param y the Y component value
     * @param z the Z component value
     * @param w the W component value
     * @return this vector modified.
     */
    public Vector4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    // Data operations

    /**
     * Returns the magnitude of this vector. Verify if is possible use the {@link #sqrMag()} instead this method,
     * because is more faster. This method calls the {@link #sqrMag()} method.
     *
     * @return the magnitude of this vector
     * @see #sqrMag()
     */
    public float mag() {
        return (float) Math.sqrt(sqrMag());
    }

    /**
     * Returns the squared magnitude of this vector, is more faster than {@link #mag()}.
     *
     * @return the squared magnitude of this vector
     * @see #mag()
     */
    public float sqrMag() {
        return x * x + y * y + z * z + w * w;
    }

    /**
     * Returns the distance of this vector and the received. Verify is if possible use {@link #sqrDist(Vector4)} instead
     * this method, because is more faster, this method calls the {@link #sqrDist(Vector4)} methods.
     *
     * @param other the vector to be used in the distance calculation (treated as a point)
     * @return the distance between this and the received vector
     * @see #sqrDist(Vector4)
     */
    public float dist(Vector4 other) {
        return (float) Math.sqrt(sqrDist(other));
    }

    /**
     * Returns the squared distance of this vector and the received. Is more faster than the {@link #dist(Vector4)}
     * method.
     *
     * @param other the vector to be used in the square distance calculation (treated as a point)
     * @return the square distance between this and the received vector
     * @see #dist(Vector4)
     */
    public float sqrDist(Vector4 other) {
        float dx = other.x - x;
        float dy = other.y - y;
        float dz = other.z - z;
        float dw = other.w - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    /**
     * Returns the dot product between this vector and the received.
     *
     * @param other the vector to be used in the dot calculation
     * @return the dot product between this and the received vector
     */
    public float dot(Vector4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    /**
     * Returns the cosine between this and the received vectors. The cosine is derived using the dot product formula
     * (see {@link #dot(Vector4)} method). This method uses the {@link #dot(Vector4)} and {@link #mag()} methods.
     *
     * @param other the vector to be used in the cosine calculation
     * @return the cosine between this and the received vectors
     * @see #dot(Vector4)
     * @see #mag()
     * @see #sin(Vector4)
     * @see #angle(Vector4)
     */
    public float cos(Vector4 other) {
        return dot(other) / mag() * other.mag();
    }

    /**
     * Returns the sine between the received vectors. Verify is is possible use the {@link #cos(Vector4)} method instead
     * this, because is more faster, this method uses the {@link #cos(Vector4)} method.
     *
     * @param other the vector to be used in the sine calculation.
     * @return the sine between this and the received vectors.
     * @see #cos(Vector4)
     * @see #angle(Vector4)
     */
    public float sin(Vector4 other) {
        float cos = cos(other);
        return (float) Math.sqrt(1 - cos * cos);
    }

    /**
     * Returns the angle (in radians) between this and the received vectors.
     * This method uses the cos() method and applies the arcCosine method
     *
     * @param other the vector to be used in the angle calculation.
     * @return the angle (in radians) between this and the received vectors.
     * @see #sin(Vector4)
     * @see #cos(Vector4)
     */
    public float angle(Vector4 other) {
        return (float) Math.acos(cos(other));
    }

    // Vector4 operations

    /**
     * Sums the received vector in this and return this vector modified. The received vector is not modified.
     *
     * @param other the vector to sum with this
     * @return this vector modified
     */
    public Vector4 sum(Vector4 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        w += other.w;
        return this;
    }

    /**
     * Subtracts the received vector of this and return this vector modified. The received vector is not modified.
     *
     * @param other the vector to subtract from this
     * @return this vector modified
     * @see #subI(Vector4)
     */
    public Vector4 sub(Vector4 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        w -= other.w;
        return this;
    }

    /**
     * Subtracts this vector from the received (inverted subtraction), but saving in this. The received vector is not
     * modified.
     *
     * @param other the vector to be subtracted from this
     * @return this vector modified
     * @see #sub(Vector4)
     */
    public Vector4 subI(Vector4 other) {
        x = other.x - x;
        y = other.y - y;
        z = other.z - z;
        w = other.w - w;
        return this;
    }

    /**
     * Multiplies each component of the received vector with the components of this vectors. The received vector is not
     * modified. The components are multiplied one by one.
     *
     * @param other the vector to multiply this
     * @return this vector modified
     * @see #scale(float)
     */
    public Vector4 mul(Vector4 other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        w *= other.w;
        return this;
    }

    /**
     * Scales each component of this vector with the received scalar.
     *
     * @param scalar the value to scale the components
     * @return the vector scaled by the received scalar
     * @see #mul(Vector4)
     * @see #negate()
     * @see #normalize()
     */
    public Vector4 scale(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    /**
     * Negates the components of this vector.
     *
     * @return this vector negated
     * @see #scale(float)
     */
    public Vector4 negate() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }

    /**
     * Returns this vector normalized, the {@link #scale(float)} and {@link #mag()} functions are called, each
     * components of this vector is divided by the magnitude, the result is a vector with the same direction but the
     * magnitude equals to 1.
     * <p>
     * If this vector is a null vector, an {@link UnsupportedOperationException} is throw.
     *
     * @return this vector normalized
     * @throws UnsupportedOperationException if the vector is null (magnitude equals 0)
     * @see #scale(float)
     */
    public Vector4 normalize() {
        float mag = mag();
        if (mag == 0) {
            throw new UnsupportedOperationException("Can not normalize a null vector.");
        }
        return scale(1 / mag());
    }

    /**
     * Projects this vector over the received vector, this vector receives the result.
     *
     * @param other the vector to project over
     * @return this vector modified
     * @see #projectI(Vector4)
     * @see #reject(Vector4)
     * @see #rejectI(Vector4)
     * @see #dot(Vector4)
     * @see #mag()
     */
    public Vector4 project(Vector4 other) {
        float scalar = dot(other) / other.sqrMag();
        x = other.x * scalar;
        y = other.y * scalar;
        z = other.z * scalar;
        w = other.w * scalar;
        return this;
    }

    /**
     * Projects the received vector on this vector, this vector receives the value.
     *
     * @param other the vector be projected
     * @return this vector modified
     * @see #project(Vector4)
     * @see #reject(Vector4)
     * @see #rejectI(Vector4)
     * @see #dot(Vector4)
     * @see #mag()
     */
    public Vector4 projectI(Vector4 other) {
        float scalar = other.dot(this) / sqrMag();
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    /**
     * Returns the rejection of this vector over the received. The rejection vector is the vector from the projection
     * subtracting the project vector.
     *
     * @param other the vector to reject over
     * @return this vector modified
     * @see #rejectI(Vector4)
     * @see #project(Vector4)
     * @see #projectI(Vector4)
     * @see #dot(Vector4)
     * @see #mag()
     */
    public Vector4 reject(Vector4 other) {
        float scalar = dot(other) / other.sqrMag();
        x -= other.x * scalar;
        y -= other.y * scalar;
        z -= other.z * scalar;
        w -= other.w * scalar;
        return this;
    }

    /**
     * Returns the rejection of the received vector over this. The rejection vector is the vector from the projection
     * subtracting the project vector.
     *
     * @param other the vector to reject over
     * @return this vector modified (received the rejection result)
     * @see #reject(Vector4)
     * @see #project(Vector4)
     * @see #projectI(Vector4)
     * @see #dot(Vector4)
     * @see #mag()
     */
    public Vector4 rejectI(Vector4 other) {
        float scalar = other.dot(this) / sqrMag();
        x = other.x - x * scalar;
        y = other.y - y * scalar;
        z = other.z - z * scalar;
        w = other.w - w * scalar;
        return this;
    }

    /**
     * Returns the interpolation between the received vectors. This method too can be used to extrapolate vectors.
     * <p>
     * If the gradient is equals to 0, this vector is the result
     * <p>
     * If the gradient is equals to 1, the received vector is the result
     * <p>
     * If the gradient is less than 0 or greater than 1, the vector is outside the interval between this and the
     * received vector.
     *
     * @param other    the vector to interpolate with this
     * @param gradient the interpolation value
     * @return this vector modified
     * @see #orientate(Vector4, float)
     */
    public Vector4 interpolate(Vector4 other, float gradient) {
        x = x * (1 - gradient) + other.x * gradient;
        y = y * (1 - gradient) + other.y * gradient;
        z = z * (1 - gradient) + other.z * gradient;
        w = w * (1 - gradient) + other.w * gradient;
        return this;
    }

    /**
     * Returns this vector summed with the other multiplied by the scalar, the received vector is treated as a
     * direction vector, and this as the origin point vector.
     *
     * @param other  the direction vector
     * @param scalar the direction scalar
     * @return this vector modified
     * @see #interpolate(Vector4, float)
     */
    public Vector4 orientate(Vector4 other, float scalar) {
        x += other.x * scalar;
        y += other.y * scalar;
        z += other.z * scalar;
        w += other.w * scalar;
        return this;
    }

    /**
     * Returns this vector scaled by the received scalar and summed with the received vector. This vector is treated as
     * a direction vector, and the received as the origin point vector.
     *
     * @param other  the origin point vector
     * @param scalar the direction scalar
     * @return this vector modified
     * @see #interpolate(Vector4, float)
     */
    public Vector4 orientateI(Vector4 other, float scalar) {
        x = other.x + x * scalar;
        y = other.y + y * scalar;
        z = other.z + z * scalar;
        w = other.w + w * scalar;
        return this;
    }

    // Static vector operations

    /**
     * This method has the same behaviour of the instance method {@link #sum(Vector4)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the sum between the received vectors
     * @see #sum(Vector4)
     */
    public static Vector4 sum(Vector4 v1, Vector4 v2) {
        return v1.copy().sum(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #sub(Vector4)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the subtraction of the first over the second vector
     * @see #sub(Vector4)
     */
    public static Vector4 sub(Vector4 v1, Vector4 v2) {
        return v1.copy().sub(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #mul(Vector4)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the component multiplication between the received vectors
     * @see #mul(Vector4)
     */
    public static Vector4 mul(Vector4 v1, Vector4 v2) {
        return v1.copy().mul(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #scale(float)}, but does not modify the received
     * vector.
     *
     * @param v the vector to scale
     * @param s the scalar
     * @return a copy of the received vector scaled
     * @see #scale(float)
     */
    public static Vector4 scale(Vector4 v, float s) {
        return v.copy().scale(s);
    }

    /**
     * This method has the same behaviour of the instance method {@link #negate()}, but does not modify the received
     * vector.
     *
     * @param v the vector to normalize
     * @return a copy of the received vector normalized
     * @see #negate()
     */
    public static Vector4 negate(Vector4 v) {
        return v.copy().negate();
    }

    /**
     * This method has the same behaviour of the instance method {@link #normalize()}, but does not modify the received
     * vector.
     *
     * @param v the vector to normalize
     * @return a copy of the received vector normalized
     * @see #normalize()
     */
    public static Vector4 normalize(Vector4 v) {
        return v.copy().normalize();
    }

    /**
     * This method has the same behaviour of the instance method {@link #project(Vector4)}, but does not modify the
     * received vectors.
     *
     * @param v1 the projection vector
     * @param v2 the base vector
     * @return the projection of the first vector over the second
     * @see #project(Vector4)
     */
    public static Vector4 project(Vector4 v1, Vector4 v2) {
        return v1.copy().project(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #reject(Vector4)}, but does not modify the
     * received vectors.
     *
     * @param v1 the projection vector
     * @param v2 the base vector
     * @return the rejection of the first vector over the second
     * @see #reject(Vector4)
     */
    public static Vector4 reject(Vector4 v1, Vector4 v2) {
        return v1.copy().reject(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #interpolate(Vector4, float)}, but does not
     * modify the received vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @param g  the gradient value
     * @return the interpolation (or extrapolation) of the received vectors and the gradient
     * @see #interpolate(Vector4, float)
     */
    public static Vector4 interpolate(Vector4 v1, Vector4 v2, float g) {
        return v1.copy().interpolate(v2, g);
    }

    /**
     * This method has the same behaviour of the instance method {@link #orientate(Vector4, float)}, but does not modify
     * the received vectors.
     *
     * @param v1 the origin vector
     * @param v2 the direction vector
     * @param s  the direction scalar
     * @return the origin vector summed with the direction vector scaled
     * @see #orientate(Vector4, float)
     */
    public static Vector4 orientate(Vector4 v1, Vector4 v2, float s) {
        return v1.copy().orientate(v2, s);
    }

    // Static default vectors

    /**
     * Returns a 0 vector.
     *
     * @return a 0 vector
     */
    public static Vector4 lZero() {
        return linear(0, 0, 0);
    }

    /**
     * Returns an affine 0 vector.
     *
     * @return an affine 0 vector
     */
    public static Vector4 aZero() {
        return affine(0, 0, 0);
    }

    /**
     * Returns a 1 vector.
     *
     * @return a 1 vector
     */
    public static Vector4 lOne() {
        return linear(1, 1, 1);
    }

    /**
     * Returns an affine 1 vector.
     *
     * @return an affine 1 vector
     */
    public static Vector4 aOne() {
        return affine(1, 1, 1);
    }

    /**
     * Returns a left vector.
     *
     * @return a left vector
     */
    public static Vector4 lLeft() {
        return linear(-1, 0, 0);
    }

    /**
     * Returns an affine left vector.
     *
     * @return an affine left vector
     */
    public static Vector4 aLeft() {
        return affine(-1, 0, 0);
    }

    /**
     * Returns a right vector.
     *
     * @return a right vector
     */
    public static Vector4 lRight() {
        return linear(1, 0, 0);
    }

    /**
     * Returns an affine right vector.
     *
     * @return an affine right vector
     */
    public static Vector4 aRight() {
        return affine(1, 0, 0);
    }

    /**
     * Returns a down vector.
     *
     * @return a down vector
     */
    public static Vector4 lDown() {
        return linear(0, -1, 0);
    }

    /**
     * Returns an affine down vector.
     *
     * @return an affine down vector
     */
    public static Vector4 aDown() {
        return affine(0, -1, 0);
    }

    /**
     * Returns a up vector.
     *
     * @return a up vector
     */
    public static Vector4 lUp() {
        return linear(0, 1, 0);
    }

    /**
     * Returns an affine up vector.
     *
     * @return an affine up vector
     */
    public static Vector4 aUp() {
        return affine(0, 1, 0);
    }

    /**
     * Returns a back vector.
     *
     * @return a back vector
     */
    public static Vector4 lBack() {
        return linear(0, 0, -1);
    }

    /**
     * Returns an affine back vector.
     *
     * @return an affine back vector
     */
    public static Vector4 aBack() {
        return affine(0, 0, -1);
    }

    /**
     * Returns a forward vector.
     *
     * @return a forward vector
     */
    public static Vector4 lForward() {
        return linear(0, 0, 1);
    }

    /**
     * Returns an affine forward vector.
     *
     * @return an affine forward vector
     */
    public static Vector4 aForward() {
        return affine(0, 0, 1);
    }

    // Static vector generators

    /**
     * Creates a linear vector with the received components, thw W component is set to 0.
     *
     * @param x the X component value
     * @param y the Y component value
     * @param z the Z component value
     * @return a linear vector
     */
    public static Vector4 linear(float x, float y, float z) {
        return new Vector4(x, y, z, 0);
    }

    /**
     * Creates an affine vector with the received components, thw W component is set to 1.
     *
     * @param x the X component value
     * @param y the Y component value
     * @param z the Z component value
     * @return an affine vector
     */
    public static Vector4 affine(float x, float y, float z) {
        return new Vector4(x, y, z, 1);
    }
}

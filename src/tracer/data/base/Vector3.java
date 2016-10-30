package tracer.data.base;

import tracer.util.Copyable;

/**
 * Represents a 3-dimension vector used to store positions and transforms.
 * <p>
 * About the methods in this class:
 * <p>
 * The instance methods that returns a Vector3 (except for the {@link #copy()} method) will always change the instance
 * that calls the method and returns itself.
 * <p>
 * For each instance method that returns a Vector3, exists an equivalent static method that returns a modified copy,
 * except for the current methods:
 * <p>
 * {@link #copy()}, the {@link #set(float, float, float)}, {@link #subI(Vector3)}, {@link #crossI(Vector3)},
 * {@link #projectI(Vector3)}, {@link #rejectI(Vector3)} and {@link #orientateI(Vector3, float)}
 *
 * @author Pedro Henrique
 */
public class Vector3 implements Copyable<Vector3> {

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
     * Creates a vector with 0 in all components.
     */
    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Creates a vector with the received components.
     *
     * @param x the X component value
     * @param y ths Y component value
     * @param z the Z component value
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Float.compare(vector3.x, x) == 0 && Float.compare(vector3.y, y) == 0 && Float.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    /**
     * Sets the received values in the vector and returns this vector modified.
     *
     * @param x the X component value
     * @param y the Y component value
     * @param z the Z component value
     * @return this vector modified.
     */
    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
        return x * x + y * y + z * z;
    }

    /**
     * Returns the distance of this vector and the received. Verify is if possible use {@link #sqrDist(Vector3)} instead
     * this method, because is more faster, this method calls the {@link #sqrDist(Vector3)} methods.
     *
     * @param other the vector to be used in the distance calculation (treated as a point)
     * @return the distance between this and the received vector
     * @see #sqrDist(Vector3)
     */
    public float dist(Vector3 other) {
        return (float) Math.sqrt(sqrDist(other));
    }

    /**
     * Returns the squared distance of this vector and the received. Is more faster than the {@link #dist(Vector3)}
     * method.
     *
     * @param other the vector to be used in the square distance calculation (treated as a point)
     * @return the square distance between this and the received vector
     * @see #dist(Vector3)
     */
    public float sqrDist(Vector3 other) {
        float dx = other.x - x;
        float dy = other.y - y;
        float dz = other.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Returns the dot product between this vector and the received.
     *
     * @param other the vector to be used in the dot calculation
     * @return the dot product between this and the received vector
     */
    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Returns the cosine between this and the received vectors. The cosine is derived using the dot product formula
     * (see {@link #dot(Vector3)} method). This method uses the {@link #dot(Vector3)} and {@link #mag()} methods.
     *
     * @param other the vector to be used in the cosine calculation
     * @return the cosine between this and the received vectors
     * @see #dot(Vector3)
     * @see #mag()
     * @see #sin(Vector3)
     * @see #angle(Vector3)
     */
    public float cos(Vector3 other) {
        return dot(other) / mag() * other.mag();
    }

    /**
     * Returns the sine between the received vectors. Verify is is possible use the {@link #cos(Vector3)} method instead
     * this, because is more faster, this method uses the {@link #cos(Vector3)} method.
     *
     * @param other the vector to be used in the sine calculation.
     * @return the sine between this and the received vectors.
     * @see #cos(Vector3)
     * @see #angle(Vector3)
     */
    public float sin(Vector3 other) {
        float cos = cos(other);
        return (float) Math.sqrt(1 - cos * cos);
    }

    /**
     * Returns the angle (in radians) between this and the received vectors.
     * This method uses the cos() method and applies the arcCosine method
     *
     * @param other the vector to be used in the angle calculation.
     * @return the angle (in radians) between this and the received vectors.
     * @see #sin(Vector3)
     * @see #cos(Vector3)
     */
    public float angle(Vector3 other) {
        return (float) Math.acos(cos(other));
    }

    // Vector3 operations

    /**
     * Sums the received vector in this and return this vector modified. The received vector is not modified.
     *
     * @param other the vector to sum with this
     * @return this vector modified
     */
    public Vector3 sum(Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    /**
     * Subtracts the received vector of this and return this vector modified. The received vector is not modified.
     *
     * @param other the vector to subtract from this
     * @return this vector modified
     * @see #subI(Vector3)
     */
    public Vector3 sub(Vector3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        return this;
    }

    /**
     * Subtracts this vector from the received (inverted subtraction), but saving in this. The received vector is not
     * modified.
     *
     * @param other the vector to be subtracted from this
     * @return this vector modified
     * @see #sub(Vector3)
     */
    public Vector3 subI(Vector3 other) {
        x = other.x - x;
        y = other.y - y;
        z = other.z - z;
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
    public Vector3 mul(Vector3 other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }

    /**
     * Scales each component of this vector with the received scalar.
     *
     * @param scalar the value to scale the components
     * @return the vector scaled by the received scalar
     * @see #mul(Vector3)
     * @see #negate()
     * @see #normalize()
     */
    public Vector3 scale(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Negates the components of this vector.
     *
     * @return this vector negated
     * @see #scale(float)
     */
    public Vector3 negate() {
        x = -x;
        y = -y;
        z = -z;
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
    public Vector3 normalize() {
        float mag = mag();
        if (mag == 0) {
            throw new UnsupportedOperationException("Can not normalize a null vector.");
        }
        return scale(1 / mag());
    }

    /**
     * Returns the cross product between this and the received vectors. The received vector is not modified, this
     * vector received the cross product result. This cross is left-hand oriented.
     *
     * @param other the vector to calculate the cross product with this
     * @return this vector modified
     */
    public Vector3 cross(Vector3 other) {
        return set(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    /**
     * Returns the cross product between this and the received vectors (inverted cross product). The received vector
     * is not modified, this vector received the cross product result. This cross is right-hand oriented.
     *
     * @param other the vector to calculate the cross product with this
     * @return this vector modified
     */
    public Vector3 crossI(Vector3 other) {
        return set(
                other.y * this.z - other.z * this.y,
                other.z * this.x - other.x * this.z,
                other.x * this.y - other.y - this.x
        );
    }

    /**
     * Projects this vector over the received vector, this vector receives the result.
     *
     * @param other the vector to project over
     * @return this vector modified
     * @see #projectI(Vector3)
     * @see #reject(Vector3)
     * @see #rejectI(Vector3)
     * @see #dot(Vector3)
     * @see #mag()
     */
    public Vector3 project(Vector3 other) {
        float scalar = dot(other) / other.sqrMag();
        x = other.x * scalar;
        y = other.y * scalar;
        z = other.z * scalar;
        return this;
    }

    /**
     * Projects the received vector on this vector, this vector receives the value.
     *
     * @param other the vector be projected
     * @return this vector modified
     * @see #project(Vector3)
     * @see #reject(Vector3)
     * @see #rejectI(Vector3)
     * @see #dot(Vector3)
     * @see #mag()
     */
    public Vector3 projectI(Vector3 other) {
        float scalar = other.dot(this) / sqrMag();
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Returns the rejection of this vector over the received. The rejection vector is the vector from the projection
     * subtracting the project vector.
     *
     * @param other the vector to reject over
     * @return this vector modified
     * @see #rejectI(Vector3)
     * @see #project(Vector3)
     * @see #projectI(Vector3)
     * @see #dot(Vector3)
     * @see #mag()
     */
    public Vector3 reject(Vector3 other) {
        float scalar = dot(other) / other.sqrMag();
        x -= other.x * scalar;
        y -= other.y * scalar;
        z -= other.z * scalar;
        return this;
    }

    /**
     * Returns the rejection of the received vector over this. The rejection vector is the vector from the projection
     * subtracting the project vector.
     *
     * @param other the vector to reject over
     * @return this vector modified (received the rejection result)
     * @see #reject(Vector3)
     * @see #project(Vector3)
     * @see #projectI(Vector3)
     * @see #dot(Vector3)
     * @see #mag()
     */
    public Vector3 rejectI(Vector3 other) {
        float scalar = other.dot(this) / sqrMag();
        x = other.x - x * scalar;
        y = other.y - y * scalar;
        z = other.z - z * scalar;
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
     * @see #orientate(Vector3, float)
     */
    public Vector3 interpolate(Vector3 other, float gradient) {
        x = x * (1 - gradient) + other.x * gradient;
        y = y * (1 - gradient) + other.y * gradient;
        z = z * (1 - gradient) + other.z * gradient;
        return this;
    }

    /**
     * Returns this vector summed with the other multiplied by the scalar, the received vector is treated as a
     * direction vector, and this as the origin point vector.
     *
     * @param other  the direction vector
     * @param scalar the direction scalar
     * @return this vector modified
     * @see #interpolate(Vector3, float)
     */
    public Vector3 orientate(Vector3 other, float scalar) {
        x += other.x * scalar;
        y += other.y * scalar;
        z += other.z * scalar;
        return this;
    }

    /**
     * Returns this vector scaled by the received scalar and summed with the received vector. This vector is treated as
     * a direction vector, and the received as the origin point vector.
     *
     * @param other  the origin point vector
     * @param scalar the direction scalar
     * @return this vector modified
     * @see #interpolate(Vector3, float)
     */
    public Vector3 orientateI(Vector3 other, float scalar) {
        x = other.x + x * scalar;
        y = other.y + y * scalar;
        z = other.z + z * scalar;
        return this;
    }

    // Static vector operations

    /**
     * This method has the same behaviour of the instance method {@link #sum(Vector3)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the sum between the received vectors
     * @see #sum(Vector3)
     */
    public static Vector3 sum(Vector3 v1, Vector3 v2) {
        return v1.copy().sum(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #sub(Vector3)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the subtraction of the first over the second vector
     * @see #sub(Vector3)
     */
    public static Vector3 sub(Vector3 v1, Vector3 v2) {
        return v1.copy().sub(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #mul(Vector3)}, but does not modify the received
     * vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @return the component multiplication between the received vectors
     * @see #mul(Vector3)
     */
    public static Vector3 mul(Vector3 v1, Vector3 v2) {
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
    public static Vector3 scale(Vector3 v, float s) {
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
    public static Vector3 negate(Vector3 v) {
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
    public static Vector3 normalize(Vector3 v) {
        return v.copy().normalize();
    }

    /**
     * This method has the same behaviour of the instance method {@link #cross(Vector3)}, but does not modify the
     * received vectors.
     *
     * @param v1 the vector to calculate the cross product
     * @param v2 the vector to calculate the cross product
     * @return the cross product of the received vectors
     * @see #cross(Vector3)
     */
    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return v1.copy().cross(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #project(Vector3)}, but does not modify the
     * received vectors.
     *
     * @param v1 the projection vector
     * @param v2 the base vector
     * @return the projection of the first vector over the second
     * @see #project(Vector3)
     */
    public static Vector3 project(Vector3 v1, Vector3 v2) {
        return v1.copy().project(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #reject(Vector3)}, but does not modify the
     * received vectors.
     *
     * @param v1 the projection vector
     * @param v2 the base vector
     * @return the rejection of the first vector over the second
     * @see #reject(Vector3)
     */
    public static Vector3 reject(Vector3 v1, Vector3 v2) {
        return v1.copy().reject(v2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #interpolate(Vector3, float)}, but does not
     * modify the received vectors.
     *
     * @param v1 the vector to operate
     * @param v2 the vector to operate
     * @param g  the gradient value
     * @return the interpolation (or extrapolation) of the received vectors and the gradient
     * @see #interpolate(Vector3, float)
     */
    public static Vector3 interpolate(Vector3 v1, Vector3 v2, float g) {
        return v1.copy().interpolate(v2, g);
    }

    /**
     * This method has the same behaviour of the instance method {@link #orientate(Vector3, float)}, but does not modify
     * the received vectors.
     *
     * @param v1 the origin vector
     * @param v2 the direction vector
     * @param s  the direction scalar
     * @return the origin vector summed with the direction vector scaled
     * @see #orientate(Vector3, float)
     */
    public static Vector3 orientate(Vector3 v1, Vector3 v2, float s) {
        return v1.copy().orientate(v2, s);
    }

    // Static default vectors

    /**
     * Returns a 0 vector.
     *
     * @return a 0 vector
     */
    public static Vector3 zero() {
        return new Vector3(0, 0, 0);
    }

    /**
     * Returns a 1 vector.
     *
     * @return a 1 vector
     */
    public static Vector3 one() {
        return new Vector3(1, 1, 1);
    }

    /**
     * Returns a left vector.
     *
     * @return a left vector
     */
    public static Vector3 left() {
        return new Vector3(-1, 0, 0);
    }

    /**
     * Returns a right vector.
     *
     * @return a right vector
     */
    public static Vector3 right() {
        return new Vector3(1, 0, 0);
    }

    /**
     * Returns a down vector.
     *
     * @return a down vector
     */
    public static Vector3 down() {
        return new Vector3(0, -1, 0);
    }

    /**
     * Returns a up vector.
     *
     * @return a up vector
     */
    public static Vector3 up() {
        return new Vector3(0, 1, 0);
    }

    /**
     * Returns a back vector.
     *
     * @return a back vector
     */
    public static Vector3 back() {
        return new Vector3(0, 0, -1);
    }

    /**
     * Returns a forward vector.
     *
     * @return a forward vector
     */
    public static Vector3 forward() {
        return new Vector3(0, 0, 1);
    }
}

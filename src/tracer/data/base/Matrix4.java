package tracer.data.base;

import tracer.util.Copyable;

import java.util.Arrays;

/**
 * Represents a squared 4x4 matrix used in transforms.
 * <p>
 * About the methods in this class:
 * <p>
 * The instance methods that returns a Matrix4 (except for the {@link #copy()} method) will always change the instance
 * that calls the method and returns itself.
 * <p>
 * For each instance method that returns a Matrix4, exists an equivalent static method that returns a modified copy,
 * except for the methods:
 * <p>
 * {@link #copy()} , all {@link #get}s and {@link #set}s, {@link #subI(Matrix4)} and {@link #mulI(Matrix4)}
 * <p>
 * The static method {@link #compose(Matrix4...)} does not exits in instance methods.
 *
 * @author Pedro Henrique
 */
public class Matrix4 implements Copyable<Matrix4> {

    /**
     * The array with the matrix components.
     */
    private float[] data;

    /**
     * The size of the matrix data.
     */
    protected static final int DATA_SIZE = 16;

    /**
     * The size of the matrix line or column.
     */
    protected static final int DIM_SIZE = 4;

    /**
     * Creates a matrix with 0 in all positions.
     */
    public Matrix4() {
        data = new float[DATA_SIZE];
    }

    /**
     * Creates a matrix with the received components.
     *
     * @param d00 the component in the first line and column
     * @param d01 the component in the first line and second column
     * @param d02 the component in the first line and third column
     * @param d03 the component in the first line and fourth column
     * @param d10 the component in the second line and first column
     * @param d11 the component in the second line and column
     * @param d12 the component in the second line and third column
     * @param d13 the component in the second line and fourth column
     * @param d20 the component in the third line and first column
     * @param d21 the component in the third line and second column
     * @param d22 the component in the third line and column
     * @param d23 the component in the third line and fourth column
     * @param d30 the component in the fourth line and first column
     * @param d31 the component in the fourth line and second column
     * @param d32 the component in the fourth line and third column
     * @param d33 the component in the fourth line and column
     */
    public Matrix4(float d00, float d01, float d02, float d03,
                   float d10, float d11, float d12, float d13,
                   float d20, float d21, float d22, float d23,
                   float d30, float d31, float d32, float d33) {
        data = new float[16];
        data[0] = d00;
        data[1] = d01;
        data[2] = d02;
        data[3] = d03;
        data[4] = d10;
        data[5] = d11;
        data[6] = d12;
        data[7] = d13;
        data[8] = d20;
        data[9] = d21;
        data[10] = d22;
        data[11] = d23;
        data[12] = d30;
        data[13] = d31;
        data[14] = d32;
        data[15] = d33;
    }

    /**
     * This constructor allows create a matrix from the received array data. It's unsafe and only can be used in this
     * class.
     *
     * @param data the matrix data
     */
    private Matrix4(float[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix4 Matrix4 = (Matrix4) o;
        return Arrays.equals(data, Matrix4.data);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "|" + data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3] + "|\n"
                + "|" + data[4] + ", " + data[5] + ", " + data[6] + ", " + data[7] + "|\n"
                + "|" + data[8] + ", " + data[9] + ", " + data[10] + ", " + data[11] + "|\n"
                + "|" + data[12] + ", " + data[13] + ", " + data[14] + ", " + data[15] + "|";
    }

    @Override
    public Matrix4 copy() {
        return new Matrix4(Arrays.copyOf(data, DATA_SIZE));
    }

    /**
     * Gets the element in the received matrix index.
     *
     * @param index the matrix index (between 0 and 15)
     * @return the element in the received index
     * @throws ArrayIndexOutOfBoundsException if the index is less than 0 or greater than 15
     */
    public float get(int index) {
        return data[index];
    }

    /**
     * Gets the element in the received matrix line and column.
     *
     * @param line   the matrix line (between 0 and 3)
     * @param column the matrix column (between 0 and 3)
     * @return the element in the received line and column
     * @throws IndexOutOfBoundsException if the line is less than 0 or greater than 3 depending of the column value
     */
    public float get(int line, int column) {
        int index = line * 4 + column;
        return data[index];
    }

    /**
     * Sets the received value in the matrix received index.
     *
     * @param v     the value to set
     * @param index the matrix index (between 0 and 15)
     * @return this matrix modified
     * @throws ArrayIndexOutOfBoundsException if the index is less than 0 or greater than 15
     */
    public Matrix4 set(float v, int index) {
        data[index] = v;
        return this;
    }

    /**
     * Sets the received value in the matrix received index.
     *
     * @param v      the value to set
     * @param line   the matrix line (between 0 and 3)
     * @param column the matrix column (between 0 and 3)
     * @return this matrix modified
     * @throws IndexOutOfBoundsException if the line is less than 0 or greater than 3 depending of the column value
     */
    public Matrix4 set(float v, int line, int column) {
        int index = line * 4 + column;
        data[index] = v;
        return this;
    }

    /**
     * Sets all the received components in this matrix and returns.
     *
     * @param d00 the component in the first line and column
     * @param d01 the component in the first line and second column
     * @param d02 the component in the first line and third column
     * @param d03 the component in the first line and fourth column
     * @param d10 the component in the second line and first column
     * @param d11 the component in the second line and column
     * @param d12 the component in the second line and third column
     * @param d13 the component in the second line and fourth column
     * @param d20 the component in the third line and first column
     * @param d21 the component in the third line and second column
     * @param d22 the component in the third line and column
     * @param d23 the component in the third line and fourth column
     * @param d30 the component in the fourth line and first column
     * @param d31 the component in the fourth line and second column
     * @param d32 the component in the fourth line and third column
     * @param d33 the component in the fourth line and column
     */
    public Matrix4 set(float d00, float d01, float d02, float d03,
                       float d10, float d11, float d12, float d13,
                       float d20, float d21, float d22, float d23,
                       float d30, float d31, float d32, float d33) {
        data[0] = d00;
        data[1] = d01;
        data[2] = d02;
        data[3] = d03;
        data[4] = d10;
        data[5] = d11;
        data[6] = d12;
        data[7] = d13;
        data[8] = d20;
        data[9] = d21;
        data[10] = d22;
        data[11] = d23;
        data[12] = d30;
        data[13] = d31;
        data[14] = d32;
        data[15] = d33;
        return this;
    }

    /**
     * This method allows set the matrix data from the received array data. It's unsafe and only can be used in this
     * package.
     *
     * @param data the matrix data
     */
    Matrix4 set(float[] data) {
        this.data = data;
        return this;
    }

    // Data operations

    /**
     * Calculates and returns the matrix determinant using the gaussian elimination.
     *
     * @return the matrix determinant
     */
    public float det() {
        Matrix4 gaussian = copy().gaussian();
        return gaussian.data[0] * gaussian.data[5] * gaussian.data[10] * gaussian.data[15];
    }

    // Vector operations

    /**
     * Multiplies this matrix with the received {@link Vector4} returning the resultant vector.
     *
     * @param vector the vector to transform
     * @return the transformed vector
     */
    public Vector4 transform(Vector4 vector) {
        return new Vector4(
                data[0] * vector.x + data[1] * vector.y + data[2] * vector.z + data[3] * vector.w,
                data[4] * vector.x + data[5] * vector.y + data[6] * vector.z + data[7] * vector.w,
                data[8] * vector.x + data[9] * vector.y + data[10] * vector.z + data[11] * vector.w,
                data[12] * vector.x + data[13] * vector.y + data[14] * vector.z + data[15] * vector.w
        );
    }

    /**
     * Multiplies this matrix with the received {@link Vector3} returning the resultant vector, the last line of this
     * matrix is ignored and the 3 remaining elements of the last column are summed if the new vector after the
     * transform.
     *
     * @param vector the vector to transform
     * @return the transformed vector
     */
    public Vector3 transformAsPoint(Vector3 vector) {
        return new Vector3(
                data[0] * vector.x + data[1] * vector.y + data[2] * vector.z + data[3],
                data[4] * vector.x + data[5] * vector.y + data[6] * vector.z + data[7],
                data[8] * vector.x + data[9] * vector.y + data[10] * vector.z + data[11]
        );
    }

    /**
     * Multiplies this matrix with the received {@link Vector3} returning the resultant vector, the last line and column
     * of the matrix are ignored.
     *
     * @param vector the vector to transform
     * @return the transformed vector
     */
    public Vector3 transformAsDirection(Vector3 vector) {
        return new Vector3(
                data[0] * vector.x + data[1] * vector.y + data[2] * vector.z,
                data[4] * vector.x + data[5] * vector.y + data[6] * vector.z,
                data[8] * vector.x + data[9] * vector.y + data[10] * vector.z
        );
    }

    // Matrix operations

    /**
     * Sums this matrix with the received, each element is summed with the equivalent element in the received matrix.
     *
     * @param other the matrix to sum with this
     * @return this matrix modified
     */
    public Matrix4 sum(Matrix4 other) {
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] += other.data[i];
        }
        return this;
    }

    /**
     * Subtracts this matrix with the received, each element is subtracted with the equivalent element in the received
     * matrix.
     *
     * @param other the matrix to subtract this
     * @return this matrix modified
     */
    public Matrix4 sub(Matrix4 other) {
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] -= other.data[i];
        }
        return this;
    }

    /**
     * Subtracts the received matrix from this, each element subtracts the equivalent element in the received matrix.
     *
     * @param other the matrix to be subtract from this
     * @return this matrix modified
     */
    public Matrix4 subI(Matrix4 other) {
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = other.data[i] - data[i];
        }
        return this;
    }

    /**
     * Multiplies this matrix with the received (at right).
     *
     * @param other the matrix to multiply
     * @return this matrix modified
     */
    public Matrix4 mul(Matrix4 other) {
        float[] resultData = new float[DATA_SIZE];
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    resultData[i * DIM_SIZE + j] += data[i * DIM_SIZE + k] * other.data[k * DIM_SIZE + j];
                }
            }
        }
        return set(resultData);
    }

    /**
     * Multiplies this matrix with the received (at left).
     *
     * @param other the matrix to multiply
     * @return this matrix modified
     */
    public Matrix4 mulI(Matrix4 other) {
        float[] resultData = new float[DATA_SIZE];
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    resultData[i * DIM_SIZE + j] += other.data[k * DIM_SIZE + j] * data[i * DIM_SIZE + k];
                }
            }
        }
        return set(resultData);
    }

    /**
     * Scale this matrix by the received scalar. Each component is multiplied.
     *
     * @param scalar the scalar to multiply the matrix
     * @return this matrix modified
     */
    public Matrix4 scale(float scalar) {
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] *= scalar;
        }
        return this;
    }

    /**
     * Transpose this matrix.
     *
     * @return this matrix modified
     */
    public Matrix4 transpose() {
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = i + 1; j < DIM_SIZE; j++) {
                int index1 = i * DIM_SIZE + j;
                int index2 = j * DIM_SIZE + i;
                float aux = data[index1];
                data[index1] = data[index2];
                data[index2] = aux;
            }
        }
        return this;
    }

    /**
     * Transform this matrix to the gaussian form.
     *
     * @return this matrix modified
     */
    public Matrix4 gaussian() {
        for (int pivot = 0; pivot < DIM_SIZE; pivot++) {
            boolean hasPivot = true;
            if (data[pivot * DIM_SIZE + pivot] == 0) {
                for (int line = pivot + 1; line < DIM_SIZE; line++) {
                    if (data[line * DIM_SIZE + pivot] != 0) {
                        swapLines(pivot, line);
                        break;
                    }
                }
                hasPivot = false;
            }
            if (hasPivot) {
                for (int line = pivot + 1; line < DIM_SIZE; line++) {
                    scaleAndSumLines(line, pivot, data[line * DIM_SIZE + pivot] / data[pivot * DIM_SIZE + pivot]);
                }
            }
        }
        return this;
    }

    /**
     * Transform this matrix in the inverse, if the matrix does not have inverse, this matrix is not modified.
     *
     * @return this matrix modified
     */
    public Matrix4 inverse() {
        Matrix4 backup = copy();
        Matrix4 inverse = identity();
        float auxScalar;
        for (int pivot = 0; pivot < DIM_SIZE; pivot++) {
            boolean hasPivot = true;
            if (data[pivot * DIM_SIZE + pivot] == 0) {
                for (int line = pivot + 1; line < DIM_SIZE; line++) {
                    if (data[line * DIM_SIZE + pivot] != 0) {
                        swapLines(pivot, line);
                        inverse.swapLines(pivot, line);
                        break;
                    }
                }
                hasPivot = false;
            }
            if (hasPivot) {
                for (int line = pivot + 1; line < DIM_SIZE; line++) {
                    auxScalar = data[line * DIM_SIZE + pivot] / data[pivot * DIM_SIZE + pivot];
                    scaleAndSumLines(line, pivot, auxScalar);
                    inverse.scaleAndSumLines(line, pivot, auxScalar);
                }
            } else {
                data = backup.data;
                throw new UnsupportedOperationException();
            }
        }
        for (int pivot = DIM_SIZE - 1; pivot >= 0; pivot--) {
            auxScalar = 1 / data[pivot * DIM_SIZE + pivot];
            scaleLine(pivot, auxScalar);
            inverse.scaleLine(pivot, auxScalar);
            for (int line = pivot - 1; line >= 0; line--) {
                auxScalar = -data[line * DIM_SIZE + pivot];
                scaleAndSumLines(line, pivot, auxScalar);
                inverse.scaleAndSumLines(line, pivot, auxScalar);
            }
        }
        data = inverse.data;
        return this;
    }

    // Gaussian and Invert support functions

    /**
     * Scale the line of the received index by the received scalar.
     *
     * @param lineIndex the line index
     * @param scalar    the line scalar
     */
    private void scaleLine(int lineIndex, float scalar) {
        int lineArrayIndex = lineIndex * DIM_SIZE;
        data[lineArrayIndex] *= scalar;
        data[lineArrayIndex + 1] *= scalar;
        data[lineArrayIndex + 2] *= scalar;
        data[lineArrayIndex + 3] *= scalar;
    }

    /**
     * Swap the lines in the received indexes.
     *
     * @param lineIndex1 the index of the first line to swap
     * @param lineIndex2 the index of the second line to swap
     */
    private void swapLines(int lineIndex1, int lineIndex2) {
        int lineArrayIndex1 = lineIndex1 * DIM_SIZE;
        int lineArrayIndex2 = lineIndex2 * DIM_SIZE;
        float aux;
        aux = data[lineArrayIndex1];
        data[lineArrayIndex1] = data[lineArrayIndex2];
        data[lineArrayIndex2] = aux;
        aux = data[lineArrayIndex1 + 1];
        data[lineArrayIndex1 + 1] = data[lineArrayIndex2 + 1];
        data[lineArrayIndex2 + 1] = aux;
        aux = data[lineArrayIndex1 + 1];
        data[lineArrayIndex1 + 2] = data[lineArrayIndex2 + 1];
        data[lineArrayIndex2 + 2] = aux;
        aux = data[lineArrayIndex1 + 1];
        data[lineArrayIndex1 + 3] = data[lineArrayIndex2 + 1];
        data[lineArrayIndex2 + 3] = aux;
    }

    /**
     * Scale the line of the scalarLineIndex by the received scalar (without modify the line) ans sums with the line
     * of the baseLineIndex (this is modified).
     *
     * @param baseLineIndex   the base line index (modified)
     * @param scalarLineIndex the scalar line index (not modified)
     * @param scalar          the scalar of the line to scale
     */
    private void scaleAndSumLines(int baseLineIndex, int scalarLineIndex, float scalar) {
        int baseArrayIndex = baseLineIndex * DIM_SIZE;
        int scalarArrayIndex = scalarLineIndex * DIM_SIZE;
        data[baseArrayIndex] += data[scalarArrayIndex] * scalar;
        data[baseArrayIndex + 1] += data[scalarArrayIndex + 1] * scalar;
        data[baseArrayIndex + 2] += data[scalarArrayIndex + 2] * scalar;
        data[baseArrayIndex + 3] += data[scalarArrayIndex + 3] * scalar;
    }

    // Static matrix operations

    /**
     * This method has the same behaviour of the instance method {@link #sum(Matrix4)}, but does not modify the received
     * matrices.
     *
     * @param m1 the matrix to operate
     * @param m2 the matrix to operate
     * @return the sum of the received matrices
     * @see #sum(Matrix4)
     */
    public static Matrix4 sum(Matrix4 m1, Matrix4 m2) {
        return m1.copy().sum(m2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #sub(Matrix4)}, but does not modify the received
     * matrices.
     *
     * @param m1 the matrix to operate
     * @param m2 the matrix to operate
     * @return the subtraction of the first matrix by the second
     * @see #sub(Matrix4)
     * @see #subI(Matrix4)
     */
    public static Matrix4 sub(Matrix4 m1, Matrix4 m2) {
        return m1.copy().sub(m2);
    }

    /**
     * This method has the same behaviour of the instance method {@link #mul(Matrix4)}, but does not modify the received
     * matrices.
     *
     * @param m1 the matrix to operate
     * @param m2 the matrix to operate
     * @return the multiplication of  the first matrix (at left) by the second (at right)
     * @see #mul(Matrix4)
     * @see #mulI(Matrix4)
     */
    public static Matrix4 mul(Matrix4 m1, Matrix4 m2) {
        return m1.copy().mul(m2);
    }

    /**
     * This method multiplies all received matrices from left to right. This method does not have an equivalent
     * instance method.
     *
     * @param ms the matrices to multiply
     * @return the multiplication of the received matrices
     * @see #mul(Matrix4)
     * @see #mulI(Matrix4)
     * @see #mul(Matrix4, Matrix4)
     */
    public static Matrix4 compose(Matrix4... ms) {
        if (ms.length == 0) {
            throw new IllegalArgumentException();
        }
        Matrix4 result = ms[0].copy();
        for (int i = 1; i < ms.length; i++) {
            result.mul(ms[i]);
        }
        return result;
    }

    /**
     * This method has the same behaviour of the instance method {@link #scale(float)}, but does not modify the received
     * matrix.
     *
     * @param m the matrix to scale
     * @param s the matrix scalar
     * @return the received matrix copy multiplied by the scalar
     * @see #scale(float)
     */
    public static Matrix4 scale(Matrix4 m, float s) {
        return m.copy().scale(s);
    }

    /**
     * This method has the same behaviour of the instance method {@link #transpose()}, but does not modify the received
     * matrix.
     *
     * @param m the matrix to transpose
     * @return a transposed copy of the received matrix
     * @see #transpose()
     */
    public static Matrix4 transpose(Matrix4 m) {
        return m.copy().transpose();
    }

    /**
     * This method has the same behaviour of the instance method {@link #gaussian()}, but does not modify the received
     * matrix.
     *
     * @param m the matrix to apply the gaussian elimination
     * @return a copy of the received matrix in the gaussian form
     * @see #gaussian()
     */
    public static Matrix4 gaussian(Matrix4 m) {
        return m.copy().gaussian();
    }

    /**
     * This method has the same behaviour of the instance method {@link #inverse()}, but does not modify the received
     * matrix.
     *
     * @param m the matrix to invert
     * @return a copy of the received matrix inverted
     * @see #inverse()
     */
    public static Matrix4 inverse(Matrix4 m) {
        return m.copy().inverse();
    }


    // Static default matrices

    /**
     * Returns the identity matrix.
     *
     * @return the identity matrix
     */
    public static Matrix4 identity() {
        return new Matrix4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    // Static homogeneous coordinate matrix generators

    /**
     * Creates and returns a translation matrix with the received translation values.
     *
     * @param x the translation in the x component
     * @param y the translation in the y component
     * @param z the translation in the z component
     * @return a translation matrix
     */
    public static Matrix4 translation(float x, float y, float z) {
        return new Matrix4(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1);
    }


    /**
     * Creates and returns a x rotation matrix with the received angle (in radians).
     *
     * @param angle the rotation angle (in radians)
     * @return a x rotation matrix
     */
    public static Matrix4 rotationX(float angle) {
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        return new Matrix4(1, 0, 0, 0, 0, cos, -sin, 0, 0, sin, cos, 0, 0, 0, 0, 1);
    }

    /**
     * Creates and returns a y rotation matrix with the received angle (in radians).
     *
     * @param angle the rotation angle (in radians)
     * @return a y rotation matrix
     */
    public static Matrix4 rotationY(float angle) {
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        return new Matrix4(cos, 0, -sin, 0, 0, 1, 0, 0, sin, 0, cos, 0, 0, 0, 0, 1);
    }

    /**
     * Creates and returns a z rotation matrix with the received angle (in radians).
     *
     * @param angle the rotation angle (in radians)
     * @return a z rotation matrix
     */
    public static Matrix4 rotationZ(float angle) {
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        return new Matrix4(cos, -sin, 0, 0, sin, cos, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    /**
     * Creates and returns a rotation matrix with the received angles (in radians). The rotation is in the
     * counterclockwise. To invert a rotation transform, just transpose the matrix. The rotation is applied first in
     * the x axis, after in the y axis and finish in the z axis.
     *
     * @param xAngle the x rotation angle (in radians)
     * @param yAngle the z rotation angle (in radians)
     * @param zAngle the z rotation angle (in radians)
     * @return a rotation matrix
     */
    public static Matrix4 rotation(float xAngle, float yAngle, float zAngle) {
        return compose(rotationZ(zAngle), rotationY(yAngle), rotationX(xAngle));
    }

    /**
     * Returns a rotation matrix that rotates around the received vector by the received angle. If the axis is the null
     * vector a zero matrix is returned.
     *
     * @param axis  the rotation axis
     * @param angle the rotation angle
     * @return the rotation matrix
     */
    public static Matrix4 rotationAround(Vector3 axis, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float axisX2 = axis.x * axis.x;
        float axisY2 = axis.y * axis.y;
        float axisZ2 = axis.z * axis.z;
        float axisXY = axis.x * axis.y;
        float axisXZ = axis.x * axis.z;
        float axisYZ = axis.y * axis.z;
        return new Matrix4(
                cos + axisX2 * (1 - cos), axisXY * (1 - cos) - axis.z * (1 - sin), axis.y * sin + axisXZ * (1 - cos), 0,
                axis.z * sin + axisXY * (1 - cos), cos + axisY2 * (1 - cos), -axis.x * sin + axisYZ * (1 - cos), 0,
                -axis.y * sin + axisXZ * (1 - cos), axis.x * sin + axisYZ * (1 - cos), cos + axisZ2 * (1 - cos), 0,
                0, 0, 0, 1
        );
    }

    /**
     * Returns the rotation between two vectors.
     * @param from the from direction vector
     * @param to the to direction vector
     * @return the rotation matrix
     */
    public static Matrix4 rotationBetween(Vector3 from, Vector3 to) {
        if (from.dot(to) == 0) {
            return identity();
        }
        Vector3 axis = Vector3.cross(from, to);
        float angle = from.cos(to);
        return rotationAround(axis, angle);
    }

    /**
     * Creates and returns a scale matrix with the received scalars.
     *
     * @param xScalar the x scalar
     * @param yScalar the y scalar
     * @param zScalar the z scalar
     * @return the scalar matrix.
     */
    public static Matrix4 scaleMatrix(float xScalar, float yScalar, float zScalar) {
        return new Matrix4(xScalar, 0, 0, 0, 0, yScalar, 0, 0, 0, 0, zScalar, 0, 0, 0, 0, 1);
    }
}

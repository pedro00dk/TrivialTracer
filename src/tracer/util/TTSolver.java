package tracer.util;

/**
 * @author Pedro Henrique
 */
public final class TTSolver {

    private TTSolver() {
    }

    /**
     * Solves the equation ax^2+bx+c=0. Solutions are returned in a sorted array
     * if they exist.
     *
     * @param a coefficient of x^2
     * @param b coefficient of x^1
     * @param c coefficient of x^0
     * @return an array containing the two real roots, or <code>null</code> if
     * no real solutions exist
     */
    public static float[] solveQuadric(float a, float b, float c) {
        float disc = b * b - 4 * a * c;
        if (disc < 0)
            return null;
        disc = (float) Math.sqrt(disc);
        float q = ((b < 0) ? -0.5f * (b - disc) : -0.5f * (b + disc));
        float t0 = q / a;
        float t1 = c / q;
        // return sorted array
        return (t0 > t1) ? new float[]{t1, t0} : new float[]{t0, t1};
    }

    /**
     * Solve a quartic equation of the form ax^4+bx^3+cx^2+cx^1+d=0. The roots
     * are returned in a sorted array of doubles in increasing order.
     *
     * @param a coefficient of x^4
     * @param b coefficient of x^3
     * @param c coefficient of x^2
     * @param d coefficient of x^1
     * @param e coefficient of x^0
     * @return a sorted array of roots, or <code>null</code> if no solutions
     * exist
     */
    public static float[] solveQuartic(float a, float b, float c, float d, float e) {
        float inva = 1 / a;
        float c1 = b * inva;
        float c2 = c * inva;
        float c3 = d * inva;
        float c4 = e * inva;
        // cubic resolvant
        float c12 = c1 * c1;
        float p = -0.375f * c12 + c2;
        float q = 0.125f * c12 * c1 - 0.5f * c1 * c2 + c3;
        float r = -0.01171875f * c12 * c12 + 0.0625f * c12 * c2 - 0.25f * c1 * c3 + c4;
        float z = solveCubicForQuartic(-0.5f * p, -r, 0.5f * r * p - 0.125f * q * q);
        float d1 = 2.0f * z - p;
        if (d1 < 0) {
            if (d1 > 1.0e-10f)
                d1 = 0;
            else
                return null;
        }
        float d2;
        if (d1 < 1.0e-10) {
            d2 = z * z - r;
            if (d2 < 0)
                return null;
            d2 = (float) Math.sqrt(d2);
        } else {
            d1 = (float) Math.sqrt(d1);
            d2 = 0.5f * q / d1;
        }
        // setup usefull values for the quadratic factors
        float q1 = d1 * d1;
        float q2 = -0.25f * c1;
        float pm = q1 - 4 * (z - d2);
        float pp = q1 - 4 * (z + d2);
        if (pm >= 0 && pp >= 0) {
            // 4 roots (!)
            pm = (float) Math.sqrt(pm);
            pp = (float) Math.sqrt(pp);
            float[] results = new float[4];
            results[0] = -0.5f * (d1 + pm) + q2;
            results[1] = -0.5f * (d1 - pm) + q2;
            results[2] = 0.5f * (d1 + pp) + q2;
            results[3] = 0.5f * (d1 - pp) + q2;
            // tiny insertion sort
            for (int i = 1; i < 4; i++) {
                for (int j = i; j > 0 && results[j - 1] > results[j]; j--) {
                    float t = results[j];
                    results[j] = results[j - 1];
                    results[j - 1] = t;
                }
            }
            return results;
        } else if (pm >= 0) {
            pm = (float) Math.sqrt(pm);
            float[] results = new float[2];
            results[0] = -0.5f * (d1 + pm) + q2;
            results[1] = -0.5f * (d1 - pm) + q2;
            return results;
        } else if (pp >= 0) {
            pp = (float) Math.sqrt(pp);
            float[] results = new float[2];
            results[0] = 0.5f * (d1 - pp) + q2;
            results[1] = 0.5f * (d1 + pp) + q2;
            return results;
        }
        return null;
    }

    /**
     * Return only one root for the specified cubic equation. This routine is
     * only meant to be called by the quartic solver. It assumes the cubic is of
     * the form: x^3+px^2+qx+r.
     *
     * @param p
     * @param q
     * @param r
     * @return
     */
    private static float solveCubicForQuartic(float p, float q, float r) {
        float A2 = p * p;
        float Q = (A2 - 3.0f * q) / 9.0f;
        float R = (p * (A2 - 4.5f * q) + 13.5f * r) / 27.0f;
        float Q3 = Q * Q * Q;
        float R2 = R * R;
        float d = Q3 - R2;
        float an = p / 3.0f;
        if (d >= 0) {
            d = R / (float) Math.sqrt(Q3);
            float theta = (float) Math.acos(d) / 3.0f;
            float sQ = -2.0f * (float) Math.sqrt(Q);
            return sQ * (float) Math.cos(theta) - an;
        } else {
            float sQ = (float) Math.pow(Math.sqrt(R2 - Q3) + Math.abs(R), 1.0 / 3.0);
            if (R < 0)
                return (sQ + Q / sQ) - an;
            else
                return -(sQ + Q / sQ) - an;
        }
    }
}

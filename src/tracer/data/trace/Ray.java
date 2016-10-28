package tracer.data.trace;

import tracer.data.base.Vector3;

/**
 * Represents a ray, with an origin and direction vectors, this vectors are used in hit checking. Due the low lifetime
 * of the objects of this class, the access can be done directly in the instance fields.
 *
 * @author Pedro Henrique
 */
public class Ray {

    /**
     * The origin of this ray.
     */
    public Vector3 origin;

    /**
     * The direction of this ray.
     */
    public Vector3 direction;

    /**
     * Creates a ray with the received origin and direction vectors, the vectors are not copied, the vector instances
     * should be controlled by other application levels, it can provide possible performance improvements. The direction
     * vector should be normalized.
     *
     * @param origin    the origin of this ray
     * @param direction the direction of this ray, the direction magnitude should be greater than 0
     */
    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction;
    }
}

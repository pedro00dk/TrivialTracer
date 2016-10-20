package tracer.data.trace;

import tracer.data.Vector3;
import tracer.data.material.Color;
import tracer.model.Model;

/**
 * Represents a hit information about a ray casting in a model. Instances of this class should only be created when a
 * hit happens. The objects of this class has little life time (as the Ray class),  so the access to the fields are
 * public, be careful with null fields and improper modifications.
 *
 * @author Pedro Henrique
 * @see Ray
 */
public class Hit {

    /**
     * The model where the ray was hit.
     */
    public Model model;

    /**
     * The ray used in the intersection calculation information.
     */
    public Ray ray;

    /**
     * The ray distance to the hit point.
     */
    public float distance;

    /**
     * The point where the ray touches the model.
     */
    public Vector3 point;

    /**
     * The model normal in the hit point.
     */
    public Vector3 normal;

    /**
     * The calculated color in the hit point.
     */
    public Color color;

    /**
     * @param ray      the ray used.
     * @param distance the ray direction scalar to generate the point.
     * @param point    the model hit point.
     * @param normal   the normal in the hit point (should be normalized).
     * @param color    the calculated color in the hit point.
     */
    public Hit(Model model, Ray ray, float distance, Vector3 point, Vector3 normal, Color color) {
        this.model = model;
        this.ray = ray;
        this.distance = distance;
        this.point = point;
        this.normal = normal;
        this.color = color;
    }
}

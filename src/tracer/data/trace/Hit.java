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
     * The ray direction scalar, used to calculate the hitPoint.
     */
    public float rayDirectionScalar;

    /**
     * The point where the ray touches the model.
     */
    public Vector3 hitPoint;

    /**
     * The model normal in the hit point (should be normalized).
     */
    public Vector3 hitNormal;

    /**
     * The calculated color in the hit point.
     */
    public Color hitColor;

    /**
     * @param ray                the ray used.
     * @param rayDirectionScalar the ray direction scalar to generate the hitPoint.
     * @param hitPoint           the model hit point.
     * @param hitNormal          the normal in the hit point (should be normalized).
     * @param hitColor           the calculated color in the hit point.
     */
    public Hit(Model model, Ray ray, float rayDirectionScalar, Vector3 hitPoint, Vector3 hitNormal, Color hitColor) {
        this.model = model;
        this.ray = ray;
        this.rayDirectionScalar = rayDirectionScalar;
        this.hitPoint = hitPoint;
        this.hitNormal = hitNormal;
        this.hitColor = hitColor;
    }
}

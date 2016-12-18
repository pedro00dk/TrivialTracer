package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Material;
import tracer.util.Copyable;

/**
 * The interface for any model type, specifies methods to check intersections with rays and get some properties.
 *
 * @author Pedro Henrique
 * @see Ray
 * @see Hit
 * @see Material
 */
public interface Model extends Copyable<Model> {

    /**
     * Returns the center of the model (depends of the implementation, should be specified in the override method), the
     * returned center is independent of the model, if modified, the model will not be modified.
     *
     * @return the center of the model
     */
    Vector3 getCenter();

    /**
     * Returns aleatory surface points with uniform probability of the model, if the count is less than 1, a zero sized
     * vector array is returned.
     *
     * @param count the number of points to get
     * @return points of the surface
     */
    Vector3[] getSurfacePoints(int count);

    /**
     * Gets the material of this model.
     *
     * @return the material of this model
     */
    Material getMaterial();

    /**
     * Sets the material of this model.
     *
     * @return the material of this model
     */
    Model setMaterial(Material material);

    /**
     * Returns if a ray intersects this model.
     *
     * @param ray the ray to test the intersection
     * @return the hit information if the ray hits this model, null if the ray misses this model
     */
    Hit intersect(Ray ray);
}

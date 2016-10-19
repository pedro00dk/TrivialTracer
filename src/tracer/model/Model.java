package tracer.model;

import tracer.data.Vector3;
import tracer.data.material.Material;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
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
     * Returns the center point of the model.
     *
     * @return the center of the model
     */
    Vector3 getCenter();

    /**
     * Returns aleatory surface points of the model.
     *
     * @return points of the surface
     */
    Vector3[] getSurfacePoints();

    /**
     * Returns aleatory surface points thats can be visible from the received direction.
     *
     * @param direction the model direction (from the center of the model) were the points are visible
     * @return visible points of the surface
     */
    Vector3[] getVisibleSurfacePoints(Vector3 direction);

    /**
     * Gets the material of this model.
     *
     * @return the material of this model
     */
    Material getMaterial();

    /**
     * Returns if a ray intersects this model.
     *
     * @param ray the ray to test the intersection
     * @return the hit information if the ray hits this model, null if the ray misses this model
     */
    Hit intersect(Ray ray);
}

package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.model.material.Material;

import java.util.Objects;
import java.util.Random;

/**
 * Simple sphere {@link Model} implementation, contains the sphere center position and the radius.
 *
 * @author Pedro Henrique
 */
public class Sphere implements Model {

    /**
     * The center of the sphere.
     */
    private Vector3 center;

    /**
     * The radius of the sphere.
     */
    private float radius;

    /**
     * The visual of this sphere.
     */
    private Material material;

    /**
     * The default center of the sphere.
     */
    private static final Vector3 DEFAULT_CENTER = Vector3.zero();

    /**
     * The default radius of the sphere.
     */
    private static final float DEFAULT_RADIUS = 1;

    /**
     * The default visual of the sphere.
     */
    private static final Material DEFAULT_MATERIAL = new Material();

    /**
     * Create the sphere in origin (0, 0, 0) with radius 1 and default visual.
     */
    public Sphere() {
        this(DEFAULT_CENTER, DEFAULT_RADIUS, DEFAULT_MATERIAL);
    }

    /**
     * Create the sphere with the received center position and radius 1 and default visual.
     *
     * @param center the sphere center position
     */
    public Sphere(Vector3 center) {
        this(center, DEFAULT_RADIUS, DEFAULT_MATERIAL);
    }

    /**
     * Create the sphere with the center in the origin (0, 0, 0) and the received radius, should be greater than 0
     * and default visual (new Material()).
     *
     * @param radius the sphere radius
     */
    public Sphere(float radius) {
        this(DEFAULT_CENTER, radius, DEFAULT_MATERIAL);
    }

    /**
     * Create the sphere with the received center position and radius, should be greater than 0, and default visual.
     *
     * @param center the sphere center position
     * @param radius the sphere radius
     */
    public Sphere(Vector3 center, float radius) {
        this(center, radius, DEFAULT_MATERIAL);
    }

    /**
     * Create the sphere with the received center position and radius, should be greater than 0  and the received
     * visual (cannot be null).
     *
     * @param center   the sphere center position
     * @param radius   the sphere radius
     * @param material the sphere visual
     */
    public Sphere(Vector3 center, float radius, Material material) {
        this.center = Objects.requireNonNull(center, "The center can not be null.");
        if (radius <= 0) {
            throw new IllegalArgumentException("The radius should be greater than 0.");
        }
        this.radius = radius;
        this.material = Objects.requireNonNull(material);
    }

    @Override
    public Model copy() {
        return new Sphere(center.copy(), radius, material.copy());
    }

    @Override
    public Vector3 getCenter() {
        return center;
    }

    @Override
    public Vector3[] getSurfacePoints(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("The count should be greater than 0.");
        }
        Random prng = new Random();
        Vector3[] surfacePoints = new Vector3[count];
        for (int i = 0; i < count; i++) {
            float longitude = 2 * (float) Math.PI * prng.nextFloat();
            float latitude = (float) Math.acos(2 * prng.nextFloat() - 1);
            surfacePoints[i] = new Vector3(
                    (float) (radius * Math.sin(latitude) * Math.cos(longitude)),
                    (float) (radius * Math.sin(latitude) * Math.sin(longitude)),
                    (float) (radius * Math.cos(latitude))).sum(center);
        }
        return surfacePoints;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Hit intersect(Ray ray) {
        Vector3 l = Vector3.sub(center, ray.origin);
        float tca = l.dot(ray.direction);
        if (tca < 0) return null;
        float d2 = l.dot(l) - tca * tca;
        float radius2 = radius * radius;
        if (d2 > radius2) return null;
        float thc = (float) Math.sqrt(radius2 - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;
        float minT = 0;
        Vector3 hitPoint = null;
        Vector3 hitNormal = null;
        if (t0 < t1) {
            if (t0 >= 0) {
                minT = t0;
            } else if (t1 >= 0) {
                minT = t1;
            } else {
                return null;
            }
        } else {
            if (t1 >= 0) {
                minT = t1;
            } else if (t0 >= 0) {
                minT = t0;
            } else {
                return null;
            }
        }
        hitPoint = Vector3.orientate(ray.origin, ray.direction, minT);
        hitNormal = Vector3.sub(hitPoint, center).normalize();
        return new Hit(this, ray, minT, hitPoint, hitNormal, material.getSurfaceColor());
    }
}

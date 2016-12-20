package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Material;
import tracer.model.bounds.BoundBox;
import tracer.util.TTRand;

import java.util.Objects;

/**
 * Simple sphere {@link Model} implementation, contains the sphere center position and the radius.
 *
 * @author Pedro Henrique
 */
public class Sphere extends AbstractModel {

    /**
     * The center of the sphere.
     */
    protected Vector3 center;

    /**
     * The radius of the sphere.
     */
    protected float radius;

    /**
     * The model surface points.
     */
    protected Vector3[] surfacePoints;

    /**
     * The internal bound box of this model.
     */
    private BoundBox boundBox;

    // The default attributes of the spheres
    private static final Vector3 DEFAULT_CENTER = Vector3.zero();
    private static final float DEFAULT_RADIUS = 1;

    // The number of points in the surface of the model
    private static final int SURFACE_POINTS_COUNT = 1000;

    /**
     * Create the sphere in origin (0, 0, 0) with radius 1 and default material.
     */
    public Sphere() {
        this(DEFAULT_CENTER, DEFAULT_RADIUS);
    }

    /**
     * Create the sphere with the received center position and radius 1 and default material.
     *
     * @param center the sphere center position
     */
    public Sphere(Vector3 center) {
        this(center, DEFAULT_RADIUS);
    }

    /**
     * Create the sphere with the center in the origin (0, 0, 0) and the received radius, should be greater than 0
     * and default material (new Material()).
     *
     * @param radius the sphere radius
     */
    public Sphere(float radius) {
        this(DEFAULT_CENTER, radius);
    }

    /**
     * Create the sphere with the received center position and radius, should be greater than 0, and default material.
     *
     * @param center the sphere center position
     * @param radius the sphere radius
     */
    public Sphere(Vector3 center, float radius) {
        this(center, radius, DEFAULT_MATERIAL);
    }

    /**
     * Create the sphere with the received center position and radius, should be greater than 0  and the received
     * material (cannot be null).
     *
     * @param center   the sphere center position
     * @param radius   the sphere radius
     * @param material the sphere material
     */
    public Sphere(Vector3 center, float radius, Material material) {
        super(material);
        this.center = Objects.requireNonNull(center, "The center can not be null.").copy();
        if (radius <= 0) {
            throw new IllegalArgumentException("The radius should be greater than 0.");
        }
        this.radius = radius;
        boundBox = new BoundBox(
                Vector3.sum(center, Vector3.one().scale(radius)),
                Vector3.sub(center, Vector3.one().scale(radius))
        );
        surfacePoints = new Vector3[SURFACE_POINTS_COUNT];
        for (int i = 0; i < SURFACE_POINTS_COUNT; i++) {
            surfacePoints[i] = TTRand.onUniformSphere().scale(radius).sum(center);
        }
    }

    @Override
    public Model copy() {
        return new Sphere(center, radius, material);
    }

    @Override
    public Vector3 getCenter() {
        return center.copy();
    }

    @Override
    public Vector3[] getSurfacePoints(int count) {
        Vector3[] surfacePoints = new Vector3[count];
        for (int i = 0; i < count; i++) {
            surfacePoints[i] = this.surfacePoints[TTRand.range(0, this.surfacePoints.length - 1)];
        }
        return surfacePoints;
    }

    @Override
    public Hit intersect(Ray ray) {
        if (!boundBox.intersect(ray)) {
            return null;
        }
        Vector3 l = Vector3.sub(center, ray.origin);
        float tca = l.dot(ray.direction);
        if (tca < 0) return null;
        float d2 = l.dot(l) - tca * tca;
        float radius2 = radius * radius;
        if (d2 > radius2) return null;
        float thc = (float) Math.sqrt(radius2 - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;
        float minT;
        Vector3 hitPoint;
        Vector3 hitNormal;
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

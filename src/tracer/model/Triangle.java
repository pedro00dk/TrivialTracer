package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Material;
import tracer.model.bounds.BoundBox;
import tracer.util.TTRand;

import java.util.Objects;

/**
 * Simple triangle {@link Model} implementation, contains triangle vertices and normals.
 *
 * @author Pedro Henrique
 */
public class Triangle extends AbstractModel {

    /**
     * The first vertex of the triangle.
     */
    protected Vector3 vertex0;

    /**
     * The second vertex of the triangle.
     */
    protected Vector3 vertex1;

    /**
     * The third vertex of the triangle.
     */
    protected Vector3 vertex2;

    /**
     * The model surface points.
     */
    protected Vector3[] surfacePoints;

    /**
     * The internal bound box of this model.
     */
    private BoundBox boundBox;

    // This parameter is used to help intersection checks.
    private static final float BOUND_SCALE = 1e-4f;

    // The number of points in the surface of the model
    private static final int SURFACE_POINTS_COUNT = 1000;

    /*
     * The constant used to ignore intersections when the inclination between the ray direction and the triangle normal
     * is next to 90 degrees.
     */
    private static final float EPSILON = 1e-4f;

    /**
     * Creates the triangle with the received vertices (should not be null). The triangles is created with the default
     * material.
     *
     * @param vertex0 the first vertex of the triangle
     * @param vertex1 the second vertex of the triangle
     * @param vertex2 the third vertex of the triangle
     */
    public Triangle(Vector3 vertex0, Vector3 vertex1, Vector3 vertex2) {
        this(vertex0, vertex1, vertex2, DEFAULT_MATERIAL);
    }

    /**
     * Creates the triangle with the received vertices and material (should not be null).
     *
     * @param vertex0  the first vertex of the triangle
     * @param vertex1  the second vertex of the triangle
     * @param vertex2  the third vertex of the triangle
     * @param material the triangle material
     */
    public Triangle(Vector3 vertex0, Vector3 vertex1, Vector3 vertex2, Material material) {
        super(material);
        this.vertex0 = Objects.requireNonNull(vertex0, "The triangle vertices can not be null.").copy();
        this.vertex1 = Objects.requireNonNull(vertex1, "The triangle vertices can not be null.").copy();
        this.vertex2 = Objects.requireNonNull(vertex2, "The triangle vertices can not be null.").copy();
        Vector3 minVector = new Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Vector3 maxVector = new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        minVector.min(this.vertex0).min(this.vertex1).min(this.vertex2);
        maxVector.max(this.vertex0).max(this.vertex1).max(this.vertex2);
        boundBox = new BoundBox(
                minVector.sub(Vector3.one().scale(BOUND_SCALE)),
                maxVector.sum(Vector3.one().scale(BOUND_SCALE))
        );
        surfacePoints = new Vector3[SURFACE_POINTS_COUNT];
        for (int i = 0; i < SURFACE_POINTS_COUNT; i++) {
            float random1 = TTRand.floatValue();
            float random2 = TTRand.floatValue();
            if (random1 + random2 > 1) {
                random1 = 1 - random1;
                random2 = 1 - random2;
            }
            surfacePoints[i] = Vector3.zero()
                    .sum(Vector3.scale(vertex0, 1 - random1 - random2))
                    .sum(Vector3.scale(vertex1, random1))
                    .sum(Vector3.scale(vertex2, random2));
        }
    }

    @Override
    public Model copy() {
        return new Triangle(vertex0.copy(), vertex1.copy(), vertex2.copy(), material.copy());
    }

    /**
     * Returns the centroid of this triangle.
     *
     * @return the center of the triangle
     */
    @Override
    public Vector3 getCenter() {
        return Vector3.zero().sum(vertex0).sum(vertex1).sum(vertex2).scale(1f / 3f);
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
        //return insideOutsideIntersect(ray);
        return mollerTrumboreIntersect(ray);
    }

    /**
     * Calculates the intersection using the inside-outside algorithm.
     *
     * @param ray the ray to check intersection
     * @return the hit information
     */
    private Hit insideOutsideIntersect(Ray ray) {
        if (!boundBox.intersect(ray)) {
            return null;
        }
        Vector3 normal = Vector3.cross(Vector3.sub(vertex2, vertex0), Vector3.sub(vertex1, vertex0)).normalize();
        float planeDistance = normal.dot(vertex0);
        float denominator = normal.dot(ray.direction);
        if (denominator < EPSILON && denominator > -EPSILON) {
            return null;
        }
        float distance = -(normal.dot(ray.origin) - planeDistance) / denominator;
        if (distance < 0) {
            return null;
        }
        Vector3 point = Vector3.orientate(ray.origin, ray.direction, distance);
        Vector3 edge0 = Vector3.sub(vertex1, vertex0);
        Vector3 vp0 = Vector3.sub(point, vertex0);
        Vector3 c0 = Vector3.cross(vp0, edge0);
        if (normal.dot(c0) < 0) {
            return null;
        }
        Vector3 edge1 = Vector3.sub(vertex2, vertex1);
        Vector3 vp1 = Vector3.sub(point, vertex1);
        Vector3 c1 = Vector3.cross(vp1, edge1);
        if (normal.dot(c1) < 0) {
            return null;
        }
        Vector3 edge2 = Vector3.sub(vertex0, vertex2);
        Vector3 vp2 = Vector3.sub(point, vertex2);
        Vector3 c2 = Vector3.cross(vp2, edge2);
        if (normal.dot(c2) < 0) {
            return null;
        }
        return new Hit(this, ray, distance, point, normal, material.getSurfaceColor());
    }

    /**
     * Calculates the intersection using the Moller-Trumbore algorithm. This algorithm is faster and use less memory
     * than {@link #insideOutsideIntersect(Ray)}.
     *
     * @param ray the ray to check intersection
     * @return the hit information
     */
    private Hit mollerTrumboreIntersect(Ray ray) {
        Vector3 edge1 = Vector3.sub(vertex1, vertex0);
        Vector3 edge2 = Vector3.sub(vertex2, vertex0);
        Vector3 p = Vector3.cross(ray.direction, edge2);
        float determinant = edge1.dot(p);
        if (determinant > -EPSILON && determinant < EPSILON) {
            return null;
        }
        float inverseDeterminant = 1 / determinant;
        Vector3 t = Vector3.sub(ray.origin, vertex0);
        float u = t.dot(p) * inverseDeterminant;
        if (u < 0 || u > 1) {
            return null;
        }
        Vector3 q = Vector3.cross(t, edge1);
        float v = ray.direction.dot(q) * inverseDeterminant;
        if (v < 0 || u + v > 1) {
            return null;
        }
        float rayDistance = edge2.dot(q) * inverseDeterminant;
        if (rayDistance > EPSILON) { //ray intersection
            return new Hit(this, ray, rayDistance, Vector3.orientate(ray.origin, ray.direction, rayDistance),
                    Vector3.cross(edge2, edge1).normalize(), material.getSurfaceColor());
        }
        return null;
    }
}

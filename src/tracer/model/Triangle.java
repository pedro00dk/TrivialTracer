package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.model.material.Material;

import java.util.Objects;
import java.util.Random;

/**
 * Simple triangle {@link Model} implementation, contains triangle vertices and normals.
 *
 * @author Pedro Henrique
 */
public class Triangle extends AbstractModel {

    /**
     * The first vertex of the triangle.
     */
    private Vector3 vertex0;

    /**
     * The second vertex of the triangle.
     */
    private Vector3 vertex1;

    /**
     * The third vertex of the triangle.
     */
    private Vector3 vertex2;

    /**
     * Creates the triangle with the received vertices (should not be null). The triangles is created with the default
     * material.
     *
     * @param vertex0 the first vertex of the triangle
     * @param vertex1 the second vertex of the triangle
     * @param vertex2 the third vertex of the triangle
     */
    public Triangle(Vector3 vertex0, Vector3 vertex1, Vector3 vertex2) {
        super();
        this.vertex0 = Objects.requireNonNull(vertex0, "The triangle vertices can not be null.");
        this.vertex1 = Objects.requireNonNull(vertex1, "The triangle vertices can not be null.");
        this.vertex2 = Objects.requireNonNull(vertex2, "The triangle vertices can not be null.");
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
        this.vertex0 = Objects.requireNonNull(vertex0, "The triangle vertices can not be null.");
        this.vertex1 = Objects.requireNonNull(vertex1, "The triangle vertices can not be null.");
        this.vertex2 = Objects.requireNonNull(vertex2, "The triangle vertices can not be null.");
    }

    @Override
    public Model copy() {
        return new Triangle(vertex0.copy(), vertex1.copy(), vertex2.copy(), material.copy());
    }

    @Override
    public Vector3 getCenter() {
        return Vector3.zero().sum(vertex0).sum(vertex1).sum(vertex2).scale(1f / 3f);
    }

    @Override
    public Vector3[] getSurfacePoints(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("The count should be greater than 0.");
        }
        Random prng = new Random();
        Vector3[] surfacePoints = new Vector3[count];
        for (int i = 0; i < count; i++) {
            float random1 = prng.nextFloat();
            float random2 = prng.nextFloat();
            if (random1 + random2 > 1) {
                random1 = 1 - random1;
                random2 = 1 - random2;
            }
            surfacePoints[i] = Vector3.zero()
                    .sum(Vector3.scale(vertex0, 1 - random1 - random2))
                    .sum(Vector3.scale(vertex1, random1))
                    .sum(Vector3.scale(vertex2, random2));
        }
        return surfacePoints;
    }

    @Override
    public Hit intersect(Ray ray) {
        Vector3 planeNormal = Vector3.cross(Vector3.sub(vertex2, vertex0), Vector3.sub(vertex1, vertex0)).normalize();
        float planeDistance = planeNormal.dot(vertex0);
        float denominator = planeNormal.dot(ray.direction);
        if (denominator < 0.0001f && denominator > -0.0001f) {
            return null;
        }
        float rayDistance = -(planeNormal.dot(ray.origin) - planeDistance) / denominator;
        if (rayDistance < 0.0001f) {
            return null;
        }
        Vector3 point = Vector3.orientate(ray.origin, ray.direction, rayDistance);
        // Inside outside test
        // edge 0
        Vector3 edge0 = Vector3.sub(vertex1, vertex0);
        Vector3 vp0 = Vector3.sub(point, vertex0);
        Vector3 c0 = Vector3.cross(vp0, edge0);
        if (planeNormal.dot(c0) < 0) {
            return null;
        }
        // edge 1
        Vector3 edge1 = Vector3.sub(vertex2, vertex1);
        Vector3 vp1 = Vector3.sub(point, vertex1);
        Vector3 c1 = Vector3.cross(vp1, edge1);
        if (planeNormal.dot(c1) < 0) {
            return null; // P is on the right side
        }
        // edge 2
        Vector3 edge2 = Vector3.sub(vertex0, vertex2);
        Vector3 vp2 = Vector3.sub(point, vertex2);
        Vector3 c2 = Vector3.cross(vp2, edge2);
        if (planeNormal.dot(c2) < 0) {
            return null; // P is on the right side;
        }
        return new Hit(this, ray, rayDistance, point, planeNormal, material.getSurfaceColor());
    }
}

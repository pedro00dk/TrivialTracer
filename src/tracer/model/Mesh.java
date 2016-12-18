package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Material;
import tracer.model.bounds.BoundBox;
import tracer.util.TTRand;

import java.util.Objects;

/**
 * Represents a mesh of triangles. All triangles contains the same material.
 *
 * @author Pedro Henrique
 */
public class Mesh extends AbstractModel {

    /**
     * The mesh triangles.
     */
    protected Triangle[] triangles;

    /**
     * The internal bound box of this model.
     */
    private BoundBox boundBox;

    // This parameter is used to help intersection checks.
    private static final float BOUND_SCALE = 1e-4f;

    /**
     * Creates a mesh with the received triangles and the default material.
     *
     * @param triangles the mesh triangles
     */
    public Mesh(Triangle[] triangles) {
        this(triangles, DEFAULT_MATERIAL);
    }

    /**
     * Creates a mesh with the received triangles material.
     *
     * @param triangles the mesh triangles
     * @param material  the mesh material
     */
    public Mesh(Triangle[] triangles, Material material) {
        super(material);
        Objects.requireNonNull("The triangles can not be null.");
        if (triangles.length == 0) {
            throw new IllegalArgumentException("The triangles can not be zero sized.");
        }
        this.triangles = new Triangle[triangles.length];
        Vector3 minVector = new Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Vector3 maxVector = new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        for (int i = 0; i < triangles.length; i++) {
            Objects.requireNonNull(triangles[i], "The triangles can not contains null elements.");
            this.triangles[i] = (Triangle) triangles[i].copy();
            minVector.min(this.triangles[i].vertex0).min(this.triangles[i].vertex1).min(this.triangles[i].vertex2);
            maxVector.max(this.triangles[i].vertex0).max(this.triangles[i].vertex1).max(this.triangles[i].vertex2);
        }
        setMaterial(material);
        boundBox = new BoundBox(
                minVector.sub(Vector3.one().scale(BOUND_SCALE)),
                maxVector.sum(Vector3.one().scale(BOUND_SCALE))
        );
    }

    @Override
    public Model copy() {
        return new Mesh(triangles, material);
    }

    @Override
    public Model setMaterial(Material material) {
        super.setMaterial(material);
        for (Triangle triangle : triangles) {
            triangle.setMaterial(material);
        }
        return this;
    }

    @Override
    public Vector3 getCenter() {
        Vector3 center = Vector3.zero();
        for (Triangle triangle : triangles) {
            center.sum(triangle.getCenter());
        }
        return center.scale(1f / triangles.length);
    }

    @Override
    public Vector3[] getSurfacePoints(int count) {
        Vector3[] surfacePoints = new Vector3[count];
        for (int i = 0; i < count; i++) {
            int triangleIndex = TTRand.range(0, triangles.length - 1);
            Vector3[] triangleSurfacePoints = triangles[triangleIndex].surfacePoints;
            surfacePoints[i] = surfacePoints[TTRand.range(0, triangleSurfacePoints.length - 1)];
        }
        return surfacePoints;
    }

    @Override
    public Hit intersect(Ray ray) {
        if (!boundBox.intersect(ray)) {
            return null;
        }
        Hit nearestHit = null;
        for (Triangle triangle : triangles) {
            Hit hit = triangle.intersect(ray);
            if (nearestHit == null || (hit != null && hit.distance < nearestHit.distance)) {
                nearestHit = hit;
            }
        }
        if (nearestHit != null) {
            nearestHit.model = this;
        }
        return nearestHit;
    }
}

package tracer.model;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Material;
import tracer.util.TTSolver;

/**
 * @author Pedro Henrique
 */
public class Torus extends AbstractModel {

    /**
     * The center of the torus.
     */
    protected Vector3 center;

    /**
     * The perpendicular axis of the torus innerRadius plane.
     */
    protected Vector3 axis;

    /**
     * The inner radius of the sphere.
     */
    protected float innerRadius;

    /**
     * The outer radius of the sphere.
     */
    protected float outerRadius;

    public Torus(Material material, Vector3 center, Vector3 axis, float innerRadius, float outerRadius) {
        super(material);
        this.center = center;
        this.axis = axis;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }

    public void setCenter(Vector3 center) {
        this.center = center;
    }

    public Vector3 getAxis() {
        return axis;
    }

    public void setAxis(Vector3 axis) {
        this.axis = axis;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
    }

    @Override
    public Model copy() {
        return null;
    }

    @Override
    public Vector3 getCenter() {
        return null;
    }

    @Override
    public Vector3[] getSurfacePoints(int count) {
        return new Vector3[0];
    }

    @Override
    public Hit intersect(Ray ray) {

        Vector3 centerToRayOrigin = Vector3.sub(ray.origin, center);
        float centerToRayOriginDotDirection = centerToRayOrigin.dot(ray.direction);
        float centerToRayOriginDotDirectionSquared = centerToRayOrigin.sqrMag();
        float innerRadiusSquared = innerRadius * innerRadius;
        float outerRadiusSquared = outerRadius * outerRadius;

        float axisDotCenterToRayOrigin = axis.dot(centerToRayOrigin);
        float axisDotRayDirection = axis.dot(ray.direction);
        float a = 1 - axisDotRayDirection * axisDotRayDirection;
        float b = 2 * (centerToRayOrigin.dot(ray.direction) - axisDotCenterToRayOrigin * axisDotRayDirection);
        float c = centerToRayOriginDotDirectionSquared - axisDotCenterToRayOrigin * axisDotCenterToRayOrigin;
        float d = centerToRayOriginDotDirectionSquared + outerRadiusSquared - innerRadiusSquared;

        // Solve quartic equation with coefficients A, B, C, D and E
        float A = 1;
        float B = 4 * centerToRayOriginDotDirection;
        float C = 2 * d + B * B * 0.25f - 4 * outerRadiusSquared * a;
        float D = B * d - 4 * outerRadiusSquared * b;
        float E = d * d - 4 * outerRadiusSquared * c;

        // Maximum number of roots is 4
        float[] roots = TTSolver.solveQuartic(A, B, C, D, E);

        if (roots == null) {
            return null;
        }

        // Find closest to zero positive solution
        float closestRoot = Float.POSITIVE_INFINITY;
        boolean greaterThanZero = false;
        for (float root : roots) {
            if (root >= 0 && root < closestRoot) {
                greaterThanZero = true;
                closestRoot = root;
            }
        }

        if (greaterThanZero) {
            return new Hit(this, ray, closestRoot, Vector3.orientate(ray.origin, ray.direction, closestRoot),
                    getNormal(ray, closestRoot), material.getSurfaceColor());
        }
        return null;
    }

    private Vector3 getNormal(Ray ray, float distance) {
        Vector3 point = Vector3.orientate(ray.origin, ray.direction, distance);
        Vector3 centerToPoint = Vector3.sub(point, center);
        float centerToPointDotAxis = centerToPoint.dot(axis);
        Vector3 direction = centerToPoint.sub(Vector3.scale(axis, centerToPointDotAxis));
        direction.normalize();
        Vector3 normal = Vector3.sub(point, center).sum(direction.scale(outerRadius)).normalize();
        return normal;
    }
}

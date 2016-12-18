package tracer.model.bounds;

import tracer.data.base.Vector3;
import tracer.data.trace.Ray;

/**
 * The bound boxes helps intersection checking optimising the performance of ray casting a scene, the bound boxes are
 * aligned with the euclidean axis. The objects of this class can be used in the Model implementations to helps the
 * ray intersection checking.
 *
 * @author Pedro Henrique
 */
public class BoundBox {


    // This parameter is used to help intersection checks.
    private static final float EPSILON = 1e-8f;

    /**
     * The minimum distance values of the x, y and z planes.
     */
    private Vector3 minPoint;

    /**
     * The maximum distance values of the x, y and z planes.
     */
    private Vector3 maxPoint;

    /**
     * Creates the bound box with the minimum anx maximum plane distances, the vectors internal copy are fixed if the
     * received points are contains mim and max values swapped.
     *
     * @param minPoint the mim distance values of the bound box
     * @param maxPoint the max distance values of the bound box
     */
    public BoundBox(Vector3 minPoint, Vector3 maxPoint) {
        this.minPoint = minPoint.copy();
        this.maxPoint = maxPoint.copy();
        if (this.minPoint.x > this.maxPoint.x) {
            float aux = this.minPoint.x;
            this.minPoint.x = this.maxPoint.x;
            this.maxPoint.x = aux;
        }
        if (this.minPoint.y > this.maxPoint.y) {
            float auy = this.minPoint.y;
            this.minPoint.y = this.maxPoint.y;
            this.maxPoint.y = auy;
        }
        if (this.minPoint.z > this.maxPoint.z) {
            float aux = this.minPoint.z;
            this.minPoint.z = this.maxPoint.z;
            this.maxPoint.z = aux;
        }
    }

    /**
     * Returns true if the received ray intersects with this bound box, does not return any information about the hit.
     *
     * @param ray the ray to check intersection
     * @return if the ray this bound box
     */
    public boolean intersect(Ray ray) {
        float txMin, txMax, tyMin, tyMax, tzMin, tzMax;
        float a = 1 / ray.direction.x;
        if (a >= 0) {
            txMin = (minPoint.x - ray.origin.x) * a;
            txMax = (maxPoint.x - ray.origin.x) * a;
        } else {
            txMin = (maxPoint.x - ray.origin.x) * a;
            txMax = (minPoint.x - ray.origin.x) * a;
        }
        float b = 1 / ray.direction.y;
        if (b >= 0) {
            tyMin = (minPoint.y - ray.origin.y) * b;
            tyMax = (maxPoint.y - ray.origin.y) * b;
        } else {
            tyMin = (maxPoint.y - ray.origin.y) * b;
            tyMax = (minPoint.y - ray.origin.y) * b;
        }
        float c = 1 / ray.direction.z;
        if (c >= 0) {
            tzMin = (minPoint.z - ray.origin.z) * c;
            tzMax = (maxPoint.z - ray.origin.z) * c;
        } else {
            tzMin = (maxPoint.z - ray.origin.z) * c;
            tzMax = (minPoint.z - ray.origin.z) * c;
        }
        float t0 = txMin > tyMin ? txMin > tzMin ? txMin : tzMin : tyMin > tzMin ? tyMin : tzMin;
        float t1 = txMax < tyMax ? txMax < tyMax ? txMax : tyMax : tyMax < tzMax ? tyMax : tzMax;
        return (t0 < t1 && t1 > EPSILON);
    }
}

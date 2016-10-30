package tracer.renderer;

import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Color;
import tracer.model.Model;
import tracer.model.material.Material;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;

import java.util.function.Consumer;

/**
 * Renderer implementation using the path tracer techniques.
 *
 * @author Pedro Henrique
 */
public class RTRenderer extends AbstractRenderer {

    /**
     * @see AbstractRenderer#AbstractRenderer(Scene, Camera, Display)
     */
    public RTRenderer(Scene scene, Camera camera, Display display) {
        super(scene, camera, display);
    }

    /**
     * @see AbstractRenderer#AbstractRenderer(Scene, Camera, Display, Consumer<Renderer>)
     */
    public RTRenderer(Scene scene, Camera camera, Display display, Consumer<Renderer> frameUpdate) {
        super(scene, camera, display, frameUpdate);
    }

    // Ray tracing internal properties
    private static final int MAX_RAY_DEPTH = 8;
    //
    private static final float ORIGIN_BIAS = 1e-4f;
    //

    @Override
    protected Color renderPixel(Ray ray) {
        return traceRay(ray, 0);
    }

    /**
     * Traces the received ray to check intersections and get the pixel color.
     *
     * @param ray      the ray to trace
     * @param rayDepth the current ray depth
     * @return the pixel color.
     */
    protected Color traceRay(Ray ray, int rayDepth) {

        // Checks the ray depth
        if (rayDepth == MAX_RAY_DEPTH) {
            return scene.getBackgroundColor();
        }

        // Intersection checking
        Hit hit = castRay(ray);

        // If no one intersection happens
        if (hit == null) {
            return scene.getBackgroundColor();
        }

        // If an intersection happens
        Material modelMaterial = hit.model.getMaterial(); // The hit model visual
        float kMax = modelMaterial.getPropagation() + modelMaterial.getReflection() + modelMaterial.getRefraction();

        // Check if is inside the model
        boolean insideModel = false;
        if (ray.direction.dot(hit.normal) > 0) {
            hit.normal.negate();
            insideModel = true;
        }

        // Emission light check
        Color emissionContribution = Color.black();
        if (modelMaterial.isEmissive()) {
            emissionContribution = modelMaterial.getEmissiveColor();
        }

        // Direct light check
        Color directLightContribution = Color.black();
        if (modelMaterial.getPropagation() > 0) {
            for (Model light : scene.getLights()) {
                if (!hit.model.equals(light) && light.getMaterial().isEmissive()) {
                    Vector3 shadowRayDirection = Vector3.sub(light.getCenter(), hit.point).normalize();
                    Vector3 shadowRayOrigin = Vector3.orientate(hit.point, hit.normal, ORIGIN_BIAS);
                    Hit shadowHit = castRay(new Ray(shadowRayOrigin, shadowRayDirection));
                    if (shadowHit != null && shadowHit.model.equals(light)) {
                        float emissionRate = shadowRayDirection.dot(hit.normal);
                        directLightContribution.sum(Color.mul(
                                modelMaterial.getSurfaceColor(),
                                light.getMaterial().getEmissiveColor()
                                ).scale(emissionRate)
                        );
                    }
                }
            }
            directLightContribution.scale(modelMaterial.getPropagation() / kMax);
        }

        // Specular contribution
        Color reflectionContribution = Color.black();
        if (modelMaterial.getReflection() > 0) {
            Vector3 reflectionRayDirection = calculateRayReflection(ray.direction, hit.normal);
            Vector3 reflectionRayOrigin = Vector3.orientate(hit.point, hit.normal, ORIGIN_BIAS);
            reflectionContribution = traceRay(new Ray(reflectionRayOrigin, reflectionRayDirection), rayDepth + 1);
            reflectionContribution.scale(modelMaterial.getReflection() / kMax);
        }

        // Transmission contribution
        Color refractionContribution = Color.black();
        if (modelMaterial.getRefraction() > 0) {
            Vector3 refractionRayDirection
                    = calculateRayRefraction(ray.direction, hit.normal, insideModel ? modelMaterial.getRefractiveIndex() : 1 / modelMaterial.getRefractiveIndex());
            Vector3 refractionRayOrigin = Vector3.orientate(hit.point, hit.normal, -ORIGIN_BIAS);
            refractionContribution = traceRay(new Ray(refractionRayOrigin, refractionRayDirection), rayDepth + 1);
            reflectionContribution.scale(modelMaterial.getRefraction() / kMax);
        }

        return Color.black().sum(emissionContribution).sum(directLightContribution).sum(reflectionContribution)
                .sum(refractionContribution);
    }
}

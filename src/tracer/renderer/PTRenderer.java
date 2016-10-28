package tracer.renderer;

import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.model.material.Material;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.model.Model;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Renderer implementation using the path tracer techniques.
 *
 * @author Pedro Henrique
 */
public class PTRenderer extends AbstractRenderer {

    /**
     * @see AbstractRenderer#AbstractRenderer(Scene, Camera, Display)
     */
    public PTRenderer(Scene scene, Camera camera, Display display) {
        super(scene, camera, display);
    }

    /**
     * @see AbstractRenderer#AbstractRenderer(Scene, Camera, Display, Consumer)
     */
    public PTRenderer(Scene scene, Camera camera, Display display, Consumer<Renderer> frameUpdate) {
        super(scene, camera, display, frameUpdate);
    }

    // Path tracing internal properties
    private static final int PIXEL_SAMPLES = 100;
    private static final int LIGHT_SAMPLES = 4;
    private static final int MAX_RAY_DEPTH = 5;
    //
    private static final float ORIGIN_BIAS = 1e-4f;
    //

    @Override
    protected void renderFrame() {
        int width = display.getDisplayWidth();
        int height = display.getDisplayHeight();
        int[] frontBuffer = display.getFrontBuffer();

        float aspectRatio = width / height;
        float halfFovyTangent = (float) Math.tan(camera.getFovy() / 2);
        Matrix4 cameraToWorldTransform = camera.cameraToWorldSpaceTransform();

        Vector3 rayOrigin = cameraToWorldTransform.transformAsPoint(Vector3.zero());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float rayDirectionX = (2 * ((x + 0.5f) / width) - 1) * aspectRatio * halfFovyTangent;
                float rayDirectionY = (1 - 2 * ((y + 0.5f) / height)) * halfFovyTangent;
                float rayDirectionZ = 1;
                Vector3 rayDirection = cameraToWorldTransform.transformAsDirection(
                        new Vector3(rayDirectionX, rayDirectionY, rayDirectionZ)
                ).normalize();
                frontBuffer[x + y * width] = renderPixel(new Ray(rayOrigin, rayDirection)).getIntValue();
            }
            display.flush(); // flushes each column
        }
    }

    @Override
    protected Color renderPixel(Ray ray) {
        Random prng = new Random();
        Color pixelColor = Color.black();
        for (int i = 0; i < PIXEL_SAMPLES; i++) {
            pixelColor.sum(traceRay(ray, 0, prng).scale(1.0f / PIXEL_SAMPLES));
        }
        return pixelColor;
    }

    /**
     * Traces a ray in the scene, the ray can taker different paths in each bounce and take different energy paths.
     *
     * @param ray      the ray to trace
     * @param rayDepth the current ray depth
     * @param prng     the prng used in indirect light calculation
     * @return the color obtained by the ray tracing
     */
    protected Color traceRay(Ray ray, int rayDepth, Random prng) {

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

        // Check if is inside the model
        boolean insideModel = false;
        if (ray.direction.dot(hit.normal) > 0) {
            hit.normal.negate();
            insideModel = true;
        }

        // Emission light check
        Color emissionContribution = null;
        if (modelMaterial.isEmissive()) {
            emissionContribution = modelMaterial.getEmissiveColor();
        }

        // Direct light check
        Color directLightContribution = Color.black();
        for (Model light : scene.getLights()) {
            if (!hit.model.equals(light) && light.getMaterial().isEmissive()) {
                for (Vector3 lightPoint : light.getSurfacePoints(LIGHT_SAMPLES)) {
                    Vector3 shadowRayDirection = Vector3.sub(lightPoint, hit.point).normalize();
                    Vector3 shadowRayOrigin = Vector3.orientate(hit.point, shadowRayDirection, ORIGIN_BIAS);
                    Hit shadowHit = castRay(new Ray(shadowRayOrigin, shadowRayDirection));
                    if (shadowHit != null && shadowHit.model.equals(light)) {
                        float emissionRate = shadowRayDirection.dot(hit.normal) / LIGHT_SAMPLES;
                        directLightContribution.sum(
                                Color.mul(
                                        modelMaterial.getSurfaceColor(),
                                        light.getMaterial().getEmissiveColor()
                                ).scale(emissionRate)
                        );
                    }
                }
            }
        }
        directLightContribution.scale(modelMaterial.getPropagation() / (modelMaterial.getPropagation() + modelMaterial.getRefraction()));

        // Indirect light check
        Color propagationContribution = Color.black();
        Color reflectionContribution = Color.black();
        Color refractionContribution = Color.black();
        float kMax = modelMaterial.getPropagation() + modelMaterial.getReflection() + modelMaterial.getRefraction();
        float randomK = prng.nextFloat() * kMax;

        if (randomK < modelMaterial.getPropagation()) {
            // Diffuse ray
            Vector3 propagationRayDirection = calculateRandomDirectionInOrientedHemisphere(hit.normal, prng);
            Vector3 propagationRayOrigin = Vector3.orientate(hit.point, propagationRayDirection, ORIGIN_BIAS);
            propagationContribution
                    = traceRay(new Ray(propagationRayOrigin, propagationRayDirection), rayDepth + 1, prng)
                    .mul(modelMaterial.getSurfaceColor());
            //
        } else if (randomK < modelMaterial.getPropagation() + modelMaterial.getReflection()) {
            // Specular ray
            Vector3 reflectionRayDirection = calculateRayReflection(ray.direction, hit.normal);
            Vector3 reflectionRayOrigin = Vector3.orientate(hit.point, reflectionRayDirection, ORIGIN_BIAS);
            reflectionContribution = traceRay(new Ray(reflectionRayOrigin, reflectionRayDirection), rayDepth + 1, prng);
            //
        } else {
            // Refracted ray
            Vector3 refractionRayDirection = calculateRayRefraction(ray.direction, hit.normal,
                    insideModel ? 1 / modelMaterial.getRefractiveIndex() : modelMaterial.getRefractiveIndex()
            );
            Vector3 refractionRayOrigin = Vector3.orientate(hit.point, refractionRayDirection, ORIGIN_BIAS);
            refractionContribution = traceRay(new Ray(refractionRayOrigin, refractionRayDirection), rayDepth + 1, prng)
                    .scale(modelMaterial.getRefraction() / (modelMaterial.getPropagation() + modelMaterial.getRefraction()));
            //
        }
        return directLightContribution.sum(refractionContribution).sum(reflectionContribution);
    }
}

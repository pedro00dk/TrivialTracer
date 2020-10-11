package tracer.renderer;

import java.util.function.Consumer;
import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Color;
import tracer.data.visual.Material;
import tracer.model.Model;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.util.TTRand;

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

    // Path tracing internal properties
    private static final int PIXEL_SAMPLES = 40;
    private static final int LIGHT_SAMPLES = 4;
    private static final int MAX_RAY_DEPTH = 4;
    //
    private static final float ORIGIN_BIAS = 1e-4f;
    //

    @Override
    protected Color renderPixel(Ray ray) {
        Color pixelColor = Color.black();
        for (int i = 0; i < PIXEL_SAMPLES; i++) {
            Color rayColor = traceRay(ray, 0).scale(1f / PIXEL_SAMPLES);
            pixelColor.r += rayColor.r;
            pixelColor.g += rayColor.g;
            pixelColor.b += rayColor.b;
        }
        float greaterComponent = pixelColor.r;
        if (greaterComponent < pixelColor.g) {
            greaterComponent = pixelColor.g;
        }
        if (greaterComponent < pixelColor.b) {
            greaterComponent = pixelColor.b;
        }
        if (greaterComponent > 1) {
            pixelColor.scale(1 / greaterComponent);
        }
        return pixelColor;
    }

    /**
     * Traces a ray in the scene, the ray can taker different paths in each bounce and take different energy paths.
     *
     * @param ray      the ray to trace
     * @param rayDepth the current ray depth
     * @return the color obtained by the ray tracing
     */
    protected Color traceRay(Ray ray, int rayDepth) {

        Color rayColor = Color.black();
        float raySignificance = 1;

        // Checks the ray depth
        while (rayDepth <= MAX_RAY_DEPTH) {

            // Checks the last level
            if (rayDepth == MAX_RAY_DEPTH) {
                rayColor.sum(Color.scale(scene.getBackgroundColor(), raySignificance));
                break;
            }

            // Intersection checking
            Hit hit = castRay(ray);

            // If no one intersection happens
            if (hit == null) {
                rayColor.sum(Color.scale(scene.getBackgroundColor(), raySignificance));
                break;
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

            // Emission light check (always happens if is emissive)
            if (modelMaterial.isEmissive()) {
                rayColor.sum(Color.scale(modelMaterial.getEmissiveColor(), raySignificance));
            }

            // Direct light check (always happens if has lights in the scene)
            if (modelMaterial.getPropagation() > 0) {
                Color directLightContribution = Color.black();
                for (Model light : scene.getLights()) {
                    if (!hit.model.equals(light) && light.getMaterial().isEmissive()) {
                        for (Vector3 lightPoint : light.getSurfacePoints(LIGHT_SAMPLES)) {
                            Vector3 shadowRayDirection = Vector3.sub(lightPoint, hit.point).normalize();
                            Vector3 shadowRayOrigin = Vector3.orientate(hit.point, hit.normal, ORIGIN_BIAS);
                            Hit shadowHit = castRay(new Ray(shadowRayOrigin, shadowRayDirection));
                            if (shadowHit != null && shadowHit.model.equals(light)) {
                                float emissionRate = shadowRayDirection.dot(hit.normal);
                                directLightContribution.sum(Color.mul(
                                        modelMaterial.getSurfaceColor(),
                                        light.getMaterial().getEmissiveColor()
                                        ).scale(emissionRate / LIGHT_SAMPLES)
                                );
                            }
                        }
                    }
                }
                directLightContribution.scale(modelMaterial.getPropagation() / kMax);
                rayColor.sum(Color.scale(directLightContribution, raySignificance));
            }

            // Random ray path
            float rayType = TTRand.floatValue() * kMax;

            if (modelMaterial.getPropagation() > 0 && rayType < modelMaterial.getPropagation()) {
                // Propagation contribution
                Vector3 propagationRayDirection = calculateRayPropagation(hit.normal);
                Vector3 propagationRayOrigin = Vector3.orientate(hit.point, hit.normal, ORIGIN_BIAS);
                ray = new Ray(propagationRayOrigin, propagationRayDirection);
                raySignificance *= (modelMaterial.getPropagation() / kMax) * (propagationRayDirection.dot(hit.normal));
            } else if (modelMaterial.getReflection() > 0
                    && rayType < modelMaterial.getPropagation() + modelMaterial.getReflection()) {
                // Specular contribution
                Vector3 reflectionRayDirection = calculateRayReflection(ray.direction, hit.normal);
                Vector3 reflectionRayOrigin = Vector3.orientate(hit.point, hit.normal, ORIGIN_BIAS);
                ray = new Ray(reflectionRayOrigin, reflectionRayDirection);
                raySignificance *= modelMaterial.getReflection() / kMax;
            } else if (modelMaterial.getRefraction() > 0) {
                // Transmission contribution
                Vector3 refractionRayDirection
                        = calculateRayRefraction(ray.direction, hit.normal, insideModel ? modelMaterial.getRefractiveIndex() : 1 / modelMaterial.getRefractiveIndex());
                Vector3 refractionRayOrigin = Vector3.orientate(hit.point, hit.normal, -ORIGIN_BIAS);
                ray = new Ray(refractionRayOrigin, refractionRayDirection);
                raySignificance = modelMaterial.getRefraction() / kMax;
            } else {
                // Pure black opaque material
                break;
            }

            // Increment rayDepth
            rayDepth++;
        }
        return rayColor;
    }
}

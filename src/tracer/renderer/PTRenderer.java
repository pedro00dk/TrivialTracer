package tracer.renderer;

import tracer.data.Matrix4;
import tracer.data.Vector3;
import tracer.data.material.Color;
import tracer.data.material.Material;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.model.Model;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Renderer implementation using the path tracer techniques.
 *
 * @author Pedro Henrique
 */
public class PTRenderer implements Renderer {

    /**
     * The scene to be rendered.
     */
    protected Scene scene;

    /**
     * The camera to be used to cast rays.
     */
    protected Camera camera;

    /**
     * The display when the generated image will be showed.
     */
    protected Display display;

    /**
     * Function that runs before each frame rendering.
     */
    private Consumer<Renderer> frameUpdate;

    /**
     * Creates the renderer with the received scene, camera and display.
     *
     * @param scene   the scene to be rendered.
     * @param camera  the camera.
     * @param display the display to show de image.
     */
    public PTRenderer(Scene scene, Camera camera, Display display) {
        this.scene = Objects.requireNonNull(scene);
        this.camera = Objects.requireNonNull(camera);
        this.display = Objects.requireNonNull(display);
    }

    /**
     * Creates the renderer with the received scene, camera, display and frame update consumer.
     *
     * @param scene       the scene to be rendered.
     * @param camera      the camera.
     * @param display     the display to show de image.
     * @param frameUpdate consumer that runs before each frame.
     */
    public PTRenderer(Scene scene, Camera camera, Display display, Consumer<Renderer> frameUpdate) {
        this.scene = Objects.requireNonNull(scene);
        this.camera = Objects.requireNonNull(camera);
        this.display = Objects.requireNonNull(display);
        this.frameUpdate = frameUpdate;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = Objects.requireNonNull(scene);
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = Objects.requireNonNull(camera);
    }

    @Override
    public void setDisplay(Display display) {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void setFrameUpdate(Consumer<Renderer> frameUpdate) {
        this.frameUpdate = frameUpdate;
    }

    // Rendering thread methods

    /**
     * Sets the state of the rendering thread, but when it is set true or false, the rendering thread not will be
     * running according the value in this field, its is used as a flag to start or stop the rendering thread.
     */
    private AtomicBoolean rendererRunning = new AtomicBoolean(false);

    @Override
    public void start() {
        if (rendererRunning.compareAndSet(false, true)) {
            new Thread(this::renderingLoop).start();
        } else {
            throw new IllegalStateException("The renderer is already running.");
        }

    }

    @Override
    public void stop() {
        if (!rendererRunning.compareAndSet(true, false)) {
            throw new IllegalStateException("The renderer is already stopped.");
        }
    }

    /**
     * The amount of frames rendered from the last start call.
     */
    private long frameCount = 0;

    /**
     * The time of the last frame.
     */
    private float frameTime = 0;

    /**
     * The current frame rate per second (uses only one frame time to calculate).
     */
    private float frameRate = 0;

    @Override
    public long getFrameCount() {
        return frameCount;
    }

    @Override
    public float getFrameTime() {
        return frameTime;
    }

    @Override
    public float getFrameRate() {
        return frameRate;
    }

    /**
     * This method runs while the {@link #stop()} method is not called, it renders the frames in sequence showing the
     * images in the display. The frame update consumer (if not null) is called before render each frame.
     */
    private void renderingLoop() {
        frameCount = 0;
        frameTime = 0;
        frameRate = 0;
        while (rendererRunning.get()) {
            long startFrameTime = System.currentTimeMillis();
            //
            if (frameUpdate != null) {
                frameUpdate.accept(this);
            }
            renderFrame();
            System.out.println(getFrameRate());
            //
            long finishFrameTime = System.currentTimeMillis();
            float deltaFrameTime = (finishFrameTime - startFrameTime) / 1000.0f;
            frameCount += 1;
            frameTime = deltaFrameTime;
            frameRate = 1 / deltaFrameTime;
        }
        frameCount = 0;
        frameTime = 0;
        frameRate = 0;
    }

    /**
     * This method renders a frame, works calculating rays directions and checking intersections.
     */
    protected void renderFrame() {
        int width = display.getDisplayWidth();
        int height = display.getDisplayHeight();
        int[] frontBuffer = display.getFrontBuffer();

        float aspectRatio = width / height;
        float halfFovyTangent = (float) Math.tan(camera.getFovy() / 2);
        Matrix4 cameraToWorldTransform = camera.cameraToWorldSpaceTransform();

        Vector3 rayOrigin = cameraToWorldTransform.transformAsPoint(Vector3.zero());

        for (int x = 0; x < width; x++) {
            System.out.println("Rendering column " + x + " of " + width);
            for (int y = 0; y < height; y++) {
                float rayDirectionX = (2 * ((x + 0.5f) / width) - 1) * aspectRatio * halfFovyTangent;
                float rayDirectionY = (1 - 2 * ((y + 0.5f) / height)) * halfFovyTangent;
                float rayDirectionZ = 1;
                Vector3 rayDirection = cameraToWorldTransform.transformAsDirection(
                        new Vector3(rayDirectionX, rayDirectionY, rayDirectionZ)
                ).normalize();
                frontBuffer[x + y * width] = renderPixel(new Ray(rayOrigin, rayDirection)).getIntValue();
            }
            display.flush();
        }
        //display.flush();
    }

    //
    private static final int PIXEL_SAMPLES = 100;
    private static final int LIGHT_SAMPLES = 4;
    private static final int MAX_RAY_DEPTH = 5;
    //
    private static final float ORIGIN_BIAS = 1e-4f;
    //

    /**
     * This method renders a pixel.
     *
     * @param ray the pixel ray
     * @return the color of the pixel
     */
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
        Material modelMaterial = hit.model.getMaterial(); // The hit model material

        // Check if is inside the model
        boolean insideModel = false;
        if (ray.direction.dot(hit.normal) > 0) {
            hit.normal.negate();
            insideModel = true;
        }

        // Emission light check
        Color emissionContribution = null;
        if (modelMaterial.isFullyEmissive()) {
            return modelMaterial.getEmissiveColor().copy();
        } else if (modelMaterial.isEmissive()) {
            emissionContribution = modelMaterial.getEmissiveColor();
        }

        // Direct light check
        Color directLightContribution = Color.black();
        for (Model light : scene.getLights()) {
            if (!hit.model.equals(light)
                    && (light.getMaterial().isEmissive() || light.getMaterial().isFullyEmissive())) {
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

    // Helper methods

    /**
     * Casts the received ray in the scene checking for intersections and returns the nearest intersection, or null if
     * the ray touches nothing
     *
     * @param ray the ray to cast in the scene
     * @return the nearest hit or null
     */
    private Hit castRay(Ray ray) {
        Hit nearestHit = null;
        for (Model model : scene.getModels()) {
            Hit hit = model.intersect(ray);
            if (nearestHit == null || (hit != null && hit.distance < nearestHit.distance)) {
                nearestHit = hit;
            }
        }
        return nearestHit;
    }

    /**
     * Returns a random point the the hemisphare oriented by the received vector.
     *
     * @param orientation the hemisphere orientation
     * @param prng        the prng used to generate the point
     * @return the point in the hemisphere
     */
    private Vector3 calculateRandomDirectionInOrientedHemisphere(Vector3 orientation, Random prng) {
        float longitude = 2 * (float) Math.PI * prng.nextFloat();
        float latitude = (float) Math.acos(2 * prng.nextFloat() - 1) / 2;
        return Matrix4.rotationBetween(Vector3.forward(), orientation).transformAsDirection(new Vector3(
                (float) (Math.sin(latitude) * Math.cos(longitude)),
                (float) (Math.sin(latitude) * Math.sin(longitude)),
                (float) (Math.cos(latitude))));
    }

    /**
     * Returns the reflected direction over the received normal.
     *
     * @param direction the direction
     * @param normal    the normal
     * @return the reflected direction
     */
    private Vector3 calculateRayReflection(Vector3 direction, Vector3 normal) {
        return Vector3.orientate(direction, normal, -2 * direction.dot(normal));
    }

    /**
     * Returns the refracted direction over the received normal with the received refractive index.
     *
     * @param direction       the direction
     * @param normal          the normal
     * @param refractiveIndex the refractive index
     * @return the refracted direction
     */
    private Vector3 calculateRayRefraction(Vector3 direction, Vector3 normal, float refractiveIndex) {
        float externalCosine = direction.dot(Vector3.negate(normal));
        float internalSquaredSine = refractiveIndex * refractiveIndex * (1 - externalCosine * externalCosine);
        float internalCosine = 1 - internalSquaredSine;
        return Vector3.sum(Vector3.scale(direction, refractiveIndex),
                Vector3.scale(normal, refractiveIndex * externalCosine - (float) Math.sqrt(internalCosine)))
                .normalize();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Corresponds to the pixel in the screen.
     * This is a conceptual method, and is not used.
     * Returns the result (itself) in a vector.
     * <p>
     * Raster space borders:
     * <p>
     * (0, 0) - - - -(width, 0)
     * <p>
     * (0, height) - - - - (width, height)
     *
     * @param rasterX the x value in the pixel screen.
     * @param rasterY the y value in the pixel screen.
     * @return the received pixel in a vector.
     */
    private static Vector3 pixelInRasterSpace(float rasterX, float rasterY) {
        return new Vector3(rasterX, rasterY, 0);
    }

    /**
     * Transform the pixel from the raster space to the NDC space.
     * This is a conceptual method, and is not used.
     * Returns the result in a vector.
     * <p>
     * NDC space borders:
     * <p>
     * (0, 0) - - - -(1, 0)
     * <p>
     * (0, 1) - - - - (1, 1)
     * <p>
     * Conversion algorithm:
     * <p>
     * PixelNDCx = (PixelRASTERx + 0.5) / ImageWidth
     * <p>
     * PixelNDCy = (PixelRASTERy + 0.5) / ImageHeight
     *
     * @param rasterX          the x value in the raster space (same that pixel screen).
     * @param rasterY          the y value in the raster space (same that pixel screen).
     * @param pixelCountWidth  the amount of pixels in a raster line.
     * @param pixelCountHeight the amount of pixels in a rater column.
     * @return the pixel transformed to the NDC space in a vector.
     * @see #pixelInRasterSpace(float, float)
     */
    private static Vector3 pixelFromRasterSpaceToNDCSpace(float rasterX, float rasterY,
                                                          int pixelCountWidth, int pixelCountHeight) {
        return new Vector3((rasterX + 0.5f) / pixelCountWidth, (rasterY + 0.5f) / pixelCountHeight, 0);
    }

    /**
     * Transform the pixel from the NDC space to the screen space.
     * This is a conceptual method, and is not used.
     * Returns the result in a vector.
     * <p>
     * Screen space borders:
     * <p>
     * (-1, 1) - - - -(1, 1)
     * <p> - - - - (0, 0) - - - - <p>
     * (-1, -1) - - - - (1, -1)
     * <p>
     * Conversion algorithm:
     * <p>
     * PixelSCREENx = 2 * PixelNDC - 1
     * <p>
     * PixelSCREENy = 1 - 2 * PixelNDC (fix negative y orientation in NDC space)
     *
     * @param ndcX the x value in the NDC space.
     * @param ndcY the y value in the NDC space.
     * @return the pixel transformed to the Screen space in a vector.
     * @see #pixelFromRasterSpaceToNDCSpace(float, float, int, int)
     */
    private static Vector3 pixelFromNDCSpaceToScreenSpace(float ndcX, float ndcY) {
        return new Vector3((2 * ndcX - 1), 1 - 2 * ndcY, 0);
    }

    /**
     * This method fixes the screen pixel coordinates to non quadratic screens and non quadratic pixels.
     * It changes the screen space dimension (overlaps on x or y axis depending of the aspect ratio).
     * This is a conceptual method, and is not used.
     * <p>
     * The space in the same of the screen space, but with the aspect ratio fixed.
     * <p>
     * screenAspectRatio = rasterImageWidth / rasterImageHeight
     * <p>
     * pixelAspectRatio = pixelWidth / pixelHeight
     * <p>
     * RatioFixedPixelSCREENx = PixelSCREENx * screenAspectRatio * pixelAspectRatio
     * <p>
     * RatioFixedPixelSCREENy = PixelSCRENNy (not modified)
     *
     * @param screenX          the x value in the screen space.
     * @param screenY          the y value in the screen space.
     * @param pixelCountWidth  the amount of pixels in a raster line.
     * @param pixelCountHeight the amount of pixels in a rater column.
     * @param pixelWidth       the pixel width.
     * @param pixelHeight      the pixel height.
     * @return the pixel in screen space with fixed ratio in a vector
     * @see #pixelFromNDCSpaceToScreenSpace(float, float)
     */
    private static Vector3 pixelFromScreenSpaceToScreenFixedRatioScreenSpace(float screenX, float screenY,
                                                                             int pixelCountWidth, int pixelCountHeight,
                                                                             float pixelWidth, float pixelHeight) {
        float screenAspectRatio = (float) pixelCountWidth / (float) pixelCountHeight;
        float pixelAspectRatio = pixelWidth / pixelHeight;
        return new Vector3(screenX * screenAspectRatio * pixelAspectRatio, screenY, 0);
    }

    /**
     * This method fixes the screen aperture using the received vertical field of view, the vertical field of view is
     * used because is more appropriate to the screen.
     * <p>
     * AngleRatioFixedPixelSCREENx = RatioFixedPixelSCREENx * tan(fovy / 2)
     * <p>
     * AngleRatioFixedPixelSCREENy = RatioFixedPixelSCREENy * tan(fovy / 2)
     *
     * @param fixedRatioScreenX the x value in the screen space with fixed ratio.
     * @param fixedRatioScreenY the y value in the screen space with fixed ratio.
     * @param fovy              the vertical field of view in radians.
     * @return the screen point with fixed ratio and angle.
     * @see #pixelFromScreenSpaceToScreenFixedRatioScreenSpace(float, float, int, int, float, float)
     */
    private static Vector3 pixelFromFixedRatioScreenSpaceToFixedRatioAndAngleScreenSpace(float fixedRatioScreenX,
                                                                                         float fixedRatioScreenY,
                                                                                         float fovy) {
        float halfFovyTan = (float) Math.tan(fovy / 2);
        return new Vector3(fixedRatioScreenX * halfFovyTan, fixedRatioScreenY * halfFovyTan, 0);
    }

    /**
     * Compute the camera primary ray direction with the received parameters, the returned vector is the ray direction
     * in local camera coordinates. This method uses the concepts of the methods in the @see tags.
     *
     * @param x                the x value of the pixel in the raster.
     * @param y                the y value of the pixel in the raster.
     * @param pixelCountWidth  the amount of pixels in a raster line.
     * @param pixelCountHeight the amount of pixels in a rater column.
     * @param pixelWidth       the pixel width.
     * @param pixelHeight      the pixel height.
     * @param fovy             the vertical field of view in radians.
     * @param positiveZ        if the camera look direction in orientated to the positive camera z direction
     * @return the ray direction of the camera in camera local space.
     * @see #pixelInRasterSpace(float, float)
     * @see #pixelFromRasterSpaceToNDCSpace(float, float, int, int)
     * @see #pixelFromNDCSpaceToScreenSpace(float, float)
     * @see #pixelFromScreenSpaceToScreenFixedRatioScreenSpace(float, float, int, int, float, float)
     * @see #pixelFromFixedRatioScreenSpaceToFixedRatioAndAngleScreenSpace(float, float, float)
     */
    private static Vector3 computeCameraRayDirection(float x, float y, int pixelCountWidth, int pixelCountHeight,
                                                     float pixelWidth, float pixelHeight, float fovy, boolean positiveZ) {
        float screenAspectRatio = (float) pixelCountWidth / (float) pixelCountHeight;
        float pixelAspectRatio = pixelWidth / pixelHeight;
        float halfFovyTan = (float) Math.tan(fovy / 2);
        return new Vector3(
                (2 * ((x + 0.5f) / pixelCountWidth) - 1) * screenAspectRatio * pixelAspectRatio * halfFovyTan,
                (1 - 2 * ((y + 0.5f) / pixelCountHeight)) * halfFovyTan,
                positiveZ ? 1 : -1
        );
    }

    /**
     * Compute the camera primary ray direction with the received parameters, the returned vector is the ray direction
     * in local camera coordinates. This method uses the concepts of the methods in the @see tags. This method assumes
     * square pixels.
     *
     * @param x                the x value of the pixel in the raster.
     * @param y                the y value of the pixel in the raster.
     * @param pixelCountWidth  the amount of pixels in a raster line.
     * @param pixelCountHeight the amount of pixels in a rater column.
     * @param fovy             the vertical field of view in radians.
     * @param positiveZ        if the camera look direction in orientated to the positive camera z direction
     * @return the ray direction of the camera in camera local space.
     * @see #pixelInRasterSpace(float, float)
     * @see #pixelFromRasterSpaceToNDCSpace(float, float, int, int)
     * @see #pixelFromNDCSpaceToScreenSpace(float, float)
     * @see #pixelFromScreenSpaceToScreenFixedRatioScreenSpace(float, float, int, int, float, float)
     * @see #pixelFromFixedRatioScreenSpaceToFixedRatioAndAngleScreenSpace(float, float, float)
     */
    private static Vector3 computeCameraRayDirection(float x, float y, int pixelCountWidth, int pixelCountHeight,
                                                     float fovy, boolean positiveZ) {
        float screenAspectRatio = (float) pixelCountWidth / (float) pixelCountHeight;
        float halfFovyTan = (float) Math.tan(fovy / 2);
        return new Vector3(
                (2 * ((x + 0.5f) / pixelCountWidth) - 1) * screenAspectRatio * halfFovyTan,
                (1 - 2 * ((y + 0.5f) / pixelCountHeight)) * halfFovyTan,
                positiveZ ? 1 : -1
        );
    }

    /**
     * Compute the camera primary ray direction with the received parameters, the returned vector is the ray direction
     * in local camera coordinates. This method uses the concepts of the methods in the @see tags. This method assumes
     * square pixels and vision direction orientated to the camera positive z direction.
     *
     * @param x                the x value of the pixel in the raster.
     * @param y                the y value of the pixel in the raster.
     * @param pixelCountWidth  the amount of pixels in a raster line.
     * @param pixelCountHeight the amount of pixels in a rater column.
     * @param fovy             the vertical field of view in radians.
     * @return the ray direction of the camera in camera local space.
     * @see #pixelInRasterSpace(float, float)
     * @see #pixelFromRasterSpaceToNDCSpace(float, float, int, int)
     * @see #pixelFromNDCSpaceToScreenSpace(float, float)
     * @see #pixelFromScreenSpaceToScreenFixedRatioScreenSpace(float, float, int, int, float, float)
     * @see #pixelFromFixedRatioScreenSpaceToFixedRatioAndAngleScreenSpace(float, float, float)
     */
    private static Vector3 computeCameraRayDirection(float x, float y, int pixelCountWidth, int pixelCountHeight,
                                                     float fovy) {
        float screenAspectRatio = (float) pixelCountWidth / (float) pixelCountHeight;
        float halfFovyTan = (float) Math.tan(fovy / 2);
        return new Vector3(
                (2 * ((x + 0.5f) / pixelCountWidth) - 1) * screenAspectRatio * halfFovyTan,
                (1 - 2 * ((y + 0.5f) / pixelCountHeight)) * halfFovyTan,
                1
        );
    }
}

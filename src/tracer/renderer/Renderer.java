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
import tracer.util.TTMath;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Renderer implementation using the path tracer techniques.
 *
 * @author Pedro Henrique
 */
public class Renderer {

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
    public Renderer(Scene scene, Camera camera, Display display) {
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
    public Renderer(Scene scene, Camera camera, Display display, Consumer<Renderer> frameUpdate) {
        this.scene = Objects.requireNonNull(scene);
        this.camera = Objects.requireNonNull(camera);
        this.display = Objects.requireNonNull(display);
        this.frameUpdate = frameUpdate;
    }

    /**
     * Gets the scene of this renderer.
     *
     * @return the scene of this renderer.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Gets the camera of this renderer.
     *
     * @return the camera of this renderer.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Gets the display of this renderer.
     *
     * @return the display of this renderer.
     */
    public Display getDisplay() {
        return display;
    }

    /**
     * Sets the received scene in this renderer.
     *
     * @param scene the new scene of this renderer.
     */
    public void setScene(Scene scene) {
        this.scene = Objects.requireNonNull(scene);
    }

    /**
     * Sets the received camera in this renderer.
     *
     * @param camera the new camera of this renderer.
     */
    public void setCamera(Camera camera) {
        this.camera = Objects.requireNonNull(camera);
    }

    /**
     * Sets the received display in this renderer.
     *
     * @param display the new display of this renderer.
     */
    public void setDisplay(Display display) {
        this.display = Objects.requireNonNull(display);
    }

    /**
     * Sets the received frame update consumer in the renderer.
     *
     * @param frameUpdate the received frame update consumer in the renderer.
     */
    public void setFrameUpdate(Consumer<Renderer> frameUpdate) {
        this.frameUpdate = frameUpdate;
    }

    // Rendering thread methods

    /**
     * Sets the state of the rendering thread, but when it is set true or false, the rendering thread not will be
     * running according the value in this field, its is used as a flag to start or stop the rendering thread.
     */
    private AtomicBoolean rendererRunning = new AtomicBoolean(false);

    /**
     * Starts the thread that renders the frames. To stop, call the stop() method. If is already running throws an
     * exception.
     *
     * @see #stop()
     */
    public void start() {
        if (rendererRunning.compareAndSet(false, true)) {
            new Thread(this::renderingLoop).start();
        } else {
            throw new IllegalStateException();
        }

    }

    /**
     * Stops the thread that renders the frames. If is already stopped throws an exception.
     *
     * @see #start()
     */
    public void stop() {
        if (!rendererRunning.compareAndSet(true, false)) {
            throw new IllegalStateException();
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

    /**
     * Gets amount of frames rendered from the last start call.
     *
     * @return the amount of frames rendered.
     */
    public long getFrameCount() {
        return frameCount;
    }

    /**
     * Gets the time of the last frame in seconds.
     *
     * @return the time of the last frame in seconds.
     */
    public float getFrameTime() {
        return frameTime;
    }

    /**
     * Gets the current frame rate per second.
     *
     * @return the current frame rate.
     */
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
            for (int y = 0; y < height; y++) {
                float rayDirectionX = (2 * ((x + 0.5f) / width) - 1) * aspectRatio * halfFovyTangent;
                float rayDirectionY = (1 - 2 * ((y + 0.5f) / height)) * halfFovyTangent;
                float rayDirectionZ = 1;
                Vector3 rayDirection = cameraToWorldTransform.transformAsDirection(
                        new Vector3(rayDirectionX, rayDirectionY, rayDirectionZ)
                ).normalize();
                frontBuffer[x + y * width] = renderPixel(new Ray(rayOrigin, rayDirection)).getIntValue();
            }
        }
        display.flush();
    }

    //
    private static final int PIXEL_SAMPLES = 10;
    private static final int LIGHT_SAMPLES = 5;
    private static final int MAX_RAY_DEPTH = 6;
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
        //for (int i = 0; i < PIXEL_SAMPLES; i++) {
        //    pixelColor.sum(traceRay(ray, 0, prng).scale(1.0f / PIXEL_SAMPLES));
        //}
        for (int i = 0; i < 1; i++) {
            pixelColor.sum(traceRay(ray, 0, prng).scale(1.0f / 1));
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

        // Intersection checking
        Hit hit = castRay(ray);

        // If no one intersection happens
        if (hit == null) {
            return scene.getBackgroundColor();
        }

        // If an intersection happens
        float bias = 1e-4f;
        boolean inside = false;
        Color resultantSurfaceColor = Color.black();
        Material hitModelMaterial = hit.model.getMaterial();

        // Check if the ray was traced inside of the model
        if (ray.direction.dot(hit.normal) > 0) {
            hit.normal.negate();
            inside = true;
        }
        Vector3 subFrontRay = Vector3.orientate(hit.point, hit.normal, bias); // Sub rays origin
        Vector3 subBackRay = Vector3.orientate(hit.point, hit.normal, -bias); // Sub refraction ray origin

        if (rayDepth < MAX_RAY_DEPTH && (hitModelMaterial.getRefraction() > 0 || hitModelMaterial.getReflection() > 0)) {
            Color refractedColor = Color.black();
            Color reflexiveColor = Color.black();
            float facingRatio = Vector3.negate(ray.direction).dot(hit.normal);
            float fresnelEffect = TTMath.interpolate((float) Math.pow(1 - facingRatio, 3), 1, 0.1f);

            // Refracted color calculation
            if (hitModelMaterial.getRefraction() > 0) {
                float ior = 1.1f;
                float eta = inside ? ior : 1 / ior;
                float cosine = ray.direction.dot(Vector3.negate(hit.normal));
                float k = 1 - eta * eta * (1 - cosine * cosine);
                Vector3 refractionRayDirection = Vector3.sum(
                        Vector3.scale(ray.direction, eta),
                        Vector3.scale(hit.normal, eta * cosine - (float) Math.sqrt(k))
                ).normalize();
                refractedColor = traceRay(new Ray(subBackRay, refractionRayDirection), rayDepth + 1, prng);
            }

            // Reflexive color calculation
            if (hitModelMaterial.getReflection() > 0) {
                Vector3 reflectionRayDirection
                        = Vector3.orientate(ray.direction, hit.normal, -2 * ray.direction.dot(hit.normal))
                        .normalize();
                reflexiveColor = traceRay(new Ray(subFrontRay, reflectionRayDirection), rayDepth + 1, prng);
            }

            resultantSurfaceColor = Color.sum(
                    reflexiveColor.scale(hitModelMaterial.getReflection() * fresnelEffect),
                    refractedColor.scale(hitModelMaterial.getRefraction() * (1 - fresnelEffect))
            ).mul(hitModelMaterial.getSurfaceColor());
        } else {
            if (rayDepth < MAX_RAY_DEPTH) {
                boolean a = true;
            }
            for (Model emissiveModel : scene.getModels()) {
                Color emissiveColor = emissiveModel.getMaterial().getEmissiveColor();
                if (emissiveColor.getR() > 0 || emissiveColor.getG() > 0 || emissiveColor.getB() > 0) {
                    Vector3 shadowRayDirection = Vector3.sub(emissiveModel.getCenter(), hit.point).normalize();
                    Hit shadowHit = castRay(new Ray(subFrontRay, shadowRayDirection));
                    if (shadowHit != null && shadowHit.model.equals(emissiveModel)) {
                        float emissionRate = shadowRayDirection.dot(hit.normal);
                        resultantSurfaceColor.sum(Color.mul(hitModelMaterial.getSurfaceColor(), emissiveColor)
                                .scale(emissionRate < 0 ? 0 : emissionRate));
                    }
                }
            }
        }
        return resultantSurfaceColor.sum(hitModelMaterial.getEmissiveColor());
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

package tracer.renderer;

import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.trace.Hit;
import tracer.data.trace.Ray;
import tracer.data.visual.Color;
import tracer.model.Model;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.util.TTRand;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Abstract Renderer implementation with the necessary objects and methods. Only the method {@link #renderPixel(Ray)}
 * needs to be implemented to the renderer work, but the method {@link #renderFrame()} too can be override.
 *
 * @author Pedro Henrique
 */
public abstract class AbstractRenderer implements Renderer {

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
     * Creates an abstract renderer with default scene, camera and a useless display.
     */
    public AbstractRenderer() {
        scene = new Scene();
        camera = new Camera();
        display = new Display() {
            @Override
            public int getDisplayWidth() {
                return 0;
            }

            @Override
            public int getDisplayHeight() {
                return 0;
            }

            @Override
            public void setDisplayWidth(int width) {
            }

            @Override
            public void setDisplayHeight(int height) {
            }

            @Override
            public void setDisplaySize(int width, int height) {
            }

            @Override
            public int[] getFrontBuffer() {
                return new int[0];
            }

            @Override
            public void flush() {
            }
        };
    }

    /**
     * Creates the renderer with the received scene, camera and display.
     *
     * @param scene   the scene to be rendered.
     * @param camera  the camera.
     * @param display the display to show de image.
     */
    public AbstractRenderer(Scene scene, Camera camera, Display display) {
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
    public AbstractRenderer(Scene scene, Camera camera, Display display, Consumer<Renderer> frameUpdate) {
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

    /**
     * This method renders a pixel.
     *
     * @param ray the pixel ray
     * @return the color of the pixel
     */
    protected abstract Color renderPixel(Ray ray);

    // Helper methods

    /**
     * Casts the received ray in the scene checking for intersections and returns the nearest intersection, or null if
     * the ray touches nothing
     *
     * @param ray the ray to cast in the scene
     * @return the nearest hit or null
     */
    protected Hit castRay(Ray ray) {
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
     * @return the point in the hemisphere
     */
    protected Vector3 calculateRayPropagation(Vector3 orientation) {
        return TTRand.onUniformHemisphere(orientation);
    }

    /**
     * Returns the reflected direction over the received normal.
     *
     * @param direction the direction
     * @param normal    the normal
     * @return the reflected direction
     */
    protected Vector3 calculateRayReflection(Vector3 direction, Vector3 normal) {
        return Vector3.orientate(direction, normal, -2 * direction.dot(normal));
    }

    /**
     * Returns the refracted direction over the received normal with the received refractive index.
     *
     * @param direction       the direction
     * @param normal          the normal
     * @param refractiveIndex the relative refractive index
     * @return the refracted direction
     */
    protected Vector3 calculateRayRefraction(Vector3 direction, Vector3 normal, float refractiveIndex) {
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

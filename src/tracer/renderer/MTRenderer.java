package tracer.renderer;

import java.util.concurrent.atomic.AtomicInteger;
import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.trace.Ray;
import tracer.data.visual.Color;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;

/**
 * @author Pedro Henrique
 */
public class MTRenderer extends AbstractRenderer {

    /**
     * The internal renderer.
     */
    private AbstractRenderer internalRenderer;

    /**
     * The number of threads to render.
     */
    private int numberOfThreads;

    public MTRenderer(AbstractRenderer internalRenderer, int numberOfThreads) {
        this.internalRenderer = internalRenderer;
        this.numberOfThreads = numberOfThreads;
        setFrameUpdate(internalRenderer.getFrameUpdate());
    }

    @Override
    public Scene getScene() {
        return internalRenderer.getScene();
    }

    @Override
    public Camera getCamera() {
        return internalRenderer.getCamera();
    }

    @Override
    public Display getDisplay() {
        return internalRenderer.getDisplay();
    }

    @Override
    public void setScene(Scene scene) {
        internalRenderer.setScene(scene);
    }

    @Override
    public void setCamera(Camera camera) {
        internalRenderer.setCamera(camera);
    }

    @Override
    public void setDisplay(Display display) {
        internalRenderer.setDisplay(display);
    }

    // Multi-threading attributes
    private AtomicInteger currentPixel;
    private final int pixelsPerThreadLoop = 100;

    // Shared attributes
    private int width;
    private int height;
    private int[] frontBuffer;
    private float aspectRatio;
    private float halfFovyTangent;
    private Matrix4 cameraToWorldTransform;
    private Vector3 rayOrigin;

    @Override
    protected void renderFrame() {
        currentPixel = new AtomicInteger();
        width = internalRenderer.display.getDisplayWidth();
        height = internalRenderer.display.getDisplayHeight();
        frontBuffer = internalRenderer.display.getFrontBuffer();
        aspectRatio = width / height;
        halfFovyTangent = (float) Math.tan(internalRenderer.camera.getFovy() / 2);
        cameraToWorldTransform = internalRenderer.camera.cameraToWorldSpaceTransform();
        rayOrigin = cameraToWorldTransform.transformAsPoint(Vector3.zero());

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(this::renderPixels);
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        display.flush();
    }

    /**
     * This methods should be executed in an separated thread, it calculate pixel colors while have pixel colors to
     * calculate. Multiple threads can run the method at the same time.
     */
    private void renderPixels() {
        int currentThreadPixel;
        while ((currentThreadPixel = currentPixel.getAndAdd(pixelsPerThreadLoop)) < width * height) {
            for (int i = 0; i < pixelsPerThreadLoop && currentThreadPixel < width * height; i++, currentThreadPixel++) {
                int x = currentThreadPixel % width;
                int y = currentThreadPixel / width;
                float rayDirectionX = (2 * ((x + 0.5f) / width) - 1) * aspectRatio * halfFovyTangent;
                float rayDirectionY = (1 - 2 * ((y + 0.5f) / height)) * halfFovyTangent;
                float rayDirectionZ = 1;
                Vector3 rayDirection = cameraToWorldTransform.transformAsDirection(
                        new Vector3(rayDirectionX, rayDirectionY, rayDirectionZ)
                ).normalize();
                frontBuffer[x + y * width] = renderPixel(new Ray(rayOrigin, rayDirection)).getIntValue();
            }
            internalRenderer.display.flush();
        }
    }

    @Override
    protected Color renderPixel(Ray ray) {
        return internalRenderer.renderPixel(ray);
    }
}

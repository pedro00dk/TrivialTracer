package tracer.renderer;

import java.util.function.Consumer;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;

/**
 * The Renderer interface, any rendering algorithm should implement this interface.
 *
 * @author Pedro Henrique
 */
public interface Renderer {

    /**
     * Gets the scene of this renderer.
     *
     * @return the scene of this renderer.
     */
    Scene getScene();

    /**
     * Gets the camera of this renderer.
     *
     * @return the camera of this renderer.
     */
    Camera getCamera();

    /**
     * Gets the display of this renderer.
     *
     * @return the display of this renderer.
     */
    Display getDisplay();

    /**
     * Sets the received scene in this renderer.
     *
     * @param scene the new scene of this renderer.
     */
    void setScene(Scene scene);

    /**
     * Sets the received camera in this renderer.
     *
     * @param camera the new camera of this renderer.
     */
    void setCamera(Camera camera);

    /**
     * Sets the received display in this renderer.
     *
     * @param display the new display of this renderer.
     */
    void setDisplay(Display display);

    /**
     * Sets the received frame update consumer in the renderer.
     *
     * @param frameUpdate the received frame update consumer in the renderer.
     */
    void setFrameUpdate(Consumer<Renderer> frameUpdate);
    Consumer<Renderer> getFrameUpdate();

    /**
     * Starts the thread that renders the frames. To stop, call the stop() method. If is already running throws an
     * exception.
     *
     * @throws IllegalStateException if is already rendering
     * @see #stop()
     */
    void start();

    /**
     * Stops the thread that renders the frames. If is already stopped throws an exception.
     *
     * @throws IllegalStateException if is not rendering
     * @see #start()
     */
    void stop();

    /**
     * Gets amount of frames rendered from the last start call.
     *
     * @return the amount of frames rendered.
     */
    long getFrameCount();

    /**
     * Gets the time of the last frame in seconds.
     *
     * @return the time of the last frame in seconds.
     */
    float getFrameTime();

    /**
     * Gets the current frame rate per second.
     *
     * @return the current frame rate.
     */
    float getFrameRate();
}

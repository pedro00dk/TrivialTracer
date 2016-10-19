package tracer.scene;

/**
 * The interface to display screens, defines the basic methods to load frames.
 *
 * @author Pedro Henrique
 */
public interface Display {

    /**
     * Gets the display width.
     *
     * @return the display width
     */
    int getDisplayWidth();

    /**
     * Gets the display height.
     *
     * @return the display height
     */
    int getDisplayHeight();

    /**
     * Sets the received width to the display, this operation recreates the image buffer.
     *
     * @param width the new width of the display
     */
    void setDisplayWidth(int width);

    /**
     * Sets the received height to the display, this operation recreates the image buffer.
     *
     * @param height the new height of the display
     */
    void setDisplayHeight(int height);

    /**
     * Sets the received size to the display, this operation recreates the image buffer.
     *
     * @param width  the new width of the display
     * @param height the new height of the display
     */
    void setDisplaySize(int width, int height);

    /**
     * Gets the current image front buffer. This buffer is updated when the display size is changed. The color encode
     * can variate according the implementation.
     *
     * @return the image font buffer
     */
    int[] getFrontBuffer();

    /**
     * Flushes the current image buffer to the display.
     */
    void flush();
}

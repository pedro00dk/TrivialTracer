package tracer.scene.display;

import tracer.scene.Display;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Simple Display implementation using java swing.
 *
 * @author Pedro Henrique
 */
public class JDisplay extends JLabel implements Display {

    /**
     * The width of the display.
     */
    private int width;

    /**
     * The height of the display.
     */
    private int height;

    /**
     * The front buffer image.
     */
    private BufferedImage frontImageBuffer;

    /**
     * The front buffer int array, contained in the front buffer image.
     */
    private int[] frontIntBuffer;

    /**
     * The default raster width.
     */
    private static final int DEFAULT_WIDTH = 640;

    /**
     * The default raster height.
     */
    private static final int DEFAULT_HEIGHT = 480;

    /**
     * Creates the display with the default width (640) and height (480).
     */
    public JDisplay() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates the display with the received width and height, they can not be less than 1 and greater than 10000.
     *
     * @param width  the width of the display.
     * @param height the height of the display.
     */
    public JDisplay(int width, int height) {
        if (width < 1 || width > 10000) {
            throw new IllegalArgumentException("The width should be between 1 and 10000");
        } else if (height < 1 || height > 10000) {
            throw new IllegalArgumentException("The height should be between 1 and 10000");
        }
        this.width = width;
        this.height = height;
        createFrontBuffer();
    }

    @Override
    public int getDisplayWidth() {
        return width;
    }

    @Override
    public int getDisplayHeight() {
        return height;
    }

    @Override
    public void setDisplayWidth(int width) {
        if (width < 1 || width > 10000) {
            throw new IllegalArgumentException("The width should be between 1 and 10000");
        }
        this.width = width;
        createFrontBuffer();
    }

    @Override
    public void setDisplayHeight(int height) {
        if (height < 1 || height > 10000) {
            throw new IllegalArgumentException("The height should be between 1 and 10000");
        }
        this.height = height;
        createFrontBuffer();
    }


    @Override
    public void setDisplaySize(int width, int height) {
        if (width < 1 || width > 10000) {
            throw new IllegalArgumentException("The width should be between 1 and 10000");
        } else if (height < 1 || height > 10000) {
            throw new IllegalArgumentException("The height should be between 1 and 10000");
        }
        this.width = width;
        this.height = height;
        createFrontBuffer();
    }

    /**
     * Encode type of the buffer per index is ARGB.
     */
    @Override
    public int[] getFrontBuffer() {
        return frontIntBuffer;
    }

    @Override
    public void flush() {
        setIcon(new ImageIcon(frontImageBuffer));
    }

    // Support methods

    /**
     * Creates the buffer with the width and height.
     */
    private void createFrontBuffer() {
        frontImageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        frontIntBuffer = ((DataBufferInt) frontImageBuffer.getRaster().getDataBuffer()).getData();
    }
}

package tracer.data.material;

import tracer.util.Copyable;
import tracer.util.TTMath;

import java.util.Objects;

/**
 * This class represents a Material of a {@link tracer.model.Model}, contains the colors (surface and emissive),
 * propagation, reflection, shininess and refraction coefficients.
 *
 * @author Pedro Henrique
 */
public class Material implements Copyable<Material> {

    /**
     * The color of the material surface.
     */
    private Color surfaceColor;

    /**
     * The color that the material emits.
     */
    private Color emissiveColor;

    /**
     * Indicates if the material is emissive.
     */
    private boolean emissive;

    /**
     * Indicates if the material is fully emissive.
     */
    private boolean fullyEmissive;

    /**
     * The material light propagation, should be between 0 (absorbs all light) and 1 (reflects the self color).
     */
    private float propagation; // Kd -> diffuse coefficient

    /**
     * The material reflection, should be between 0 (completely diffuse) and 1 (specular).
     */
    private float reflection; // Ks -> specular coefficient

    /**
     * The material shininess, should be between 0 (low concentration) and 1000 (high concentration).
     */
    private float shininess; //A -> specular energy concentration

    /**
     * The material refraction, should be between 0 (opaque) and 1 (transparent).
     */
    private float refraction; // Kt -> transparency coefficient

    /**
     * The default surface color of the material.
     */
    private static final Color DEFAULT_SURFACE_COLOR = Color.lightGray();

    /**
     * The default emissive color of the material.
     */
    private static final Color DEFAULT_EMISSIVE_COLOR = Color.black();

    /**
     * The default emissive info of the material.
     */
    private static final boolean DEFAULT_EMISSIVE = false;

    /**
     * The default fully emissive info of the material.
     */
    private static final boolean DEFAULT_FULLY_EMISSIVE = false;

    /**
     * The default propagation of the material.
     */
    private static final float DEFAULT_PROPAGATION = 1;

    /**
     * The default reflection of the material.
     */
    private static final float DEFAULT_REFLECTION = 0.15f;

    /**
     * The default reflection of the material.
     */
    private static final float DEFAULT_SHININESS = 5;

    /**
     * The default refraction of the material.
     */
    private static final float DEFAULT_REFRACTION = 0;

    /**
     * Create the material with the default fields, the surface light gray, emits nothing, perfectly diffuse, reflects
     * 0.15 of the light, low shininess (5) and opaque (refraction equals 0).
     */
    public Material() {
        this(DEFAULT_SURFACE_COLOR, DEFAULT_EMISSIVE_COLOR, DEFAULT_EMISSIVE, DEFAULT_FULLY_EMISSIVE,
                DEFAULT_PROPAGATION, DEFAULT_REFLECTION, DEFAULT_SHININESS, DEFAULT_REFRACTION);
    }

    /**
     * Creates the material with the received surface color, and the default fields (see {@link #Material()}).
     *
     * @param surfaceColor the material surface color
     */
    public Material(Color surfaceColor) {
        this(surfaceColor, DEFAULT_EMISSIVE_COLOR, DEFAULT_EMISSIVE, DEFAULT_FULLY_EMISSIVE, DEFAULT_PROPAGATION,
                DEFAULT_REFLECTION, DEFAULT_SHININESS, DEFAULT_REFRACTION);
    }

    /**
     * Creates the material with the received values, the numeric values are clamped if out of the valid interval.
     *
     * @param surfaceColor  the material surface color
     * @param emissiveColor the color that this material emits
     * @param emissive      if the material is emissive
     * @param fullyEmissive if the material is fully emissive
     * @param propagation   the propagation of the material (should be between than 0 and 1)
     * @param reflection    the reflection of the material (should be between than 0 and 1)
     * @param shininess     the shininess of the material (should be between than 0 and 1000)
     * @param refraction    the refraction of the material (should be between than 0 and 1)
     */
    public Material(Color surfaceColor, Color emissiveColor, boolean emissive, boolean fullyEmissive, float propagation,
                    float reflection, float shininess, float refraction) {
        this.surfaceColor = Objects.requireNonNull(surfaceColor, "The surface color can not be null.").copy();
        this.emissiveColor = Objects.requireNonNull(emissiveColor, "The emissive color ca not be null.").copy();
        this.emissive = emissive;
        this.fullyEmissive = fullyEmissive;
        this.propagation = TTMath.clamp01(propagation);
        this.reflection = TTMath.clamp01(reflection);
        this.shininess = TTMath.clamp(shininess, 0, 1000);
        this.refraction = TTMath.clamp01(refraction);
    }

    @Override
    public Material copy() {
        return new Material(surfaceColor, emissiveColor, emissive, fullyEmissive, propagation, reflection, shininess,
                refraction);
    }

    /**
     * Returns the surface color of this object.
     *
     * @return the surface color
     */
    public Color getSurfaceColor() {
        return surfaceColor.copy();
    }

    /**
     * Returns the emissive color of this object.
     *
     * @return the emissive color
     */
    public Color getEmissiveColor() {
        return emissiveColor.copy();
    }

    /**
     * Returns if this material is emissive.
     *
     * @return if this material is emissive
     */
    public boolean isEmissive() {
        return emissive;
    }

    /**
     * Returns if this material is fully emissive.
     *
     * @return if this material is fully emissive
     */
    public boolean isFullyEmissive() {
        return fullyEmissive;
    }

    /**
     * Returns the propagation of this material.
     *
     * @return the propagation of this material
     */
    public float getPropagation() {
        return propagation;
    }

    /**
     * Returns the reflection of this material.
     *
     * @return the reflection of this material
     */
    public float getReflection() {
        return reflection;
    }

    /**
     * Returns the shininess of this material.
     *
     * @return the shininess of this material
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * Returns the refraction of this material.
     *
     * @return the refraction of this material
     */
    public float getRefraction() {
        return refraction;
    }
}

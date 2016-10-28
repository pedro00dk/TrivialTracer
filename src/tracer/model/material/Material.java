package tracer.model.material;

import tracer.data.visual.Color;
import tracer.util.Copyable;
import tracer.util.TTMath;

import java.util.Objects;

/**
 * This class represents a Material of a {@link tracer.model.Model}, contains the {@link Color}s (surface and emissive),
 * propagation, reflection and refraction coefficients.
 *
 * @author Pedro Henrique
 */
public class Material implements Copyable<Material> {

    /**
     * The color of the visual surface.
     */
    private Color surfaceColor;

    /**
     * The color that the visual emits.
     */
    private Color emissiveColor;

    /**
     * Indicates if the visual is emissive.
     */
    private boolean emissive;

    /**
     * The visual light propagation, should be between 0 (absorbs all light) and 1 (reflects the self color).
     */
    private float propagation; // Kd -> diffuse coefficient

    /**
     * The visual reflection, should be between 0 (completely diffuse) and 1 (specular).
     */
    private float reflection; // Ks -> specular coefficient

    /**
     * The visual refraction, should be between 0 (opaque) and 1 (transparent).
     */
    private float refraction; // Kt -> transparency coefficient

    /**
     * The refractive index of the visual, ignored if the refraction is 0.
     */
    private float refractiveIndex;

    /**
     * The default surface color of the visual.
     */
    private static final Color DEFAULT_SURFACE_COLOR = Color.lightGray();

    /**
     * The default emissive color of the visual.
     */
    private static final Color DEFAULT_EMISSIVE_COLOR = Color.black();

    /**
     * The default emissive info of the visual.
     */
    private static final boolean DEFAULT_EMISSIVE = false;

    /**
     * The default propagation of the visual.
     */
    private static final float DEFAULT_PROPAGATION = 1;

    /**
     * The default reflection of the visual.
     */
    private static final float DEFAULT_REFLECTION = 0.15f;

    /**
     * The default refraction of the visual.
     */
    private static final float DEFAULT_REFRACTION = 0;

    /**
     * The default refractive index of the visual.
     */
    private static final float DEFAULT_REFRACTIVE_INDEX = 1;

    /**
     * Create the visual with the default fields, the surface light gray, emits nothing, perfectly diffuse, reflects
     * 0.15 of the light and is opaque (refraction equals 0).
     */
    public Material() {
        this(DEFAULT_SURFACE_COLOR.copy(), DEFAULT_EMISSIVE_COLOR.copy(), DEFAULT_EMISSIVE, DEFAULT_PROPAGATION,
                DEFAULT_REFLECTION, DEFAULT_REFRACTION, DEFAULT_REFRACTIVE_INDEX);
    }

    /**
     * Creates the visual with the received surface color and not emissive, and the received properties
     *
     * @param surfaceColor the visual surface color
     * @param propagation  the propagation of the visual (should be between than 0 and 1)
     * @param reflection   the reflection of the visual (should be between than 0 and 1)
     * @param refraction   the refraction of the visual (should be between than 0 and 1)
     */
    public Material(Color surfaceColor, float propagation, float reflection, float refraction, float refractiveIndex) {
        this(surfaceColor, Color.black(), false, propagation, reflection, refraction, refractiveIndex);
    }

    /**
     * Creates a emissive visual, with the received emission color and default visual properties.
     *
     * @param emissiveColor the emissive color
     */
    public Material(Color emissiveColor) {
        this(Color.black(), emissiveColor, true, 0, 0, 0, 1);
    }

    /**
     * Creates the visual with the received values, the numeric values are clamped if out of the valid interval.
     *
     * @param surfaceColor    the visual surface color
     * @param emissiveColor   the color that this visual emits
     * @param emissive        if the visual is emissive
     * @param propagation     the propagation of the visual (should be between than 0 and 1)
     * @param reflection      the reflection of the visual (should be between than 0 and 1)
     * @param refraction      the refraction of the visual (should be between than 0 and 1)
     * @param refractiveIndex the refractive index of the visual (should be between 1 and 10)
     */
    public Material(Color surfaceColor, Color emissiveColor, boolean emissive, float propagation, float reflection,
                    float refraction, float refractiveIndex) {
        this.surfaceColor = Objects.requireNonNull(surfaceColor, "The surface color can not be null.");
        this.emissiveColor = Objects.requireNonNull(emissiveColor, "The emissive color ca not be null.");
        this.emissive = emissive;
        this.propagation = TTMath.clamp01(propagation);
        this.reflection = TTMath.clamp01(reflection);
        this.refraction = TTMath.clamp01(refraction);
        this.refractiveIndex = TTMath.clamp(refractiveIndex, 1, 10);
    }

    @Override
    public Material copy() {
        return new Material(surfaceColor.copy(), emissiveColor.copy(), emissive, propagation, reflection, refraction,
                refractiveIndex);
    }

    /**
     * Returns the surface color of this object.
     *
     * @return the surface color
     */
    public Color getSurfaceColor() {
        return surfaceColor;
    }

    /**
     * Returns the emissive color of this object.
     *
     * @return the emissive color
     */
    public Color getEmissiveColor() {
        return emissiveColor;
    }

    /**
     * Returns if this visual is emissive.
     *
     * @return if this visual is emissive
     */
    public boolean isEmissive() {
        return emissive;
    }

    /**
     * Returns the propagation of this visual.
     *
     * @return the propagation of this visual
     */
    public float getPropagation() {
        return propagation;
    }

    /**
     * Returns the reflection of this visual.
     *
     * @return the reflection of this visual
     */
    public float getReflection() {
        return reflection;
    }

    /**
     * Returns the refraction of this visual.
     *
     * @return the refraction of this visual
     */
    public float getRefraction() {
        return refraction;
    }

    /**
     * Returns the refractive index of the visual.
     *
     * @return the refractive index
     */
    public float getRefractiveIndex() {
        return refractiveIndex;
    }

    /**
     * Sets the received surface color in this visual.
     *
     * @param surfaceColor the new surface color
     * @return this visual modified
     */
    public Material setSurfaceColor(Color surfaceColor) {
        this.surfaceColor = Objects.requireNonNull(surfaceColor, "The surface color can not be null.");
        return this;
    }

    /**
     * Sets the received emissive color in this visual.
     *
     * @param emissiveColor the new emissive color
     * @return this visual modified
     */
    public Material setEmissiveColor(Color emissiveColor) {
        this.emissiveColor = Objects.requireNonNull(emissiveColor, "The surface color can not be null.");
        return this;
    }

    /**
     * Sets the received emissive property in this visual.
     *
     * @param emissive if this visual is emissive
     * @return this visual modified
     */
    public Material setEmissive(boolean emissive) {
        this.emissive = emissive;
        return this;
    }

    /**
     * Sets the received propagation property in this visual.
     *
     * @param propagation the new propagation of the visual (should be between 0 and 1)
     * @return this visual modified
     */
    public Material setPropagation(float propagation) {
        this.propagation = TTMath.clamp01(propagation);
        return this;
    }

    /**
     * Sets the received reflection property in this visual.
     *
     * @param reflection the new reflection of the visual (should be between 0 and 1)
     * @return this visual modified
     */
    public Material setReflection(float reflection) {
        this.reflection = TTMath.clamp01(reflection);
        return this;
    }

    /**
     * Sets the received refraction property in this visual.
     *
     * @param refraction the new refraction of the visual (should be between 0 and 1)
     * @return this visual modified
     */
    public Material setRefraction(float refraction) {
        this.refraction = TTMath.clamp01(refraction);
        return this;
    }

    /**
     * Sets the received refractive index in this visual.
     *
     * @param refractiveIndex the new refractive index of the visual (should be between 1 and 10)
     * @return this visual modified
     */
    public Material setRefractiveIndex(float refractiveIndex) {
        this.refractiveIndex = TTMath.clamp(refractiveIndex, 1, 10);
        return this;
    }
}

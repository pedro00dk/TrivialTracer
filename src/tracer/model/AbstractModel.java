package tracer.model;

import tracer.data.visual.Material;

import java.util.Objects;

/**
 * Contains the basic implementation for all models.
 *
 * @author Pedro Henrique
 */
public abstract class AbstractModel implements Model {

    /**
     * The model material.
     */
    protected Material material;

    // The default attribute of the abstract models
    protected static final Material DEFAULT_MATERIAL = new Material();

    /**
     * Creates the model with the default material.
     */
    protected AbstractModel() {
        this(DEFAULT_MATERIAL);
    }

    /**
     * Creates the model with the received material.
     *
     * @param material the model material
     */
    protected AbstractModel(Material material) {
        this.material = Objects.requireNonNull(material, "The model material can not be null.").copy();
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Model setMaterial(Material material) {
        this.material = Objects.requireNonNull(material, "The model material can not be null.");
        return this;
    }
}

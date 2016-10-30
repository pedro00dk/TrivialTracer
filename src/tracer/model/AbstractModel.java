package tracer.model;

import tracer.model.material.Material;

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

    /**
     * The default material os the model.
     */
    protected static final Material DEFAULT_MATERIAL = new Material();

    /**
     * Creates the model with the default material.
     */
    public AbstractModel() {
        this(DEFAULT_MATERIAL.copy());
    }

    /**
     * Creates the model with the received material.
     *
     * @param material the model material
     */
    public AbstractModel(Material material) {
        this.material = Objects.requireNonNull(material, "The model material can not be null.");
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

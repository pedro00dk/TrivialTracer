package tracer.scene;

import tracer.data.material.Color;
import tracer.model.Model;
import tracer.util.Copyable;

import java.util.Objects;
import java.util.Set;

/**
 * The scene to be rendered, contains all models and background color.
 *
 * @author Pedro Henrique
 */
public class Scene implements Copyable<Scene> {

    /**
     * The models of the scene.
     */
    private Set<Model> models;

    /**
     * The background color of the scene.
     */
    private Color backgroundColor;

    /**
     * The default background color.
     */
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.white().scale(0.1f);

    /**
     * Creates the scene with the received models, or none model if not receive and the default background color
     * (black).
     *
     * @param models the models of the scene
     */
    public Scene(Set<Model> models) {
        this(models, DEFAULT_BACKGROUND_COLOR);
    }

    /**
     * Creates the scene with the received models, or none model if not receive and the received background color.
     *
     * @param models          the models of the scene
     * @param backgroundColor the background color of the scene
     */
    public Scene(Set<Model> models, Color backgroundColor) {
        this.models = Objects.requireNonNull(models, "The set of models can not be null.");
        this.backgroundColor = Objects.requireNonNull(backgroundColor, "The background color can not be null.");
    }

    @Override
    public Scene copy() {
        return new Scene(models, backgroundColor);
    }

    /**
     * Returns the list of models.
     *
     * @return the list of models
     */
    public Set<Model> getModels() {
        return models;
    }

    /**
     * Returns the background color of this scene.
     *
     * @return the background color
     */
    public Color getBackgroundColor() {
        return backgroundColor.copy();
    }

    /**
     * Sets the received set of models
     *
     * @param models the set of models
     */
    public void setModels(Set<Model> models) {
        this.models = Objects.requireNonNull(models, "The set of models can not be null.");
    }

    /**
     * Sets the background color of the scene.
     *
     * @param backgroundColor the new background color
     */
    public Scene setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}

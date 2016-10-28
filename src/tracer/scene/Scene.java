package tracer.scene;

import tracer.data.visual.Color;
import tracer.model.Model;
import tracer.util.Copyable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * The scene to be rendered, contains all {@link Model}s and background {@link Color}.
 *
 * @author Pedro Henrique
 */
public class Scene implements Copyable<Scene> {

    /**
     * All models of the scene.
     */
    private List<Model> models;

    /**
     * The emissive models of the scene.
     */
    private List<Model> lights;

    /**
     * The background color of the scene.
     */
    private Color backgroundColor;

    /**
     * The default background color.
     */
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.black();

    /**
     * Creates the scene with no models and default background color (dark gray)
     */
    public Scene() {
        this(new ArrayList<>(), DEFAULT_BACKGROUND_COLOR.copy());
    }

    /**
     * Creates the scene with the received models, or none model if not receive and the default background color
     * (black).
     *
     * @param models the models of the scene
     */
    public Scene(List<Model> models) {
        this(models, DEFAULT_BACKGROUND_COLOR);
    }

    /**
     * Creates the scene with no models and the received background color.
     *
     * @param backgroundColor the background color of the scene
     */
    public Scene(Color backgroundColor) {
        this(new ArrayList<>(), backgroundColor);
    }

    /**
     * Creates the scene with the received models, or none model if not receive and the received background color.
     *
     * @param models          the models of the scene
     * @param backgroundColor the background color of the scene
     */
    public Scene(List<Model> models, Color backgroundColor) {
        this.models = Objects.requireNonNull(models, "The set of models can not be null.");
        lights = new ArrayList<>();
        for (Model model : models) {
            if (model.getMaterial().isEmissive()) {
                lights.add(model);
            }
        }
        this.backgroundColor = Objects.requireNonNull(backgroundColor, "The background color can not be null.");
    }

    @Override
    public Scene copy() {
        return new Scene(models, backgroundColor);
    }

    /**
     * Adds the received model to the scene models.
     *
     * @param model the model to add (can not be null)
     */
    public void addModel(Model model) {
        models.add(Objects.requireNonNull(model, "The model can not be null."));
        if (model.getMaterial().isEmissive()) {
            lights.add(model);
        }
    }

    /**
     * Returns the collection of models of the scene. The collection should no be modified.
     *
     * @return the collection of models
     */
    public Collection<Model> getModels() {
        return models;
    }

    /**
     * Returns the collection of emissive models of the scene. The collection should no be modified.
     *
     * @return the collection of emissive models
     */
    public Collection<Model> getLights() {
        return lights;
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
     * Sets the background color of the scene.
     *
     * @param backgroundColor the new background color
     */
    public Scene setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}

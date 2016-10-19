package tracer.scene;

import tracer.data.material.Color;
import tracer.model.Model;
import tracer.util.Copyable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The scene to be rendered, contains all models and other scene info. This class implements the Iterable interface, to
 * get the models just iterate it.
 *
 * @author Pedro Henrique
 */
public class Scene implements Copyable<Scene>, Iterable<Model> {

    /**
     * The models of the scene.
     */
    private List<Model> models;

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
    public Scene(Model... models) {
        this(DEFAULT_BACKGROUND_COLOR, models);
    }

    /**
     * Creates the scene with the received models, or none model if not receive and the received background color.
     *
     * @param models          the models of the scene
     * @param backgroundColor the background color of the scene
     */
    public Scene(Color backgroundColor, Model... models) {
        this.models = new ArrayList<>();
        if (models != null) {
            for (Model model : models) {
                this.models.add(Objects.requireNonNull(model, "None of the received models can be null.").copy());
            }
        }
        this.backgroundColor = Objects.requireNonNull(backgroundColor, "The background color can not be null.").copy();
    }

    @Override
    public Scene copy() {
        return new Scene(backgroundColor, (Model[]) models.toArray());
    }

    @Override
    public Iterator<Model> iterator() {
        return models.iterator();
    }

    /**
     * Returns the background color of this scene.
     *
     * @return the background color
     */
    public Color getBackgroundColor() {
        return backgroundColor.copy();
    }
}

package loader;

import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.model.Model;
import tracer.model.material.Material;
import tracer.scene.Scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads SDL files and creates models.
 *
 * @author Pedro Henrique
 */
public final class SDLLoader {

    /**
     * Prevents instantiation.
     */
    private SDLLoader() {
    }

    public static Scene load(String sdlFilePath) throws IOException {
        Path path = Paths.get(sdlFilePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("The path does not exists.");
        } else if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("The path is a directory.");
        }
        List<Vector3> vertices = new ArrayList<>();
        Scene scene = new Scene();
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "background":
                    scene.setBackgroundColor(new Color(
                            Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])
                    ));
                    break;
                case "light":
                    String objLightFilePath = path.getParent().toString() + "\\" + parts[1];
                    List<Model> lights = OBJLoader.load(objLightFilePath);

                    Color emissiveColor = new Color(
                            Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4])
                    );
                    Material lightMaterial = new Material(emissiveColor);
                    for (Model light : lights) {
                        light.setMaterial(lightMaterial);
                        scene.addModel(light);
                    }
                    break;
                case "object":
                    String objFilePath = path.getParent().toString() + "\\" + parts[1];
                    List<Model> models = OBJLoader.load(objFilePath);

                    Color surfaceColor = new Color(
                            Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4])
                    );
                    // ka ignored
                    Material material = new Material(
                            surfaceColor,
                            Float.parseFloat(parts[6]),
                            Float.parseFloat(parts[7]),
                            Float.parseFloat(parts[8]),
                            Float.parseFloat(parts[9])
                    );
                    for (Model model : models) {
                        model.setMaterial(material);
                        scene.addModel(model);
                    }
                    break;
            }
        }
        return scene;
    }
}

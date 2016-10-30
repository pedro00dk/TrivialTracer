package loader;

import tracer.data.base.Vector3;
import tracer.model.Model;
import tracer.model.Triangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads OBJ files and creates models.
 *
 * @author Pedro Henrique
 */
public final class OBJLoader {

    /**
     * Prevents instantiation.
     */
    private OBJLoader() {
    }

    public static List<Model> load(String objFilePath) throws IOException {
        Path path = Paths.get(objFilePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("The path does not exists.");
        } else if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("The path is a directory.");
        }
        List<Vector3> vertices = new ArrayList<>();
        List<Model> faces = new ArrayList<>();
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "v":
                    vertices.add(new Vector3(
                            Float.parseFloat(parts[1].split("/")[0]),
                            Float.parseFloat(parts[2].split("/")[0]),
                            Float.parseFloat(parts[3].split("/")[0])
                    ));
                    break;
                case "f":
                    faces.add(new Triangle(
                            vertices.get(Integer.parseInt(parts[1].split("/")[0]) - 1),
                            vertices.get(Integer.parseInt(parts[2].split("/")[0]) - 1),
                            vertices.get(Integer.parseInt(parts[3].split("/")[0]) - 1)
                    ));
                    break;
            }
        }
        return faces;
    }
}

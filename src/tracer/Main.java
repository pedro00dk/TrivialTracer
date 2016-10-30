package tracer;

import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.model.Sphere;
import tracer.model.material.Material;
import tracer.renderer.RTRenderer;
import tracer.renderer.Renderer;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.scene.display.JDisplay;

import javax.swing.*;

/**
 * @author Pedro Henrique
 */
public class Main {

    public static void main(String[] args) {
        Scene scene = new Scene(Color.black());
        Camera camera = new Camera(Matrix4.rotationY(55f).transformAsDirection(new Vector3(0, 0, 20)),
                Vector3.zero(), Vector3.up(), (float) Math.toRadians(60)
        );
        Display display = new JDisplay(600, 600);

        //

        Material lightMaterial = new Material(Color.white());
        Material lightMaterial2 = new Material(Color.white().scale(0.8f));
        Material opaqueMaterial1 = new Material(Color.lightGray(), 1, 0, 0, 0);
        Material opaqueMaterial2 = new Material(Color.pink(), 1, 0, 0, 0);
        Material reflexiveMaterial = new Material(Color.yellow(), 0, 1, 0, 0); // testing color importance
        Material translucentMaterial = new Material(Color.orange(), 0, 0, 1, 1.1f); // testing color importance
        Material reflexiveAndTranslucentMaterial = new Material(Color.blue(), 1, 1, 1, 1.1f);

        //

        // Lights
        scene.addModel(new Sphere(new Vector3(6, 0, 12), 1.8f, lightMaterial));
        scene.addModel(new Sphere(new Vector3(-12, -12, -12), 0.3f, lightMaterial2));

        // Walls
        scene.addModel(new Sphere(new Vector3(10040, 0, 0), 10000, opaqueMaterial1));
        scene.addModel(new Sphere(new Vector3(-10040, 0, 0), 10000, opaqueMaterial1));
        scene.addModel(new Sphere(new Vector3(0, 10040, 0), 10000, opaqueMaterial1));
        scene.addModel(new Sphere(new Vector3(0, -10040, 0), 10000, opaqueMaterial1));
        scene.addModel(new Sphere(new Vector3(0, 0, 10040), 10000, opaqueMaterial1));
        scene.addModel(new Sphere(new Vector3(0, 0, -10040), 10000, opaqueMaterial1));

        // Some spheres
        scene.addModel(new Sphere(new Vector3(0, 0, 0), 2, reflexiveAndTranslucentMaterial));
        scene.addModel(new Sphere(new Vector3(5, 0, 0), 1.3f, reflexiveMaterial));
        scene.addModel(new Sphere(new Vector3(-5, 0, 0), 1.3f, translucentMaterial));
        scene.addModel(new Sphere(new Vector3(-9, 2, 2), 1.3f, opaqueMaterial2));
        scene.addModel(new Sphere(new Vector3(0, -5, 0), 1.3f, opaqueMaterial2));
        scene.addModel(new Sphere(new Vector3(0, 5, 0), 1.3f, opaqueMaterial2));
        scene.addModel(new Sphere(new Vector3(0, 0, -5), 1.3f, opaqueMaterial2));
        scene.addModel(new Sphere(new Vector3(0, 0, 5), 1.3f, opaqueMaterial2));

        JFrame frame = new JFrame();
        frame.setSize(650, 650);
        frame.add((JDisplay) display);

        Renderer renderer = new RTRenderer(scene, camera, display, Main::frameUpdateConsumer);
        renderer.start();

        frame.setVisible(true);
    }

    static void frameUpdateConsumer(Renderer r) {
        Vector3 cameraPosition = r.getCamera().getPosition();
        r.getCamera().setPosition(
                Matrix4.rotationY(
                        (float) Math.toRadians(12) * r.getFrameTime()
                ).transformAsPoint(cameraPosition)
        );
        System.out.println(r.getFrameRate());

    }
}

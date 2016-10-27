package tracer;

import tracer.data.Matrix4;
import tracer.data.Vector3;
import tracer.data.material.Color;
import tracer.data.material.Material;
import tracer.model.Sphere;
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
        Scene scene = new Scene(Color.darkGray());
        Camera camera = new Camera(new Vector3(12, 0.4f, 0), Vector3.zero());
        Display display = new JDisplay(600, 600);

        //

        Material lightMaterial = new Material(Color.white());
        Material lightMaterial2 = new Material(Color.white().scale(0.8f));

        Material wallOpaqueMaterial = new Material(Color.white().scale(0.7f), 1, 0, 0, 0);
        Material opaqueMaterial = new Material(Color.pink(), 1, 0, 0, 0);
        Material reflexiveMaterial = new Material(Color.yellow().scale(0.75f), 0, 1, 0, 0);
        Material translucentMaterial = new Material(Color.white(), 0, 0, 1, 1.03f);
        Material reflexiveAndTranslucentMaterial = new Material(Color.white(), 1, 0.5f, 0.5f, 1.1f);

        //

        // Lights
        scene.addModel(new Sphere(new Vector3(12, 12, -12), 1.8f, lightMaterial));
        scene.addModel(new Sphere(new Vector3(-12, -12, 12), 0.3f, lightMaterial2));

        // Walls
        scene.addModel(new Sphere(new Vector3(10015, 0, 0), 10000, wallOpaqueMaterial));
        scene.addModel(new Sphere(new Vector3(-10015, 0, 0), 10000, wallOpaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 10015, 0), 10000, wallOpaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, -10015, 0), 10000, wallOpaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 0, 10015), 10000, wallOpaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 0, -10015), 10000, wallOpaqueMaterial));


        // Some spheres
        scene.addModel(new Sphere(new Vector3(0, 0, 0), 2, reflexiveMaterial));
        scene.addModel(new Sphere(new Vector3(-5, 0, 0), 1.3f, translucentMaterial));
        scene.addModel(new Sphere(new Vector3(-9, 2, 2), 1.3f, opaqueMaterial));
        scene.addModel(new Sphere(new Vector3(5, 0, 0), 1.3f, reflexiveAndTranslucentMaterial));
        scene.addModel(new Sphere(new Vector3(0, -5, 0), 1.3f, opaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 5, 0), 1.3f, opaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 0, -5), 1.3f, opaqueMaterial));
        scene.addModel(new Sphere(new Vector3(0, 0, 5), 1.3f, opaqueMaterial));

        //

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
                        (float) Math.toRadians(5) * r.getFrameTime()
                ).transformAsPoint(cameraPosition)
        );
        System.out.println(r.getFrameRate());
    }
}

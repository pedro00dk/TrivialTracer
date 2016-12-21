import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.data.visual.Material;
import tracer.model.Model;
import tracer.model.Sphere;
import tracer.model.Torus;
import tracer.renderer.MTRenderer;
import tracer.renderer.PTRenderer;
import tracer.renderer.RTRenderer;
import tracer.renderer.Renderer;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.scene.display.JDisplay;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Pedro Henrique
 */
public class Main2 {

    public static void main(String[] args) throws IOException {

        //
        Scene scene = new Scene();
        Camera camera = new Camera(Vector3.back().scale(30).sum(Vector3.up().scale(10)), Vector3.up().scale(10));
        Display display = new JDisplay(600, 600);
        //

        //
        Material lightMat = new Material(Color.white());

        Material redWallMat = new Material(Color.red(), 1, 0, 0, 1);
        Material whiteWallMat = new Material(Color.white(), 1, 0, 0, 1);
        Material greenWallMat = new Material(Color.green(), 1, 0, 0, 1);

        Material sphere1Mat = new Material(Color.black(), 0, 1, 0, 1);
        Material sphere2Mat = new Material(Color.black(), 0, 0, 1, 1.2f);
        //

        //
        scene.addModel(new Sphere(Vector3.up().scale(19), 2, lightMat));

        scene.addModel(new Sphere(Vector3.left().scale(10010), 10000, redWallMat));
        scene.addModel(new Sphere(Vector3.forward().scale(10010), 10000, whiteWallMat));
        scene.addModel(new Sphere(Vector3.up().scale(10020), 10000, whiteWallMat));
        scene.addModel(new Sphere(Vector3.right().scale(10010), 10000, greenWallMat));
        scene.addModel(new Sphere(Vector3.down().scale(10000), 10000, whiteWallMat));

        scene.addModel(new Sphere(new Vector3(5, 5, 3), 5, sphere1Mat));
        scene.addModel(new Sphere(new Vector3(-4, 3, 1), 3, sphere2Mat));

        //scene.addModel(new Torus(redWallMat, Vector3.up().scale(8).sum(Vector3.forward().scale(7)), Vector3.forward(), 2f, 5f));
        //

        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.add((JDisplay) display);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        //Renderer renderer = new RTRenderer(scene, camera, display);
        Renderer renderer = new PTRenderer(scene, camera, display, Main2::frameUpdateConsumer);
        renderer.start();

        frame.setVisible(true);
    }

    static void frameUpdateConsumer(Renderer r) {
        Matrix4 rot = Matrix4.rotationY((float) Math.toRadians(10));
        for (Model m : r.getScene().getModels()) {
            if (m instanceof Torus) {
                Torus t = (Torus) m;
                t.setAxis(rot.transformAsDirection(t.getAxis()));
            }
        }
        System.out.println(r.getFrameRate());

    }
}

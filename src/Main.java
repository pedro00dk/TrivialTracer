import loader.SDLLoader;
import tracer.data.base.Matrix4;
import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.model.Model;
import tracer.renderer.MTRenderer;
import tracer.renderer.PTRenderer;
import tracer.renderer.RTRenderer;
import tracer.renderer.Renderer;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.scene.display.JDisplay;
import tracer.util.TTRand;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Pedro Henrique
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Scene scene = SDLLoader.load("model\\cornellroom.sdl");
        Camera camera = new Camera(new Vector3(0, 0, 1f),
                Vector3.zero(), Vector3.up(), (float) Math.toRadians(25)
        );
        Display display = new JDisplay(650, 650);

        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.add((JDisplay) display);

        Renderer renderer = new MTRenderer(new PTRenderer(scene, camera, display, Main::frameUpdateConsumer), 8);
        renderer.start();

        frame.setVisible(true);
    }

    static void frameUpdateConsumer(Renderer r) {
        Vector3 cameraPosition = r.getCamera().getPosition();
        r.getCamera().setPosition(
                Matrix4.rotationY(
                        (float) Math.toRadians(0) * r.getFrameTime()
                ).transformAsPoint(cameraPosition)
        );
        for (Model light : r.getScene().getLights()) {
            //light.getMaterial().setEmissiveColor(Color.white().scale(TTRand.range(0.1f, 1)));
        }
        System.out.println(r.getFrameRate());

    }
}

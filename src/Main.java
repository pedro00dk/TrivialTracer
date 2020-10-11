import java.io.IOException;
import javax.swing.JFrame;
import loader.SDLLoader;
import tracer.data.base.Vector3;
import tracer.data.visual.Color;
import tracer.data.visual.Material;
import tracer.model.Sphere;
import tracer.renderer.AbstractRenderer;
import tracer.renderer.MTRenderer;
import tracer.renderer.PTRenderer;
import tracer.renderer.RTRenderer;
import tracer.renderer.Renderer;
import tracer.scene.Camera;
import tracer.scene.Display;
import tracer.scene.Scene;
import tracer.scene.display.JDisplay;


/**
 * @author Pedro Henrique
 */
public class Main {

    public static void main(String[] args) throws IOException {
        var env = System.getenv();
        boolean cornelExample = env.containsKey("CORNEL") && env.get("CORNEL").equals("true");
        boolean pathTracer = env.containsKey("PATH_TRACE") && env.get("PATH_TRACE").equals("true");
        boolean multiThreaded = env.containsKey("THREADS") && env.get("THREADS").equals("true");
        System.out.println(cornelExample);
        if (!cornelExample) spheres(pathTracer, multiThreaded);
        else cornellRoom(pathTracer, multiThreaded);
    }

    static void frameUpdateConsumer(Renderer r) {
        System.out.println(r.getFrameRate());
    }

    static void spheres(boolean pathTracer, boolean multiThreaded) {
        Scene scene = new Scene();
        Camera camera = new Camera(Vector3.back().scale(30).sum(Vector3.up().scale(10)), Vector3.up().scale(10));
        Display display = new JDisplay(800, 800);
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

        // scene
        //     .addModel(
        //         new Torus(redWallMat, Vector3.up().scale(8).sum(Vector3.forward().scale(7)), Vector3.forward(), 2f, 5f)
        //     );

        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.add((JDisplay) display);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        // Renderer renderer = new RTRenderer(scene, camera, display);
        AbstractRenderer renderer = pathTracer
            ? new PTRenderer(scene, camera, display, Main::frameUpdateConsumer)
            : new RTRenderer(scene, camera, display, Main::frameUpdateConsumer);
        if (multiThreaded) renderer = new MTRenderer(renderer, Runtime.getRuntime().availableProcessors());
        renderer.start();

        frame.setVisible(true);
    }

    static void cornellRoom(boolean pathTracer, boolean multiThreaded) throws IOException {
        String scenePath = Main.class.getResource("model/cornellroom.sdl").getPath();
        Scene scene = SDLLoader.load(scenePath);
        Camera camera = new Camera(new Vector3(0, 0, 1f), Vector3.zero(), Vector3.up(), (float) Math.toRadians(25));
        Display display = new JDisplay(800, 800);

        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.add((JDisplay) display);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        AbstractRenderer renderer = pathTracer
            ? new PTRenderer(scene, camera, display, Main::frameUpdateConsumer)
            : new RTRenderer(scene, camera, display, Main::frameUpdateConsumer);
        if (multiThreaded) renderer = new MTRenderer(renderer, Runtime.getRuntime().availableProcessors());
        renderer.start();

        frame.setVisible(true);
    }

}

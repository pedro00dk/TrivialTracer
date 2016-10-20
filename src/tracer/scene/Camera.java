package tracer.scene;

import tracer.data.Matrix4;
import tracer.data.Vector3;
import tracer.util.Copyable;

import java.util.Objects;

/**
 * Represents a camera in the {@link Scene}, used to calculate and get the origin and direction of the rays to generate
 * the frame, too contains the vertical field of view.
 * <p>
 * Unlike other objects, the camera properties are copied from the received parameters, because of the relationship
 * between the camera vectors.
 *
 * @author Pedro Henrique
 * @see Scene
 */
public class Camera implements Copyable<Camera> {

    /**
     * The position of the camera.
     */
    private Vector3 position;

    /**
     * The position that the camera is looking (is a point, not a direction).
     */
    private Vector3 target;

    /**
     * The up direction of the camera.
     */
    private Vector3 up;

    /**
     * The vertical field of view of the camera in radians. The horizontal fov depends of the frame aspect ratio.
     */
    private float fovy;

    /**
     * The default position of the camera.
     */
    private static final Vector3 DEFAULT_POSITION = Vector3.zero();

    /**
     * The default target of the camera.
     */
    private static final Vector3 DEFAULT_TARGET = Vector3.forward();

    /**
     * The default up direction of the camera.
     */
    private static final Vector3 DEFAULT_UP = Vector3.up();

    /**
     * The default vertical field of view of the camera in radians.
     */
    private static final float DEFAULT_FOVY = (float) Math.PI / 3; // 60 degrees

    /**
     * Creates a camera with the default position (0, 0, 0), the default target (0, 0, 1) and the default up (0, 1, 0)
     * and default vertical field of view (60).
     */
    public Camera() {
        this(DEFAULT_POSITION, DEFAULT_TARGET, DEFAULT_UP, DEFAULT_FOVY);
    }

    /**
     * Creates the camera with the received position, the target is calculated adding the received position with
     * Vector3.forward(), the up direction is set as default (0, 1, 0) and default vertical field of view (60).
     *
     * @param position the position of the camera
     */
    public Camera(Vector3 position) {
        this(position, Vector3.sum(position, DEFAULT_TARGET), DEFAULT_UP, DEFAULT_FOVY);
    }

    /**
     * Creates the camera with the received parameters, the up direction is set as default (0, 1, 0) and default
     * vertical field of view (60).
     *
     * @param position the position of the camera
     * @param target   the target of the camera, should be different of the position
     */
    public Camera(Vector3 position, Vector3 target) {
        this(position, target, DEFAULT_UP, DEFAULT_FOVY);
    }

    /**
     * Creates the camera with the received parameters and default vertical field of view (60).
     *
     * @param position the position of the camera
     * @param target   the target of the camera, should be different of the position
     * @param up       the direction of the camera, the magnitude should be greater than 0
     */
    public Camera(Vector3 position, Vector3 target, Vector3 up) {
        this(position, target, up, DEFAULT_FOVY);
    }

    /**
     * Creates the camera with the received parameters.
     *
     * @param position the position of the camera
     * @param target   the target of the camera, should be different of the position
     * @param up       the direction of the camera, the magnitude should be greater than 0
     * @param fovy     the vertical field of view of the camera in radians, should between 180
     */
    public Camera(Vector3 position, Vector3 target, Vector3 up, float fovy) {
        Objects.requireNonNull(position, "The camera position can not be null.");
        Objects.requireNonNull(target, "The camera target can not be null.");
        Objects.requireNonNull(up, "The camera up direction can not be null");
        if (position.equals(target)) {
            throw new IllegalArgumentException("The position can not be equals to the target.");
        } else if (up.sqrMag() == 0) {
            throw new IllegalArgumentException("The up magnitude should be greater than 0.");
        } else if (Vector3.sub(target, position).cos(up) == 1) {
            throw new IllegalArgumentException("The looking direction and up direction are the same.");
        }
        if (fovy <= 0 || fovy >= (float) Math.PI) {
            throw new IllegalArgumentException("The fovy should be between 0 and PI.");
        }
        // This class uses this copies to prevents errors (checked above)
        this.position = position.copy();
        this.target = target.copy();
        this.up = up.copy();
        this.fovy = fovy;
    }

    @Override
    public Camera copy() {
        return new Camera(position, target, up, fovy);
    }

    /**
     * Returns a copy of the current position of the camera.
     *
     * @return the position of the camera
     */
    public Vector3 getPosition() {
        return position.copy();
    }

    /**
     * Returns a copy of the current target of the camera.
     *
     * @return the target of the camera
     */
    public Vector3 getTarget() {
        return target.copy();
    }

    /**
     * Returns a copy of the current up direction of the camera.
     *
     * @return the up direction of the camera
     */
    public Vector3 getUp() {
        return up.copy();
    }

    /**
     * Returns the vertical field of view of the camera in radians. The horizontal field of view depends of the aspect
     * ratio.
     *
     * @return the vertical field of view of the camera in radians
     */
    public float getFovy() {
        return fovy;
    }

    /**
     * Returns the direction that the camera is looking at.
     *
     * @return the front direction of the camera
     */
    public Vector3 getForwardDirection() {
        return Vector3.sub(target, position).normalize();
    }

    /**
     * Returns the right side direction of the camera.
     *
     * @return the right side direction of the camera
     */
    public Vector3 getRightDirection() {
        Vector3 frontDirection = Vector3.sub(target, position);
        return Vector3.cross(up, frontDirection).normalize();
    }

    /**
     * The real up direction of the camera, calculated using the position, target and up properties and directions.
     *
     * @return the real up direction of the camera
     */
    public Vector3 getUpDirection() {
        Vector3 frontDirection = Vector3.sub(target, position);
        Vector3 sideDirection = Vector3.cross(up, frontDirection);
        return Vector3.cross(frontDirection, sideDirection).normalize();
    }

    /**
     * Sets the received position in the camera.
     *
     * @param position the new position of the camera
     */
    public void setPosition(Vector3 position) {
        if (position.equals(target)) {
            throw new IllegalArgumentException("The position can not be equals to the target.");
        }
        this.position = position.copy();
    }

    /**
     * Sets the received target in the camera.
     *
     * @param target the new target of the camera
     */
    public void setTarget(Vector3 target) {
        if (position.equals(target)) {
            throw new IllegalArgumentException("The position can not be equals to the target.");
        } else if (Vector3.sub(target, position).cos(up) == 1) {
            throw new IllegalArgumentException("The looking direction and up direction are the same.");
        }
        this.target = target.copy();
    }

    /**
     * Sets the received up direction in the camera.
     *
     * @param up the new up direction of the camera
     */
    public void setUp(Vector3 up) {
        if (up.sqrMag() == 0) {
            throw new IllegalArgumentException("The up magnitude should be greater than 0.");
        } else if (Vector3.sub(target, position).cos(up) == 1) {
            throw new IllegalArgumentException("The looking direction and up direction are the same.");
        }
        this.up = up.copy();
    }

    /**
     * Sets the vertical field of view of the camera in radians.
     *
     * @param fovy the new vertical field of view of the camera in radians, should be between 0 and PI / 2.
     */
    public void setFov(float fovy) {
        if (fovy <= 0 || fovy >= (float) Math.PI) {
            throw new IllegalArgumentException("The fovy should be between 0 and PI.");
        }
        this.fovy = fovy;
    }

    /**
     * Sets the position properties of the camera.
     *
     * @param position the new position of the camera.
     * @param target   the new target of the camera, should be different from the position.
     * @param up       the new up direction of the camera, should have the magnitude greater than 0.
     */
    public void lookAt(Vector3 position, Vector3 target, Vector3 up) {
        Objects.requireNonNull(position, "The camera position can not be null.");
        Objects.requireNonNull(target, "The camera target can not be null.");
        Objects.requireNonNull(up, "The camera up direction can not be null");
        if (position.equals(target)) {
            throw new IllegalArgumentException("The position can not be equals to the target.");
        } else if (up.sqrMag() == 0) {
            throw new IllegalArgumentException("The up magnitude should be greater than 0.");
        } else if (Vector3.sub(target, position).cos(up) == 1) {
            throw new IllegalArgumentException("The looking direction and up direction are the same.");
        }
        // This class uses this copies to prevents errors (checked above)
        this.position = position.copy();
        this.target = target.copy();
        this.up = up.copy();
    }

    // Matrix methods

    /**
     * Returns a transform matrix from the camera space to the world space, the world space is canonical.
     *
     * @return a transform to the camera to the world space.
     */
    public Matrix4 cameraToWorldSpaceTransform() {
        Vector3 frontDirection = Vector3.sub(target, position).normalize();
        Vector3 sideDirection = Vector3.cross(up, frontDirection).normalize();
        Vector3 upDirection = Vector3.cross(frontDirection, sideDirection).normalize();
        return new Matrix4(
                sideDirection.x, upDirection.x, frontDirection.x, position.x,
                sideDirection.y, upDirection.y, frontDirection.y, position.y,
                sideDirection.z, upDirection.z, frontDirection.z, position.z,
                0, 0, 0, 1
        );
    }

    /**
     * Returns a transform matrix from the world space to the camera space, the world space is canonical.
     *
     * @return a transform to the world to the camera space.
     */
    public Matrix4 worldToCameraSpaceTransform() {
        Vector3 frontDirection = Vector3.sub(target, position).normalize();
        Vector3 sideDirection = Vector3.cross(up, frontDirection).normalize();
        Vector3 upDirection = Vector3.cross(frontDirection, sideDirection).normalize();
        return new Matrix4(
                sideDirection.x, upDirection.x, frontDirection.x, position.x,
                sideDirection.y, upDirection.y, frontDirection.y, position.y,
                sideDirection.z, upDirection.z, frontDirection.z, position.z,
                0, 0, 0, 1
        ).inverse();
    }
}

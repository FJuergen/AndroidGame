package de.hs_kl.gatav.gles05colorcube;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.gles05colorcube.shaders.StaticShader;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView touchableGLSurfaceView;

    private final int MENU_RESET = 1, MENU_PAN = 2, MENU_ZOOM = 3;
    private final int GROUP_DEFAULT = 0, GROUP_PAN = 1, GROUP_ZOOM = 2;
    private boolean PAN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        touchableGLSurfaceView = new TouchableGLSurfaceView(this);
        setContentView(touchableGLSurfaceView);
        touchableGLSurfaceView.setFocusableInTouchMode(true);
        touchableGLSurfaceView.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(GROUP_DEFAULT, MENU_RESET, 0, "Reset");
        menu.add(GROUP_PAN, MENU_PAN, 0, "Pan");
        menu.add(GROUP_ZOOM, MENU_ZOOM, 0, "Zoom");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (PAN) {
            menu.setGroupVisible(GROUP_PAN, false);
            menu.setGroupVisible(GROUP_ZOOM, true);
        } else {
            menu.setGroupVisible(GROUP_PAN, true);
            menu.setGroupVisible(GROUP_ZOOM, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                TouchableGLSurfaceView.resetViewing();
                Toast.makeText(this, "trackball reset", Toast.LENGTH_SHORT).show();
                touchableGLSurfaceView.requestRender();
                return true;
            case MENU_PAN:
                Toast.makeText(this, "panning activated", Toast.LENGTH_SHORT).show();
                PAN = true;
                TouchableGLSurfaceView.guiZoom = false;
                return true;
            case MENU_ZOOM:
                Toast.makeText(this, "zooming activated", Toast.LENGTH_SHORT).show();
                PAN = false;
                TouchableGLSurfaceView.guiZoom = true;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        touchableGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        touchableGLSurfaceView.onPause();
    }

}

// touchable GLSurfaceView with
// an implementation of a virtual trackball rotation control
class TouchableGLSurfaceView extends GLSurfaceView {
    private OurRenderer ourRenderer;
    private StaticShader shader = new StaticShader();

    static public boolean guiZoom = true;
    // possible touch states
    final static int NONE = 0;
    final static int ROTATE = 1;
    final static int ZOOM = 2;
    final static int PAN = 3;
    int touchState = NONE;

    final static float MIN_DIST = 50;
    static int oldDistance = 0;
    static int centerX = 0, centerY = 0;
    static int oldCenterX = 0, oldCenterY = 0;

    static float EYE_DISTANCE, EYE_DISTANCE_INC;
    static float PAN_X, PAN_Y, PAN_INC;
    static float CURRENT_QUATERNION[], LAST_QUATERNION[];
    static float TRANSFORM_MATRIX[];

    static int OLD_MOUSE_X, OLD_MOUSE_Y, MOUSE_BUTTON_PRESSED;

    static int WINDOW_W = 600;
    static int WINDOW_H = 800;

    static float zNear = 1.0f, zFar = 1000.0f;

    static {
        CURRENT_QUATERNION = new float[4];
        LAST_QUATERNION = new float[4];
        TRANSFORM_MATRIX = new float[16];
    }

    public TouchableGLSurfaceView(Context context) {
        super(context);
        ourRenderer = new OurRenderer();
        setRenderer(ourRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float p1x, p1y, p2x, p2y;
        // normalize mouse positions
        p1x = (2.0f * OLD_MOUSE_X - WINDOW_W) / WINDOW_W;
        p1y = (WINDOW_H - 2.0f * OLD_MOUSE_Y) / WINDOW_H;
        p2x = (2.0f * x - WINDOW_W) / WINDOW_W;
        p2y = (WINDOW_H - 2.0f * y) / WINDOW_H;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchState = ROTATE;
                OLD_MOUSE_X = (int) x;
                OLD_MOUSE_Y = (int) y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // secondary touch event starts: remember distance
                oldDistance = (int) calcDistance(event);
                // and midpoint
                calcMidpoint(event);
                oldCenterX = centerX;
                oldCenterY = centerY;
                if (oldDistance > MIN_DIST) {
                    if (guiZoom) {
                        touchState = ZOOM;
                    } else {
                        touchState = PAN;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchState == ROTATE) {
                    // single finger rotate
                    Trackball.trackball(LAST_QUATERNION, p1x, p1y, p2x, p2y);
                    OLD_MOUSE_X = (int) x;
                    OLD_MOUSE_Y = (int) y;
                    Trackball.add_quats(LAST_QUATERNION, CURRENT_QUATERNION, CURRENT_QUATERNION);
                    requestRender();
                } else if (touchState == ZOOM) {
                    // double-finger zoom, zoom depends on changing distance
                    int dist = (int) calcDistance(event);
                    if (dist > MIN_DIST) {
                        if (dist > oldDistance)
                            EYE_DISTANCE -= EYE_DISTANCE_INC;
                        else if (dist < oldDistance)
                            EYE_DISTANCE += EYE_DISTANCE_INC;
                        oldDistance = dist;
                        requestRender();
                    }
                } else if (touchState == PAN) {
                    int dist = (int) calcDistance(event);
                    calcMidpoint(event);
                    if (dist > MIN_DIST) {
                        if (centerX > oldCenterX)
                            PAN_X -= PAN_INC;
                        if (centerX < oldCenterX)
                            PAN_X += PAN_INC;
                        if (centerY > oldCenterY)
                            PAN_Y += PAN_INC;
                        if (centerY < oldCenterY)
                            PAN_Y -= PAN_INC;
                        oldCenterX = centerX;
                        oldCenterY = centerY;
                        requestRender();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                touchState = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchState = ROTATE;
                // update touch down location for drag event to holding finger
                switch (event.getActionIndex()) {
                    case 0:
                        OLD_MOUSE_X = (int) event.getX(1);
                        OLD_MOUSE_Y = (int) event.getY(1);
                        break;
                    case 1:
                        OLD_MOUSE_X = (int) event.getX(0);
                        OLD_MOUSE_Y = (int) event.getY(0);
                        break;
                }
                break;
        }
        return true;
    }

    private float calcDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void calcMidpoint(MotionEvent event) {
        centerX = (int) ((event.getX(0) + event.getX(1)) / 2);
        centerY = (int) ((event.getY(0) + event.getY(1)) / 2);
    }

    // the implementation of the renderer interface
    private class OurRenderer implements GLSurfaceView.Renderer {

        private ColorCube colorCube;

        public OurRenderer() {
            colorCube = new ColorCube();
        }

        public void onDrawFrame(GL10 gl) {
            shader.start();
            // the first thing to do: clear screen and depth buffer
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            // reset modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            // manipulate modelview matrix by setting viewing transformation
            gl.glTranslatef(-PAN_X, -PAN_Y, -EYE_DISTANCE);
            Trackball.build_rotmatrix(TRANSFORM_MATRIX, CURRENT_QUATERNION);
            gl.glMultMatrixf(TRANSFORM_MATRIX, 0);

            colorCube.draw(gl);
            shader.stop();
        }

        // resize of viewport
        // set projection matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            float aspectRatio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 45.0f, aspectRatio, zNear, zFar);
            GLU.gluLookAt(gl, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
        }

        // creation of viewport
        // initialization of some opengl features
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            gl.glClearColor(0, 0, 0, 1);
            gl.glEnable(GL10.GL_CULL_FACE);
            //gl.glShadeModel(GL10.GL_FLAT);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);

            resetViewing();
        }
    }

    // reset of view parameters
    static void resetViewing() {
        EYE_DISTANCE = 0.5f;
        EYE_DISTANCE_INC = 0.2f;
        PAN_X = 0.0f;
        PAN_Y = 0.0f;
        PAN_INC = 0.1f;

        // trackball init
        Trackball.trackball(CURRENT_QUATERNION, 0.0f, 0.0f, 0.0f, 0.0f);
    }
}

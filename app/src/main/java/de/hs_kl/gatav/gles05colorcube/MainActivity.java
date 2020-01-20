package de.hs_kl.gatav.gles05colorcube;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.models.RawModel;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.objConverter.ModelData;
import de.hs_kl.gatav.gles05colorcube.objConverter.OBJFileLoader;
import de.hs_kl.gatav.gles05colorcube.objConverter.Vector3f;
import de.hs_kl.gatav.gles05colorcube.renderEngine.Loader;
import de.hs_kl.gatav.gles05colorcube.renderEngine.MasterRenderer;
import de.hs_kl.gatav.gles05colorcube.shaders.StaticShader;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.gameLogic.GameManager;
import de.hs_kl.gatav.gles05colorcube.toolbox.Maths;


public class MainActivity extends AppCompatActivity {
    private GLSurfaceView touchableGLSurfaceView;
    public static AssetManager assetManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Debug.waitForDebugger();
        MainActivity.assetManager = getAssets();

        touchableGLSurfaceView = new TouchableGLSurfaceView(this);
        setContentView(touchableGLSurfaceView);
        touchableGLSurfaceView.setFocusableInTouchMode(true);
        touchableGLSurfaceView.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
class TouchableGLSurfaceView extends GLSurfaceView{
    private OurRenderer ourRenderer;
    private GameManager gameManager;
    private StaticShader shader;
    private RotationSensor sensor;

    Loader loader;

    public TouchableGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(3);

        sensor = new RotationSensor(context);
        ourRenderer = new OurRenderer();
        setRenderer(ourRenderer);
        gameManager = new GameManager(MainActivity.assetManager);
        gameManager.loadLevel(1);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    // the implementation of the renderer interface
    private class OurRenderer implements GLSurfaceView.Renderer {
        private MasterRenderer renderer;

        RawModel model;

        ModelTexture texture;
        TexturedModel texturedModel;
        Entity entity;
        Light light;

        Camera camera = new Camera();

        // Declare as volatile because we are updating it from another thread
        public OurRenderer() {
        }

        public void onDrawFrame(GL10 gl) {
            float[] rotations = sensor.getDeviceRotation();
            entity.setRotv(rotations[3]);
            entity.setRotx(rotations[0]);
            entity.setRoty(rotations[1]);
            entity.setRotz(rotations[2]);
            renderer.processEntity(entity);
            renderer.render(light,camera);
        }

        // resize of viewport
        // set projection matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        // creation of viewport
        // initialization of some opengl features
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            renderer = new MasterRenderer();
            loader = new Loader();
            ModelData modelData = OBJFileLoader.loadOBJ("dragon");
            model = loader.loadToVAO(modelData.getVertices(),modelData.getIndices(),modelData.getNormals(),modelData.getTextureCoords());
            texture = new ModelTexture(loader.loadTexture("purple.png"));
            texturedModel = new TexturedModel(model,texture);
            texture.setReflectivity(0.75f);
            texture.setShineDamper(10);
            entity = new Entity(texturedModel, new Vector3f(0,0,-15),0,0,0,1);
            light = new Light(new Vector3f(0,5,-5),new Vector3f(1,0,1));
            shader = new StaticShader();

        }
    }
}

class RotationSensor extends Activity implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor rotation;

    float[] rotations = {0,0,0,0};

    private final float epsilon = 0.0f;




    public RotationSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            for(int i = 0; i<event.values.length; i++){
                if(Math.abs(event.values[i])>epsilon){
                    rotations[i] = -event.values[i];
                }
            }
        }
    }
    public float[] getDeviceRotation(){
        return rotations;
    }

}


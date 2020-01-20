package de.hs_kl.gatav.gles05colorcube;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.gameLogic.GameManager;
import de.hs_kl.gatav.gles05colorcube.models.RawModel;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.normalMappingObjConverter.NormalMappedObjLoader;
import de.hs_kl.gatav.gles05colorcube.objConverter.ModelData;
import de.hs_kl.gatav.gles05colorcube.objConverter.OBJFileLoader;
import de.hs_kl.gatav.gles05colorcube.renderEngine.Loader;
import de.hs_kl.gatav.gles05colorcube.renderEngine.MasterRenderer;
import de.hs_kl.gatav.gles05colorcube.shaders.StaticShader;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.toolbox.RotationSensor;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector4f;

// touchable GLSurfaceView with
// an implementation of a virtual trackball rotation control
public class TouchableGLSurfaceView extends GLSurfaceView {
    private OurRenderer ourRenderer;
    private StaticShader shader;
    public static RotationSensor rotationSensor;
    private GameManager gameManager;

    Loader loader;

    public TouchableGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(3);
        ourRenderer = new OurRenderer();
        rotationSensor = new RotationSensor(context);
        gameManager = new GameManager(MainActivity.assetManager);
        gameManager.loadLevel(1);
        setRenderer(ourRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    // the implementation of the renderer interface
    private class OurRenderer implements GLSurfaceView.Renderer {
        private MasterRenderer renderer;

        RawModel model;

        List<Light> lights = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        List<Entity> normalEntities = new ArrayList<>();

        ModelTexture texture;
        TexturedModel texturedModel;

        Camera camera = new Camera();

        // Declare as volatile because we are updating it from another thread
        public OurRenderer() {
        }

        public void onDrawFrame(GL10 gl) {
            gameManager.onDrawFrame();

            float[] rotations = rotationSensor.getDeviceRotation();
            //entity.increaseRotation((float)Math.toDegrees(rotations[0]), (float)Math.toDegrees(rotations[1]), (float)Math.toDegrees(rotations[2]));
            for(Entity entity : normalEntities) {
                entity.setRotx(-rotations[1]);
                entity.setRoty(rotations[2]);
                entity.setRotz(-rotations[0]);
            }
            //camera.setPitch(rotations[1]);
            //camera.setYaw(rotations[0]);
            renderer.renderScene(entities, normalEntities, lights,new Vector4f(0, -1, 0, 100000), camera);
        }

        // resize of viewport
        // set projection matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        // creation of viewport
        // initialization of some opengl features
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            renderer = new MasterRenderer(camera);
            loader = new Loader();
            ModelData modelData = OBJFileLoader.loadOBJ("dragon");
            model = loader.loadToVAO(modelData.getVertices(),modelData.getIndices(),modelData.getNormals(),modelData.getTextureCoords());
            texture = new ModelTexture(loader.loadTexture("purple"));
            texturedModel = new TexturedModel(model,texture);
            texture.setReflectivity(0.75f);
            texture.setShineDamper(10);
            lights.add(new Light(new Vector3f(0,50,-50),new Vector3f(.5f,.5f,.5f)));
            lights.add(new Light(new Vector3f(0,0,5),new Vector3f(0,2,0), new Vector3f(1,0.01f,0.002f)));
            lights.add(new Light(new Vector3f(0,-5,5),new Vector3f(0,0,2), new Vector3f(1,0.01f,0.002f)));
            lights.add(new Light(new Vector3f(0,-5,-5),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));


            TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                    new ModelTexture(loader.loadTexture("barrel")));
            barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
            barrelModel.getTexture().setShineDamper(10);
            barrelModel.getTexture().setReflectivity(0.5f);

            TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
                    new ModelTexture(loader.loadTexture("crate")));
            crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
            crateModel.getTexture().setShineDamper(10);
            crateModel.getTexture().setReflectivity(0.5f);

            TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
                    new ModelTexture(loader.loadTexture("boulder")));
            boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
            boulderModel.getTexture().setShineDamper(10);
            boulderModel.getTexture().setReflectivity(0.5f);


            Entity entity = new Entity(barrelModel, new Vector3f(0, 0, -15), 0f, 0f, 0f, 1f);
            Entity entity2 = new Entity(boulderModel, new Vector3f(0, 0, -15), 0, 0, 0, 1f);
            Entity entity3 = new Entity(crateModel, new Vector3f(0, 0, -15), 0, 0, 0, 0.04f);

            normalEntities.add(entity2);
            //normalEntities.add(entity2);
            //normalEntities.add(entity3);
            //entities.add(new Entity(texturedModel, new Vector3f(0,0,-15),0,0,0, 1, 1));

            shader = new StaticShader();
        }
    }
}


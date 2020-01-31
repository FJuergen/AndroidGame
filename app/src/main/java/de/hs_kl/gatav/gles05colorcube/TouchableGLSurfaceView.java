package de.hs_kl.gatav.gles05colorcube;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.fonts.Font;
import android.opengl.GLSurfaceView;
import android.provider.CalendarContract;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.fontRendering.TextMaster;
import de.hs_kl.gatav.gles05colorcube.fonts.FontType;
import de.hs_kl.gatav.gles05colorcube.fonts.GUIText;
import de.hs_kl.gatav.gles05colorcube.gameLogic.GameManager;
import de.hs_kl.gatav.gles05colorcube.gameLogic.Player;
import de.hs_kl.gatav.gles05colorcube.gameLogic.Tile;
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
import de.hs_kl.gatav.gles05colorcube.vector.Vector2f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector4f;

// touchable GLSurfaceView with
// an implementation of a virtual trackball rotation control
public class TouchableGLSurfaceView extends GLSurfaceView {
    private OurRenderer ourRenderer;
    private StaticShader shader;
    public static RotationSensor rotationSensor;
    private GameManager gameManager;

    boolean stopped = false;

    private long lastFrameTime;

    public static Loader loader;


    public TouchableGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(3);
        ourRenderer = new OurRenderer();
        rotationSensor = new RotationSensor(context);
        gameManager = new GameManager(MainActivity.assetManager);
        setRenderer(ourRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    // the implementation of the renderer interface
    private class OurRenderer implements GLSurfaceView.Renderer {
        private MasterRenderer renderer;

        RawModel model;
        Player player;
        List<Light> lights = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        List<Entity> normalEntities = new ArrayList<>();
        GUIText winText;
        GUIText lossText;

        Date startTime;

        ModelTexture texture;
        TexturedModel texturedModel;

        Camera camera = new Camera();

        public OurRenderer() {
            lastFrameTime = System.currentTimeMillis();
        }

        public void onDrawFrame(GL10 gl) {
            if(!stopped) {
                checkWin();
                long delta = System.currentTimeMillis() - lastFrameTime;
                float deltaTime = (float) delta / 1000;

                player.move(deltaTime, gameManager.getCurrentLevel());
                lastFrameTime = System.currentTimeMillis();


                float[] rotations = rotationSensor.getDeviceRotation();
                //entity.increaseRotation((float)Math.toDegrees(rotations[0]), (float)Math.toDegrees(rotations[1]), (float)Math.toDegrees(rotations[2]));
                for (Entity entity : normalEntities) {
                    //entity.setRotx(-rotations[1]);
                    //entity.setRoty(rotations[2]);
                    //entity.setRotz(-rotations[0]);
                }
                //camera.setPitch(rotations[1]);
                //camera.setYaw(-rotations[2]);
                renderer.renderScene(entities, normalEntities, lights, new Vector4f(0, -1, 0, 100000), camera);
            }
            if(stopped) {

                renderer.prepare();
                TextMaster.render();
            }
        }

        // resize of viewport
        // set projection matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        public void checkWin(){
            if(player.won){
                long difference =  Calendar.getInstance().getTime().getTime() - startTime.getTime();
                stopped = true;
                FontType font = new FontType(loader.loadTexture("arial"), "fonts/arial.fnt");
                winText = new GUIText("Gut gemacht! Du hast: " + ((difference / 100  % 600 ) / 10f) + " Sekunden gebraucht", 1, font, new Vector2f(0.3f,0.3f), .4f, true);
                winText.setColour(1,1,1);
            }
            if(player.lost){
                stopped = true;
                FontType font = new FontType(loader.loadTexture("arial"), "fonts/arial.fnt");
                lossText = new GUIText("Schade das war wohl nichts. ", 1, font, new Vector2f(0.3f,0.5f), .4f, true);
                lossText.setColour(1,1,1);

            }
        }

        public void restart(){
            TextMaster.removeText(winText);
            TextMaster.removeText(lossText);
            startTime = Calendar.getInstance().getTime();

        }

        // creation of viewport
        // initialization of some opengl features
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            startTime = Calendar.getInstance().getTime();


            renderer = new MasterRenderer(camera);
            loader = new Loader();
            TextMaster.init(loader);
            gameManager.loadLevel(1);
            lights.add(new Light(new Vector3f(0,50,50),new Vector3f(.5f,.5f,.5f)));


            TexturedModel playerModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("sphere", loader),
                    new ModelTexture(loader.loadTexture("white")));
            playerModel.getTexture().setNormalMap(loader.loadTexture("flat"));
            playerModel.getTexture().setShineDamper(10);
            playerModel.getTexture().setReflectivity(0.3f);

            normalEntities.addAll(gameManager.generateBoard());
            for(Entity e : normalEntities){
                if(e.getClass() == Tile.class && ((Tile)e).light != null){
                    lights.add(((Tile)e).light);
                }
            }

            player = new Player(gameManager.getPlayerLocation(),0,0,0,1,camera);

            normalEntities.add(player);

            shader = new StaticShader();
        }
    }
}


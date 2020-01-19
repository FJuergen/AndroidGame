package de.hs_kl.gatav.gles05colorcube.renderEngine;

import android.content.res.Resources;
import android.opengl.GLES30;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.shaders.StaticShader;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {


    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    public static final float RED = 0;
    public static final float GREEN = 0;
    public static final float BLUE = 0;

    private static Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer entityRenderer;

    //private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;


    private Map<TexturedModel,List<Entity>> entities = new HashMap<>();
    //private List<Terrain> terrains = new ArrayList<>();


    public MasterRenderer(){
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(shader,projectionMatrix);
        //terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);

    }

    public void render(List<Light> lights, Camera camera){
        prepare();
        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();

        /*terrainShader.start();
        terrainShader.loadLights(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        terrains.clear();
        */
        entities.clear();
    }

    /*public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

     */

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel,newBatch);
        }
    }

    public void cleanUp(){
        shader.cleanUp();
        //terrainShader.cleanUp();
    }

    public void prepare() {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }


    private void createProjectionMatrix(){
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        float aspectRatio = (float) width / (float) height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length) ;
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public static void enableCulling(){
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glEnable(GLES30.GL_BACK);
    }
    public static void disableCulling(){
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    }
}

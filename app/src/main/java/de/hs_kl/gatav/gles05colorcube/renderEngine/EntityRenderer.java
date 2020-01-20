package de.hs_kl.gatav.gles05colorcube.renderEngine;

import android.opengl.GLES30;

import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.shaders.StaticShader;
import de.hs_kl.gatav.gles05colorcube.toolbox.Maths;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;

import java.util.List;
import java.util.Map;

public class EntityRenderer {
    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        System.out.println(projectionMatrix);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    public void render(Map<TexturedModel,List<Entity>> entities){
        for(TexturedModel model:entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for(Entity entity: batch){
                prepareInstance(entity);
                GLES30.glDrawElements(GLES30.GL_TRIANGLES, model.getModel().getVertexCount(),
                        GLES30.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel model){
        GLES30.glBindVertexArray(model.getModel().getVaoID());
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glEnableVertexAttribArray(2);
        /*if(model.getTexture().isHasTransparency()){
            MasterRenderer.disableCulling();
        }

        shader.loadFakeLightingVariable(model.getTexture().isUseFakeLighting());

         */
        shader.loadShineVariables(model.getTexture().getShineDamper(), model.getTexture().getReflectivity());
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    private void unbindTexturedModel(){
        //MasterRenderer.enableCulling();
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotx(),
                entity.getRoty(), entity.getRotz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);

    }
}

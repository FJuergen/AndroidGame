package de.hs_kl.gatav.gles05colorcube.guis;

import android.opengl.GLES30;

import java.util.List;

import de.hs_kl.gatav.gles05colorcube.models.RawModel;
import de.hs_kl.gatav.gles05colorcube.renderEngine.Loader;
import de.hs_kl.gatav.gles05colorcube.toolbox.Maths;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;

public class GuiRenderer {
    private final RawModel quad;
    private GuiShader shader;

    public GuiRenderer(Loader loader){
        float[] positions = {-1,1,-1,-1,1,1,1,-1};
        quad = loader.loadToVAO(positions);
        shader = new GuiShader();
    }

    public void render(List<GuiTexture> textures){
        shader.start();
        GLES30.glBindVertexArray(quad.getVaoID());
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
        for(GuiTexture tex : textures){
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, tex.getTexture());
            Matrix4f matrix = Maths.createTransformationMatrix(tex.getPosition(),tex.getScale());
            shader.loadTransformation(matrix);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0,quad.getVertexCount());
        }

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }
}

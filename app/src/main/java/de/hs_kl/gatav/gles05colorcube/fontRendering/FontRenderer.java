package de.hs_kl.gatav.gles05colorcube.fontRendering;

import android.opengl.GLES30;

import java.util.List;
import java.util.Map;

import de.hs_kl.gatav.gles05colorcube.fonts.FontType;
import de.hs_kl.gatav.gles05colorcube.fonts.GUIText;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	public void render(Map<FontType, List<GUIText>> texts){
		prepare();
		for(FontType font : texts.keySet()){
			GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText text : texts.get(font)){
				renderText(text);
			}
		}
		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		GLES30.glEnable(GLES30.GL_BLEND);
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
		GLES30.glDisable(GLES30.GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text){
		GLES30.glBindVertexArray(text.getMesh());
		GLES30.glEnableVertexAttribArray(0);
		GLES30.glEnableVertexAttribArray(1);
		shader.loadColour(text.getColour());
		shader.loadTranslation(text.getPosition());
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, text.getVertexCount());
		GLES30.glDisableVertexAttribArray(0);
		GLES30.glDisableVertexAttribArray(1);
		GLES30.glBindVertexArray(0);

	}
	
	private void endRendering(){
		shader.stop();
		GLES30.glDisable(GLES30.GL_BLEND);
		GLES30.glEnable(GLES30.GL_DEPTH_TEST);

	}

}

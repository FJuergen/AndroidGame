package de.hs_kl.gatav.gles05colorcube.normalMappingRenderer;

import android.opengl.GLES30;

import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL11;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.models.RawModel;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.renderEngine.MasterRenderer;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.toolbox.Maths;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector4f;


public class NormalMappingRenderer {

	private NormalMappingShader shader;

	public NormalMappingRenderer(Matrix4f projectionMatrix) {
		this.shader = new NormalMappingShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities, Vector4f clipPlane, List<Light> lights, Camera camera, Matrix4f toShadowSpace) {
		shader.start();
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		prepare(clipPlane, lights, camera);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GLES30.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVertexCount(), GLES30.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getModel();
		GLES30.glBindVertexArray(rawModel.getVaoID());
		GLES30.glEnableVertexAttribArray(0);
		GLES30.glEnableVertexAttribArray(1);
		GLES30.glEnableVertexAttribArray(2);
		GLES30.glEnableVertexAttribArray(3);
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		/*if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		 */
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, model.getTexture().getTextureID());
		GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, model.getTexture().getNormalMap());
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GLES30.glDisableVertexAttribArray(0);
		GLES30.glDisableVertexAttribArray(1);
		GLES30.glDisableVertexAttribArray(2);
		GLES30.glDisableVertexAttribArray(3);
		GLES30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotx(),
				entity.getRoty(), entity.getRotz(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, Camera camera) {
		shader.loadClipPlane(clipPlane);
		//need to be public variables in MasterRenderer
		shader.loadSkyColour(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}

}

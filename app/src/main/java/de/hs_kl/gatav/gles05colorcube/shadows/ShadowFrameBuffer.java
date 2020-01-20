package de.hs_kl.gatav.gles05colorcube.shadows;

import android.content.res.Resources;
import android.opengl.GLES32;

import java.nio.ByteBuffer;

import de.hs_kl.gatav.gles05colorcube.MainActivity;


/**
 * The frame buffer for the shadow pass. This class sets up the depth texture
 * which can be rendered to during the shadow render pass, producing a shadow
 * map.
 * 
 * @author Karl
 *
 */
public class ShadowFrameBuffer {

	private final int WIDTH;
	private final int HEIGHT;
	private int fbo;
	private int shadowMap;

	/**
	 * Initialises the frame buffer and shadow map of a certain size.
	 * 
	 * @param width
	 *            - the width of the shadow map in pixels.
	 * @param height
	 *            - the height of the shadow map in pixels.
	 */
	protected ShadowFrameBuffer(int width, int height) {
		this.WIDTH = width;
		this.HEIGHT = height;
		initialiseFrameBuffer();
	}

	/**
	 * Deletes the frame buffer and shadow map texture when the game closes.
	 */
	protected void cleanUp() {
		//GLES32.glDeleteFramebuffers(fbo);
		//GLES32.glDeleteTextures(shadowMap);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target.
	 */
	protected void bindFrameBuffer() {
		bindFrameBuffer(fbo, WIDTH, HEIGHT);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target.
	 */
	protected void unbindFrameBuffer() {
		GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
		GLES32.glViewport(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
	}

	/**
	 * @return The ID of the shadow map texture.
	 */
	protected int getShadowMap() {
		return shadowMap;
	}

	/**
	 * Creates the frame buffer and adds its depth attachment texture.
	 */
	private void initialiseFrameBuffer() {
		fbo = createFrameBuffer();
		shadowMap = createDepthBufferAttachment(WIDTH, HEIGHT);
		unbindFrameBuffer();
	}

	/**
	 * Binds the frame buffer as the current render target.
	 * 
	 * @param frameBuffer
	 *            - the frame buffer.
	 * @param width
	 *            - the width of the frame buffer.
	 * @param height
	 *            - the height of the frame buffer.
	 */
	private static void bindFrameBuffer(int frameBuffer, int width, int height) {
		GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
		GLES32.glBindFramebuffer(GLES32.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GLES32.glViewport(0, 0, width, height);
	}

	/**
	 * Creates a frame buffer and binds it so that attachments can be added to
	 * it. The draw buffer is set to none, indicating that there's no colour
	 * buffer to be rendered to.
	 * 
	 * @return The newly created frame buffer's ID.
	 */
	private static int createFrameBuffer() {
		int[] buf = {GLES32.GL_NONE};
		int[] frameBuffer = new int[1];
		GLES32.glGenFramebuffers(1,frameBuffer,0);
		GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, frameBuffer[0]);
		GLES32.glDrawBuffers(1,buf,0);
		GLES32.glReadBuffer(GLES32.GL_NONE);
		return frameBuffer[0];
	}

	/**
	 * Creates a depth buffer texture attachment.
	 * 
	 * @param width
	 *            - the width of the texture.
	 * @param height
	 *            - the height of the texture.
	 * @return The ID of the depth texture.
	 */
	private static int createDepthBufferAttachment(int width, int height) {
		int[] texture = new int[1];
		GLES32.glGenTextures(1,texture,0);
		GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture[0]);
		GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_DEPTH_COMPONENT16, width, height, 0,
				GLES32.GL_DEPTH_COMPONENT, GLES32.GL_FLOAT, null);
		GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
		GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
		GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);
		GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);
		GLES32.glFramebufferTexture(GLES32.GL_FRAMEBUFFER, GLES32.GL_DEPTH_ATTACHMENT, texture[0], 0);
		return texture[0];
	}
}

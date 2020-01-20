package de.hs_kl.gatav.gles05colorcube.shadows;


import de.hs_kl.gatav.gles05colorcube.shaders.ShaderProgram;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "shaders/shadowVertexShader.glsl";
	private static final String FRAGMENT_FILE = "shaders/shadowFragmentShader.glsl";
	
	private int location_mvpMatrix;

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

}

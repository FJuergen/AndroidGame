package de.hs_kl.gatav.gles05colorcube.shaders;

public class StaticShader extends ShaderProgram {
    private static final String VERTEX_FILE = "vertexShader.glsl";
    private static final String FRAGMENT_FILE = "fragmentShader.glsl";
    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0,"position");
    }
}

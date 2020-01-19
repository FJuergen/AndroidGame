package de.hs_kl.gatav.gles05colorcube.models;

import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;

public class TexturedModel  {
    private RawModel model;
    private ModelTexture texture;


    public TexturedModel(RawModel model, ModelTexture texture) {
        this.model = model;
        this.texture = texture;
    }

    public RawModel getModel() {
        return model;
    }

    public int getNumberOfRows(){
        return 1;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}

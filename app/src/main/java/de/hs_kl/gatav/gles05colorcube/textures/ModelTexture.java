package de.hs_kl.gatav.gles05colorcube.textures;

public class ModelTexture {
    private int textureID;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private int numberOfRows = 1;


    public int getNumberOfRows() {
        return numberOfRows;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public ModelTexture(int id){
        textureID = id;
    }

    public int getTextureID() {
        return textureID;
    }
}

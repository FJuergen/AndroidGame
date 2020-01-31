package de.hs_kl.gatav.gles05colorcube.gameLogic;

import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

public class Tile extends Entity {

    public Light light = null;

    public Tile(TexturedModel model, Vector3f position, float rotx, float roty, float rotz, float scale, Light light) {
        super(model, position, rotx, roty, rotz, scale);
        this.light = light;
    }
    public Tile(TexturedModel model, Vector3f position, float rotx, float roty, float rotz, float scale) {
        super(model, position, rotx, roty, rotz, scale);
    }
}

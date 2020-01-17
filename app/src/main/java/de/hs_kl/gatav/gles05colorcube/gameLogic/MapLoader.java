package de.hs_kl.gatav.gles05colorcube.gameLogic;
import android.graphics.BitmapFactory;
import java.io.InputStream;

public class MapLoader {
    public Map load(InputStream stream) {
        return new Map(BitmapFactory.decodeStream(stream));
    }
}

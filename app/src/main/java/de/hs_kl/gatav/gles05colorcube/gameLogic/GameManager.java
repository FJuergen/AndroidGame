package de.hs_kl.gatav.gles05colorcube.gameLogic;
import java.io.IOException;
import android.content.res.AssetManager;

public class GameManager {
    Map currentMap;
    AssetManager assetManager;
    Player player;

    public GameManager(AssetManager assManager) {
        assetManager = assManager;
    }

    public void onDrawFrame() {
        player.moveByRotation();
    }

    public void loadLevel(int level) {
        MapLoader mapLoader = new MapLoader();
        try {
            currentMap = mapLoader.load(assetManager.open("maps/map" + level + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new Player(currentMap.getEmptyCoordinates());
    }

    public Map getCurrentLevel() {
        return currentMap;
    };
}

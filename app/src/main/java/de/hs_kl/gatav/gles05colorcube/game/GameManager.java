package de.hs_kl.gatav.gles05colorcube.game;
import java.io.IOException;
import android.content.res.AssetManager;

public class GameManager {
    Map currentMap;
    AssetManager assetManager;
    Player player;

    public GameManager(AssetManager assManager) {
        assetManager = assManager;
    }

    public void update(float deltaTime) {
        player.moveByRotation(deltaTime, currentMap);
    }

    public void loadLevel(int level) {
        MapLoader mapLoader = new MapLoader();
        try {
            currentMap = mapLoader.load(assetManager.open("maps/map" + level + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new Player(currentMap.getEmptyVectorCoordinates());
    }

    public Map getCurrentLevel() {
        return currentMap;
    };
}

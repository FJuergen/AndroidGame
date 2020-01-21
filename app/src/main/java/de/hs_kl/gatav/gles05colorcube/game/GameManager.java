package de.hs_kl.gatav.gles05colorcube.game;
import java.io.IOException;
import android.content.res.AssetManager;

public class GameManager {
    Map currentMap;
    AssetManager assetManager;
    MapLoader mapLoader;
    Player player;

    int currentLevel = 1;

    public GameManager(AssetManager assManager) {
        assetManager = assManager;
        mapLoader = new MapLoader();
    }

    public void update(float deltaTime) {
        if (player.isDead()) {
            // TODO: display death
            loadLevel(1);
        }
        if (player.isDone()) {
            loadLevel(++currentLevel);
        }
        player.moveByRotation(deltaTime, currentMap);
    }

    public void loadLevel(int level) {
        currentLevel = level;
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

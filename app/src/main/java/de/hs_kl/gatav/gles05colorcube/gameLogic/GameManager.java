package de.hs_kl.gatav.gles05colorcube.gameLogic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;

import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.normalMappingObjConverter.NormalMappedObjLoader;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

import static de.hs_kl.gatav.gles05colorcube.TouchableGLSurfaceView.loader;

public class GameManager {

    private final float SCALE = .5f;

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

    public List<Entity> generateBoard(){
        List<Entity> retList = new ArrayList<>();


        TexturedModel brickModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("brick", loader),
                new ModelTexture(loader.loadTexture("pavement")));
        brickModel.getTexture().setNormalMap(loader.loadTexture("pavement_normal"));
        brickModel.getTexture().setShineDamper(10);
        brickModel.getTexture().setReflectivity(0.5f);
        TexturedModel brickModel2 = new TexturedModel(NormalMappedObjLoader.loadOBJ("brick", loader),
                new ModelTexture(loader.loadTexture("brick_wall")));
        brickModel2.getTexture().setNormalMap(loader.loadTexture("brick_wall_normal"));
        brickModel2.getTexture().setShineDamper(10);
        brickModel2.getTexture().setReflectivity(0.5f);

        TexturedModel goal = new TexturedModel(NormalMappedObjLoader.loadOBJ("brick", loader),
                new ModelTexture(loader.loadTexture("purple")));
        goal.getTexture().setNormalMap(loader.loadTexture("flat"));
        goal.getTexture().setShineDamper(10);
        goal.getTexture().setReflectivity(0.5f);

        TexturedModel death = new TexturedModel(NormalMappedObjLoader.loadOBJ("brick", loader),
                new ModelTexture(loader.loadTexture("white")));
        death.getTexture().setNormalMap(loader.loadTexture("flat"));
        death.getTexture().setShineDamper(10);
        death.getTexture().setReflectivity(0.5f);


        float offsetX = -currentMap.getWidth()/2;
        float offsetY = -currentMap.getHeight()/2;
        for(int i = 0; i< currentMap.getWidth(); i++){
            for(int j = 0; j < currentMap.getHeight(); j++){
                switch(currentMap.getObjectAt(i,j)){
                    case EMPTY:
                        retList.add(new Tile(brickModel, new Vector3f(i + offsetX, j + offsetY, -20f ), 0,0,0,SCALE));
                        break;
                    case WALL:
                        retList.add(new Tile(brickModel2, new Vector3f(i + offsetX, j + offsetY, -19f), 0,0,0,SCALE));
                        break;
                    case GOAL:
                        retList.add(new Tile(goal, new Vector3f(i + offsetX, j + offsetY, -20f), 0,0,0,SCALE));
                        break;
                    case DEATH:
                        retList.add(new Tile(death, new Vector3f(i + offsetX, j + offsetY, -20f), 0,0,0,SCALE));
                        break;
                }
            }
        }
        return retList;
    }
}

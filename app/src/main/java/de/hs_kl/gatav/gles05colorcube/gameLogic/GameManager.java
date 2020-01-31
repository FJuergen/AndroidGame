package de.hs_kl.gatav.gles05colorcube.gameLogic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;

import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.entities.Light;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.normalMappingObjConverter.NormalMappedObjLoader;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.vector.Vector2f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

import static de.hs_kl.gatav.gles05colorcube.TouchableGLSurfaceView.loader;


public class GameManager {

    private float SCALE;


    public Vector3f getPlayerLocation() {
        return playerLocation;
    }

    Vector3f playerLocation;

    Map currentMap;
    AssetManager assetManager;



    public GameManager(AssetManager assManager) {
        assetManager = assManager;
    }


    public void loadLevel(int level) {
        MapLoader mapLoader = new MapLoader();
        try {
            currentMap = mapLoader.load(assetManager.open("maps/map" + level + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SCALE = currentMap.tileSize;
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
        Random random = new Random();
        boolean foundPlayerLocation = false;
        for(int i = 0; i< currentMap.getWidth(); i++){
            for(int j = 0; j < currentMap.getHeight(); j++){
                switch(currentMap.getObjectAt(i,j)){
                    case EMPTY:
                        if(!foundPlayerLocation && random.nextFloat() < 0.01 && i > 1){
                            playerLocation = new Vector3f((i + offsetX)* SCALE *  2 , (j + offsetY) * SCALE * 2, -20f + SCALE * 2 );
                            foundPlayerLocation = true;
                        }
                        retList.add(new Tile(brickModel, new Vector3f((i + offsetX)* SCALE *  2 , (j + offsetY) * SCALE * 2, -20f ), 0,0,0,SCALE));
                        break;
                    case WALL:
                        retList.add(new Tile(brickModel2, new Vector3f((i + offsetX)* SCALE * 2, (j + offsetY) * SCALE* 2, -20f + SCALE * 2), 0,0,0,SCALE));
                        break;
                    case GOAL:
                        Vector3f winPosition = new Vector3f((i + offsetX)* SCALE * 2, (j + offsetY) * SCALE* 2, -20f);
                        Light winLight = new Light(Vector3f.add(winPosition,new Vector3f(0,1,SCALE * 2),null),new Vector3f(1,0,1),new Vector3f(1,0.05f,0.005f));
                        retList.add(new Tile(goal, winPosition, 0,0,0,SCALE,winLight));
                        break;
                    case DEATH:
                        Vector3f lossPosition = new Vector3f((i + offsetX)* SCALE * 2, (j + offsetY) * SCALE*  2, -20f);
                        Light lossLight = new Light(Vector3f.add(lossPosition,new Vector3f(0,1,SCALE * 2),null),new Vector3f(1,1,1),new Vector3f(1,0.05f,0.005f));
                        retList.add(new Tile(death, lossPosition, 0,0,0,SCALE,lossLight));
                        break;
                }
            }
        }
        return retList;
    }

}

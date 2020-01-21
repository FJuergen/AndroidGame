package de.hs_kl.gatav.gles05colorcube.gameLogic;
import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.Random;

import de.hs_kl.gatav.gles05colorcube.vector.Vector2f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

public class Map {
    public enum MapObjectType {
        EMPTY,
        DEATH,
        WALL,
        GOAL
    }
    private MapObjectType[][] mapMap;
    private int height;
    private int width;
    public float tileSize = 1.0f;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Map(Bitmap map) {
        height = map.getHeight();
        width = map.getWidth();
        mapMap = new MapObjectType[height][width];

        for (int y = 0; y < mapMap.length; y++) {
            MapObjectType[] row = mapMap[y];

            for (int x = 0; x <  row.length; x++) {
                Color col = map.getColor(x, y);

                if (col.red() > 0.9) {
                    row[x] = MapObjectType.GOAL;
                }
                else if (col.green() > 0.9) {
                    row[x] = MapObjectType.WALL;
                }
                else if (col.blue() > 0.9) {
                    row[x] = MapObjectType.DEATH;
                } else {
                    row[x] = MapObjectType.EMPTY;
                }
            }
        }
    }

    public MapObjectType getObjectAt(int x, int y) {
        return mapMap[y][x];
    }

    public MapObjectType getObjectAtVector(Vector3f target) {
        int y = (int)Math.floor(target.y / tileSize);
        int x = (int)Math.floor(target.x / tileSize);
        return getObjectAt(x, y);
    }

    public Vector3f getEmptyVectorCoordinates() {
        Random random = new Random();
        int randX = random.nextInt(width);
        int randY = random.nextInt(height);
        MapObjectType e = getObjectAt(randX, randY);

        if (e == MapObjectType.EMPTY) {
            return new Vector3f(
                    randX * tileSize - 0.5f * tileSize,
                    randY * tileSize - 0.5f * tileSize,
                    0
            );
        } else {
            return getEmptyVectorCoordinates();
        }
    }

    public Vector2f toMapSpace(Vector3f position){
        float offsetX = -this.getWidth() / 2;
        float offsetY = -this.getHeight() / 2;

        return new Vector2f(((position.x)/(tileSize *2))- offsetX,((position.y )/(tileSize * 2))- offsetY);

    }

}

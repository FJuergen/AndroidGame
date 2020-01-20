package de.hs_kl.gatav.gles05colorcube.gameLogic;
import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.Random;

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

    public Vector3f getEmptyCoordinates() {
        Random random = new Random();
        int randX = random.nextInt(width);
        int randY = random.nextInt(height);
        MapObjectType e = mapMap[randY][randX];

        if (e == MapObjectType.EMPTY) {
            return new Vector3f(randX, randY, 0);
        } else {
            return getEmptyCoordinates();
        }
    }
}

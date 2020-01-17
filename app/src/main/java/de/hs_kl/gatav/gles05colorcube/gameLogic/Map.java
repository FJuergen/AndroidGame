package de.hs_kl.gatav.gles05colorcube.gameLogic;
import android.graphics.Bitmap;
import android.graphics.Color;

public class Map {
    private enum MapObjectType {
        EMPTY,
        START,
        DEATH,
        WALL,
        GOAL
    }
    private MapObjectType[][] mapMap;

    public Map(Bitmap map) {
        int height = map.getHeight();
        int width = map.getWidth();
        mapMap = new MapObjectType[height][width];

        for (int y = 0; y < mapMap.length; y++) {
            MapObjectType[] row = mapMap[y];

            for (int x = 0; x <  row.length; x++) {
                MapObjectType e = row[x];
                Color col = map.getColor(x, y);

                if (col.red() > 0.9 && col.green() > 0.9 && col.blue() > 0.9) {
                    e = MapObjectType.GOAL;
                }
                else if (col.red() > 0.9) {
                    e = MapObjectType.START;
                }
                else if (col.green() > 0.9) {
                    e = MapObjectType.WALL;
                }
                else if (col.blue() > 0.9) {
                    e = MapObjectType.DEATH;
                } else {
                    e = MapObjectType.EMPTY;
                }
            }
        }
    }

    public MapObjectType getObjectAt(int x, int y) {
        return mapMap[y][x];
    }
}

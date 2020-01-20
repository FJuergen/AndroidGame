package de.hs_kl.gatav.gles05colorcube.gameLogic;

import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;
import de.hs_kl.gatav.gles05colorcube.TouchableGLSurfaceView;

public class Player {
    private Vector3f pos;

    public Player(Vector3f startCoordinates) {
        setPosition(startCoordinates);
        System.out.println("player start position: " + pos);
    }

    public Vector3f move(Vector3f target, Map map) {
        if (isCollidingWith(target, map) == Map.MapObjectType.WALL) {
            return pos;
        }
        setPosition(target);
        return pos;
    }

    public void moveByRotation() {
        float[] rotations = TouchableGLSurfaceView.rotationSensor.getDeviceRotation();
        for (float rot : rotations) {
            System.out.println(rot);
        }
    }

    public void setPosition(Vector3f coordinates) {
        pos = coordinates;
    }

    public Map.MapObjectType isCollidingWith(Vector3f target, Map map) {
        int x = Math.round(target.x);
        int y = Math.round(target.y);

        return map.getObjectAt(x, y);
    }
}

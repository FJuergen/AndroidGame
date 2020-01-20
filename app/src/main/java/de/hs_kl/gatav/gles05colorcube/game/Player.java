package de.hs_kl.gatav.gles05colorcube.game;

import android.opengl.Matrix;

import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;
import de.hs_kl.gatav.gles05colorcube.TouchableGLSurfaceView;

public class Player {
    private float[] velocity = new float[3];
    private float[] counterVelocity = {1.0f, 1.0f, 1.0f};
    private float counterVelocityDuration = 1000;
    private float lastCollisionTime;
    private float[] transformationMatrix;
    private float speed = 5.0f;
    private float size = 1.0f;


    public Player(Vector3f startCoordinates) {
        transformationMatrix = new float[16];
        lastCollisionTime = System.currentTimeMillis();
        Matrix.setIdentityM(transformationMatrix, 0);
        setX(startCoordinates.x);
        setY(startCoordinates.y);
        setZ(startCoordinates.z);
    }

    // 0 == pi -> (x + pi % pi)
    //
    // 0,0,0        is flat
    // 0, -x, 0     is tilted towards user
    // 0, x, 0      is tilted back
    // 0, 0, -x     is tilted left
    // 0, 0, x      is tilted right
    // 0.5 * pi     is each extreme
    public void moveByRotation(float deltaTime, Map map) {
        float[] rotation  = TouchableGLSurfaceView.rotationSensor.getDeviceRotation();
        velocity[0] = rotation[2]; // x
        velocity[1] = rotation[1]; // y
        transformationMatrix = processCollision(deltaTime, map);
    }

    protected float[] processCollision(float deltaTime, Map map) {
        float[] tempMatrix = new float[16];
        System.arraycopy(transformationMatrix, 0, tempMatrix, 0, 16);

        Matrix.translateM(tempMatrix, 0,
                deltaTime * velocity[0] * counterVelocity[0] * speed,
                deltaTime * velocity[1] * counterVelocity[1] * speed,
                deltaTime * velocity[2] * counterVelocity[2] * speed);
        Vector3f target = new Vector3f(tempMatrix[12], tempMatrix[13], tempMatrix[14]);
        Map.MapObjectType collision = map.getObjectAtVector(target);

        if (collision == Map.MapObjectType.EMPTY) {
            resetCounterVelocity();
            return tempMatrix;
        } else if (collision == Map.MapObjectType.WALL) {
            lastCollisionTime = System.currentTimeMillis();
            counterVelocity = getCounterVelocity(target, map.tileSize);
            return transformationMatrix;
        }

        // Death/Goal Handling
        else {
            return tempMatrix;
        }
    }

    protected float[] getCounterVelocity(Vector3f target, float objSize) {
        int y = (int)Math.floor(target.y);
        int x = (int)Math.floor(target.x);

        float playerB = target.y + size;
        float playerR = target.x + size;
        float objB = y + objSize;
        float objR = x + objSize;

        float collisionB = objB - target.y;
        float collisionR = objR - target.x;
        float collisionT = playerB - target.y;
        float collisionL = playerR - target.x;

        // top collision
        if (collisionT < collisionB && collisionT < collisionL && collisionT < collisionR) {
            float[] counterVelocity = {velocity[0], -velocity[1], 0f};
            return counterVelocity;
        }
        // bot collision
        else if (collisionB < collisionT && collisionB < collisionL && collisionB < collisionR) {
            float[] counterVelocity = {velocity[0], -velocity[1], 0f};
            return counterVelocity;
        }
        // left collision
        else if (collisionL < collisionR && collisionL < collisionT && collisionL < collisionB) {
            float[] counterVelocity = {-velocity[0], velocity[1], 0f};
            return counterVelocity;
        }
        // right collision
        else if (collisionR < collisionL && collisionR < collisionT && collisionR < collisionB) {
            float[] counterVelocity = {-velocity[0], velocity[1], 0f};
            return counterVelocity;
        }
        else {
            float[] counterVelocity = {1.0f, 1.0f, 1.0f};
            return counterVelocity;
        }
    }

    protected void resetCounterVelocity() {
        if (System.currentTimeMillis() - lastCollisionTime > counterVelocityDuration) {
            float[] resetVelocity = {1.0f, 1.0f, 1.0f};
            counterVelocity = resetVelocity;
        }
    }

    public void setPosition(Vector3f pos) {
        Matrix.setIdentityM(transformationMatrix, 0);
        Matrix.translateM(transformationMatrix, 0, pos.x, pos.y, pos.z);
    }

    /*
     * An OpenGL transformation matrix has the following format:
     * Values:    Indices:
     *   v v v x    0  4  8 12
     *   v v v y    1  5  9 13
     *   v v v z    2  6 10 14
     *   v v v v    3  7 11 15 * While the values marked with v are based on all the transformation that
     * were done at the matrix, the values marked with x, y and z contain the
     * coordinates. With that in mind we can provide the following convenience
     * functions to provide easy access to those values */
    public float getX() {
        return transformationMatrix[12];
    }

    public float getY() {
        return transformationMatrix[13];
    }

    public float getZ() {
        return transformationMatrix[14];
    }

    public void setX(float x) {
        transformationMatrix[12] = x;
    }

    public void setY(float y) {
        transformationMatrix[13] = y;
    }

    public void setZ(float z) {
        transformationMatrix[14] = z;
    }
}

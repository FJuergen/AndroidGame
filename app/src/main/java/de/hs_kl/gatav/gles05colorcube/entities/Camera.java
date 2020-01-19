package de.hs_kl.gatav.gles05colorcube.entities;

import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f(0,0,0);

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    private float pitch;
    private float yaw;
    private float roll;

    public void move(){
    }


    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

}

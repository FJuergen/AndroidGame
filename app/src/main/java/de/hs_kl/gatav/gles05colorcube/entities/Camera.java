package de.hs_kl.gatav.gles05colorcube.entities;

import de.hs_kl.gatav.gles05colorcube.R;
import de.hs_kl.gatav.gles05colorcube.objConverter.Vector3f;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class Camera {

    private Vector3f position = new Vector3f(0,0,0);
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

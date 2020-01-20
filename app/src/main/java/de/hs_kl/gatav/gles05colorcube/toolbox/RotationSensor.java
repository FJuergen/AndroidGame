package de.hs_kl.gatav.gles05colorcube.toolbox;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;

public class RotationSensor extends Activity implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor rotation;

    static float[] rotations = {0,0,0,0};
    static private float[] rotMatrix = new float[16];
    static private float[] rot = new float[3];

    private final float epsilon = 0.0f;




    public RotationSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotMatrix,event.values);
        }
    }
    /**
     *
     * @return float array with Azimuth Pitch Roll in Radians
     */
    public float[] getDeviceRotation(){
        //SensorManager.getAngleChange(rot,rotMatrix,prevRotMatrix);
        //prevRotMatrix = rotMatrix;
        SensorManager.getOrientation(rotMatrix,rot);
        return rot;
    }

}

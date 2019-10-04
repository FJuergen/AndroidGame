package de.hs_kl.gatav.gles05colorcube.toolbox;

import android.renderscript.*;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.objConverter.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){

        Matrix4f matrix = new Matrix4f();
        matrix.loadIdentity();
        matrix.translate(translation.x,translation.y,translation.z);
        matrix.rotate((rx),1,0,0);
        matrix.rotate((ry),0,1,0);
        matrix.rotate((rz),0,0,1);
        matrix.scale(scale,scale,scale);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.loadIdentity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), 1,0,0);
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()),0,1,0);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        viewMatrix.translate(negativeCameraPos.x, negativeCameraPos.y, negativeCameraPos.z);
        return viewMatrix;
    }
}

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
    public static Matrix4f createTransformationMatrix(Vector3f translation,  float rx, float ry, float rz,float rotv, float scale){

        Matrix4f rotation = new Matrix4f();

        double sqw = rotv*rotv;
        double sqx = rx*rx;
        double sqy = ry*ry;
        double sqz = rz*rz;

        // invs (inverse square length) is only required if quaternion is not already normalised
        double invs = 1 / (sqx + sqy + sqz + sqw);
        rotation.set(0,0,(float) ((sqx - sqy - sqz + sqw)*invs)); // since sqw + sqx + sqy + sqz =1/invs*invs
        rotation.set(1,1,(float) ((-sqx + sqy - sqz + sqw)*invs)) ;
        rotation.set(2,2,(float)((-sqx - sqy + sqz + sqw)*invs ));

        double tmp1 = rx*ry;
        double tmp2 = rz*rotv;
        rotation.set(1,0,(float)( 2.0 * (tmp1 + tmp2)*invs)) ;
        rotation.set(0,1,(float)( 2.0 * (tmp1 - tmp2)*invs ));

        tmp1 = rx*rz;
        tmp2 = ry*rotv;
        rotation.set(2,0,(float)(2.0 * (tmp1 - tmp2)*invs)) ;
        rotation.set(0,2,(float)(2.0 * (tmp1 + tmp2)*invs)) ;
        tmp1 = ry*rz;
        tmp2 = rx*rotv;
        rotation.set(2,1,(float)( 2.0 * (tmp1 + tmp2)*invs));
        rotation.set(1,2,(float)(2.0 * (tmp1 - tmp2)*invs)) ;

        Matrix4f matrix = new Matrix4f();
        matrix.loadIdentity();
        matrix.translate(translation.x,translation.y,translation.z);
        matrix.multiply(rotation);
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

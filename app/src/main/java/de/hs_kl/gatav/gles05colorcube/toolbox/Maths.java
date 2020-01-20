package de.hs_kl.gatav.gles05colorcube.toolbox;


import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.vector.Matrix4f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector2f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){

        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        matrix.translate(translation);
        matrix.rotate((rx),new Vector3f(1f,0f,0f));
        matrix.rotate((ry),new Vector3f(0f,1f,0f));
        matrix.rotate((rz),new Vector3f(0f,0f,1f));
        matrix.scale(new Vector3f(scale,scale,scale));
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        viewMatrix.rotate(camera.getPitch(), new Vector3f(1f,0f,0f));
        viewMatrix.rotate(camera.getYaw(),new Vector3f(0f,1f,0f));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        viewMatrix.translate(new Vector3f(negativeCameraPos.x, negativeCameraPos.y, negativeCameraPos.z));
        return viewMatrix;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }
}

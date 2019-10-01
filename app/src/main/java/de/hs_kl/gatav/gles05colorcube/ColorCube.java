package de.hs_kl.gatav.gles05colorcube;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;


public class ColorCube {

    private final int MODE = GL10.GL_TRIANGLES;

    private FloatBuffer topColorBuffer;

    private FloatBuffer vertexBuffer;
    private ShortBuffer frontTopologyBuffer;
    private ShortBuffer topTopologyBuffer;
    private ShortBuffer bottomTopologyBuffer;
    private ShortBuffer leftTopologyBuffer;
    private ShortBuffer rightTopologyBuffer;
    private ShortBuffer backTopologyBuffer;
    private ShortBuffer topologyBuffer;


    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    public ColorCube() {

        float[] verteces = {
                -0.5f, -0.5f, 0.5f, //v0
                0.5f, -0.5f, 0.5f, //v1
                0.5f, 0.5f, 0.5f, //v2
                -0.5f, 0.5f, 0.5f, //v3

                -0.5f, -0.5f, -0.5f, //v4
                0.5f, -0.5f, -0.5f, //v5
                0.5f, 0.5f, -0.5f, //v6
                -0.5f, 0.5f, -0.5f  //v7
        };
        ByteBuffer vertecesBB = ByteBuffer.allocateDirect(verteces.length * 4);
        vertecesBB.order(ByteOrder.nativeOrder());
        vertexBuffer = vertecesBB.asFloatBuffer();
        vertexBuffer.put(verteces);
        vertexBuffer.position(0);
        vertexBuffer.flip();

        short[] indices = {
                0, 1, 2,
                2, 3, 0,
                3, 2, 6,
                6, 7, 3,
                4, 5, 1,
                1, 0, 4,
                4, 0, 3,
                3, 7, 4,
                1, 5, 6,
                6, 2, 1,
                7, 6, 5,
                5, 4, 7,

        };
        ByteBuffer indicesBB = ByteBuffer.allocateDirect(indices.length * 2);
        indicesBB.order(ByteOrder.nativeOrder());
        topologyBuffer = indicesBB.asShortBuffer();
        topologyBuffer.put(indices);
        topologyBuffer.position(0);


        float[] colors = {
                0.0f, 0.0f, 1.0f, 0.0f,        // v4
                1.0f, 0.0f, 0.0f, 0.0f,        // v0
                0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 0.0f
        };	   // v1
        ByteBuffer topColorBB = ByteBuffer.allocateDirect(colors.length * 4);
        topColorBB.order(ByteOrder.nativeOrder());
        topColorBuffer = topColorBB.asFloatBuffer();
        topColorBuffer.put(colors);
        topColorBuffer.position(0);

    }

    public void draw(float[] mvpMatrix) {
//TRANSLATION
        float[] transMatrix = new float[16];

        Matrix.setIdentityM(transMatrix,0);
        Matrix.translateM(transMatrix,0,5.0f,0,0);
        Matrix.multiplyMM(transMatrix,0,mvpMatrix,0,transMatrix,0);



        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, transMatrix, 0);

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3,
                GLES20.GL_UNSIGNED_SHORT, topologyBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

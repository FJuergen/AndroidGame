package de.hs_kl.gatav.gles05colorcube;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader  {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();

    public RawModel loadToVAO(float[] positions){
        int vaoID = createVAO();
        storeDataInAttributeList(0,positions);
        unbindVAO();
        return new RawModel(vaoID,positions.length/3);
    }

    private int createVAO(){
        int[] vaoID = new int[1];
        vaos.add(vaoID[0]);
        GLES30.glGenVertexArrays(1,vaoID,0);
        GLES30.glBindVertexArray(vaoID[0]);
        return vaoID[0];
    }

    public void cleanUp(){
        for(int vao:vaos){
            IntBuffer buffer = IntBuffer.allocate(1);
            buffer.put(vao);
            buffer.flip();
            GLES30.glDeleteVertexArrays(1, buffer);
        }
        for(int vbo:vbos){
            IntBuffer buffer = IntBuffer.allocate(1);
            buffer.put(vbo);
            buffer.flip();
            GLES30.glDeleteBuffers(1, buffer);
        }

    }

    private void storeDataInAttributeList(int attributeNumber, float[] data){
        int[] vboID = new int[1];
        vbos.add(vboID[0]);
        GLES30.glGenBuffers(1, vboID, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboID[0]);
        System.out.println(vboID[0]);
        FloatBuffer buffer = storeDataInFLoatBuffer(data);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,data.length * 4, buffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(attributeNumber, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO(){
        GLES30.glBindVertexArray(0);
    }

    private FloatBuffer storeDataInFLoatBuffer(float[] data){
        FloatBuffer buffer = FloatBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}

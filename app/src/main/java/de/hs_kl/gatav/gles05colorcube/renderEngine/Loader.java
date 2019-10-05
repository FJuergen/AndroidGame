package de.hs_kl.gatav.gles05colorcube.renderEngine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import de.hs_kl.gatav.gles05colorcube.MainActivity;
import de.hs_kl.gatav.gles05colorcube.models.RawModel;
import de.hs_kl.gatav.gles05colorcube.objConverter.ModelData;

public class Loader  {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, int[] indices,float[] normals, float[] textureCoords){
        int vaoID = createVAO();
        bindIndicesToBuffer(indices);
        storeDataInAttributeList(0,3 ,positions);
        storeDataInAttributeList(1,2 ,textureCoords);
        storeDataInAttributeList(2,3,normals);
        unbindVAO();
        return new RawModel(vaoID,indices.length);
    }


    private void bindIndicesToBuffer(int[] indices){
        int[] vboID = new int[1];
        GLES30.glGenBuffers(1,vboID,0);
        vbos.add(vboID[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,vboID[0]);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,indices.length*4, buffer, GLES30.GL_STATIC_DRAW);

    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = IntBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
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
        for(int texture:textures){
            IntBuffer buffer = IntBuffer.allocate(1);
            buffer.put(texture);
            buffer.flip();
            GLES30.glDeleteTextures(1, buffer);
        }

    }

    public int loadTexture(String fileName){

        final int[] textureID = new int[1];
        GLES30.glGenTextures(1,textureID,0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(MainActivity.assetManager.open("textures/" + fileName),null,options);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0]);

            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,bitmap,0);
            bitmap.recycle();

        } catch (IOException e) {
            e.printStackTrace();
        }
        textures.add(textureID[0]);
        return textureID[0];
    }

    private void storeDataInAttributeList(int attributeNumber,int dataSize, float[] data){
        int[] vboID = new int[1];
        GLES30.glGenBuffers(1, vboID, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboID[0]);
        vbos.add(vboID[0]);
        System.out.println(vboID[0]);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,data.length * 4, buffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(attributeNumber, dataSize, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO(){
        GLES30.glBindVertexArray(0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = FloatBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}

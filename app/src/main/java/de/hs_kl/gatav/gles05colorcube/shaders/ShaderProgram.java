package de.hs_kl.gatav.gles05colorcube.shaders;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import de.hs_kl.gatav.gles05colorcube.MainActivity;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public ShaderProgram(String vertexFile, String fragmentFile){
        vertexShaderID = loadShader(vertexFile,GLES20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile,GLES20.GL_FRAGMENT_SHADER);
        programID = GLES20.glCreateProgram();
        GLES20.glAttachShader(programID,vertexShaderID);
        GLES20.glAttachShader(programID,fragmentShaderID);
        GLES20.glLinkProgram(programID);
        GLES20.glValidateProgram(programID);
        bindAttributes();

    }

    public void start(){
        GLES20.glUseProgram(programID);
    }

    public void stop(){
        GLES20.glUseProgram(0);
    }

    public void cleanUp(){
        stop();
        GLES20.glDetachShader(programID,vertexShaderID);
        GLES20.glDetachShader(programID,fragmentShaderID);
        GLES20.glDeleteShader(vertexShaderID);
        GLES20.glDeleteShader(fragmentShaderID);
        GLES20.glDeleteProgram(programID);

    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName){
        GLES20.glBindAttribLocation(programID,attribute, variableName);
    }

    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(MainActivity.assetManager.open(file)));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("\n");
            }


            reader.close();
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }


        int shaderID = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shaderID, shaderSource.toString());
        GLES20.glCompileShader(shaderID);
        int[] success = new int[1];
        GLES20.glGetShaderiv(shaderID,GLES20.GL_COMPILE_STATUS,success, 0);
        if(success[0]== GL11.GL_FALSE){
            if(type==GLES30.GL_FRAGMENT_SHADER){
                System.out.println("Fragment");
            }
            else{
                System.out.println("Vertex");
            }
            System.err.println(GLES20.glGetShaderInfoLog(shaderID));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }

}

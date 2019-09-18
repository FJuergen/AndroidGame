package de.hs_kl.gatav.gles05colorcube.objConverter;

public class Vector2f {
    float x, y;
    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float length(){
        return (float)Math.sqrt(x*x + y * y);
    }

}

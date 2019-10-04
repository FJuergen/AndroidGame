package de.hs_kl.gatav.gles05colorcube.objConverter;

public class Vector3f {
    public float x, y, z;
    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length(){
        return (float)Math.sqrt(x*x + y * y + z*z);
    }
}

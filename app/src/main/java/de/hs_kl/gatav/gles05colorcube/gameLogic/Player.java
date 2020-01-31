package de.hs_kl.gatav.gles05colorcube.gameLogic;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.text.method.Touch;

import de.hs_kl.gatav.gles05colorcube.entities.Camera;
import de.hs_kl.gatav.gles05colorcube.entities.Entity;
import de.hs_kl.gatav.gles05colorcube.models.TexturedModel;
import de.hs_kl.gatav.gles05colorcube.normalMappingObjConverter.NormalMappedObjLoader;
import de.hs_kl.gatav.gles05colorcube.textures.ModelTexture;
import de.hs_kl.gatav.gles05colorcube.vector.Vector2f;
import de.hs_kl.gatav.gles05colorcube.vector.Vector3f;
import de.hs_kl.gatav.gles05colorcube.TouchableGLSurfaceView;

public class Player extends Entity{

    float maxSpeed = 0.2f;
    public boolean won = false;
    public boolean lost = false;
    Vector3f originalLocation;

    private Vector3f velocity = new Vector3f(0.0f,0.0f,0.0f);
    private Vector3f counterVelocity = new Vector3f(1.0f,1.0f,1.0f);
    private float counterVelocityDuration = 1000;
    private float lastCollisionTime;
    private float speed = 5.0f;
    private float size = 1.0f;
    private float dragFactor = 0.0f;

    float collisionFinness = 30f;

    Camera camera;

    public Player( Vector3f position, float rx, float ry, float rz, float scale, Camera camera) {
        super(new TexturedModel(NormalMappedObjLoader.loadOBJ("sphere", TouchableGLSurfaceView.loader),new ModelTexture(TouchableGLSurfaceView.loader.loadTexture("white"))),position,rx,ry,rz,1);
        this.setScale(size);
        this.camera = camera;
        this.camera.move(new Vector3f(this.getPosition().x,this.getPosition().y,0.0f));
        originalLocation = new Vector3f(position);
        lastCollisionTime = System.currentTimeMillis();
    }

    public void reset(){
        this.setPosition(new Vector3f(originalLocation));
        camera.setPosition(new Vector3f(this.getPosition().x,this.getPosition().y,0.0f));
        won = false;
        lost = false;
    }

    public void move(float deltaTime, Map map) {
        float[] rotation  = TouchableGLSurfaceView.rotationSensor.getDeviceRotation();
        if(Math.abs(rotation[0]) < 0.001)rotation[0] = 0;
        if(Math.abs(rotation[1]) < 0.001)rotation[1] = 0;
        velocity.x += (rotation[2]) / 10; // x
        velocity.y += (rotation[1]) / 10; // y
        velocity.x = (velocity.x * (1-dragFactor));
        velocity.y = (velocity.y * (1-dragFactor));

        if(Math.abs(velocity.x) > maxSpeed){
            velocity.x = Math.copySign(maxSpeed,velocity.x);
        }
        if(Math.abs(velocity.y) > maxSpeed){
            velocity.y = Math.copySign(maxSpeed,velocity.y);
        }
        //TODO move
        velocity = processCollision(velocity,map);
        this.increasePosition(velocity.x, velocity.y, 0);
        camera.move(velocity);
    }


    //TODO implement collisions
    protected Vector3f processCollision(Vector3f direction, Map map) {
        float offset = 1;
        Vector3f temp = new Vector3f(direction);

        for(float i = 0; i < (Math.PI * 2); i+= ((Math.PI * 2) / collisionFinness)){
            Vector3f currentPosition = new Vector3f(this.getPosition());
            Vector2f hitPosition = map.toMapSpace(currentPosition.translate((float)(temp.x + offset + Math.sin(i)),(float)(temp.y + offset + Math.cos(i)),0));
            Map.MapObjectType intersectedObject = map.getObjectAt((int)hitPosition.x,(int)hitPosition.y);


            if(intersectedObject == Map.MapObjectType.WALL){
                if(Math.sin(i) > 0.5 || Math.sin(i) < -0.5)temp.x = - direction.x;
                if(Math.cos(i) > 0.5 || Math.cos(i) < -0.5)temp.y = - direction.y;
            }
        }

        Vector3f currentPosition = new Vector3f(this.getPosition());
        Vector2f hitPosition = map.toMapSpace(currentPosition.translate(temp.x,temp.y,0));
        Map.MapObjectType intersectedObject = map.getObjectAt((int)hitPosition.x,(int)hitPosition.y);



        if(intersectedObject == Map.MapObjectType.DEATH){
            lost = true;
        }
        if(intersectedObject == Map.MapObjectType.GOAL){
            won = true;
        }

        return temp;
    }
}

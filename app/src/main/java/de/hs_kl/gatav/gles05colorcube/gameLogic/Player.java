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

    float maxSpeed = 0.3f;
    public boolean won = false;
    public boolean lost = false;

    private Vector3f velocity = new Vector3f(0.0f,0.0f,0.0f);
    private Vector3f counterVelocity = new Vector3f(1.0f,1.0f,1.0f);
    private float counterVelocityDuration = 1000;
    private float lastCollisionTime;
    private float speed = 5.0f;
    private float size = 1.0f;
    private float dragFactor = 0.0f;

    Camera camera;

    public Player( Vector3f position, float rx, float ry, float rz, float scale, Camera camera) {
        super(new TexturedModel(NormalMappedObjLoader.loadOBJ("sphere", TouchableGLSurfaceView.loader),new ModelTexture(TouchableGLSurfaceView.loader.loadTexture("white"))),position,rx,ry,rz,1);
        this.setScale(size);
        this.camera = camera;
        camera.move(new Vector3f(this.getPosition().x,this.getPosition().y,0.0f));
        lastCollisionTime = System.currentTimeMillis();
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
        Vector3f currentPosition = new Vector3f(this.getPosition());
        Vector3f temp = new Vector3f(direction);
        Vector2f hitPosition = map.toMapSpace(currentPosition.translate(temp.x,temp.y,0));
        Map.MapObjectType intersectedObject = map.getObjectAt((int)hitPosition.x,(int)hitPosition.y);


        Vector2f hitPositionTop = map.toMapSpace(new Vector3f(currentPosition).translate(temp.x  + 2f,temp.y,0));
        Map.MapObjectType intersectedObjectTop = map.getObjectAt((int)hitPositionTop.x,(int)hitPositionTop.y);
        Vector2f hitPositionBottom = map.toMapSpace(new Vector3f(currentPosition).translate(temp.x -0f,temp.y,0));
        Map.MapObjectType intersectedObjectBottom = map.getObjectAt((int)hitPositionBottom.x ,(int)hitPositionBottom.y);
        Vector2f hitPositionRight = map.toMapSpace(new Vector3f(currentPosition).translate(temp.x,temp.y - 1f,0));
        Map.MapObjectType intersectedObjectRight = map.getObjectAt((int)hitPositionRight.x,(int)hitPositionRight.y);
        Vector2f hitPositionLeft = map.toMapSpace(new Vector3f(currentPosition).translate(temp.x,temp.y + 1f,0));
        Map.MapObjectType intersectedObjectLeft = map.getObjectAt((int)hitPositionLeft.x,(int)hitPositionLeft.y);


        if(intersectedObjectBottom == Map.MapObjectType.WALL || intersectedObjectTop == Map.MapObjectType.WALL){
            temp.x = -direction.x;
            //this.increasePosition(-direction.x,-direction.y,0);
        }
        if(intersectedObjectLeft == Map.MapObjectType.WALL || intersectedObjectRight == Map.MapObjectType.WALL){
            temp.y = -direction.y;
            //this.increasePosition(-direction.x,-direction.y,0);
        }
        if(intersectedObject == Map.MapObjectType.DEATH){
            lost = true;
        }
        if(intersectedObject == Map.MapObjectType.GOAL){
            won = true;
        }

        if(intersectedObject == Map.MapObjectType.WALL){
            System.out.println("WALL");
        }

        return temp;
    }
}

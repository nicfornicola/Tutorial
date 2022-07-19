package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;
    
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch;
    private float yaw;
    private float roll;

    private Player player;

    public Camera(Vector3f position) {
        this.position = position;
    }

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        calculatePitch();
        calculateZoom();
        calculateAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float hDistance, float vDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vDistance;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * .1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch(){
        if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * .1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAroundPlayer(){
        if(Mouse.isButtonDown(0)){
         float angleChange = Mouse.getDX() * .3f;
         angleAroundPlayer -= angleChange;
        }
    }

}

package edu.ohiostate.udodge;

import android.graphics.Bitmap;

/**
 * Created by Nate on 3/20/2018.
 */

public class Ball {
    private Bitmap bitmap;

    private int xPos, yPos;
    private boolean thrown;
    private int screenHeight;
    private int positionNumber;

    //constructor
    public Ball(Bitmap bm) {
        bitmap = bm;

        xPos = 0;
        yPos = 0;
        thrown = false;
    }

    public void throwBall(int positionNumber, int screenHeight) {
        this.screenHeight = screenHeight;
        thrown = true;
        this.positionNumber = positionNumber;
        yPos = screenHeight;
    }

    public void missed() {
        thrown = false;
        yPos = screenHeight;
    }

    public void hit() {
        thrown = false;
        yPos = screenHeight;
    }

    //getters and setters
    public int getPositionNumber(){ return positionNumber; }

    public void setPositionNumber(int positionNumber) { this.positionNumber = positionNumber; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isThrown() {
        return thrown;
    }
}

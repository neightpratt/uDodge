package edu.ohiostate.udodge;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Nate on 3/20/2018.
 */

public class Character {
    private Bitmap bodyBitmap;
    private Bitmap headBitmap;

    private int positionNumber;
    private int xPos, yPos;

    //constructor
    public Character(Bitmap bodyBM, Bitmap headBM) {
        bodyBitmap = bodyBM;
        headBitmap = headBM;

        xPos = 0;
        yPos = 0;
        positionNumber = 1;
    }

    //getters and setters
    public Bitmap getBodyBitmap() {
        return bodyBitmap;
    }

    public void setBodyBitmap(Bitmap bitmap) {
        this.bodyBitmap = bitmap;
    }

    public Bitmap getHeadBitmap() { return headBitmap; }

    public void setHeadBitmap(Bitmap bitmap) { this.headBitmap = bitmap; }

    public int getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(int currentPos) {
        this.positionNumber = currentPos;

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
}

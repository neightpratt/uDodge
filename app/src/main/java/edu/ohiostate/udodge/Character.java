package edu.ohiostate.udodge;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Nate on 3/20/2018.
 */

public class Character {
    private Bitmap bitmap;

    private int positionNumber;
    private int xPos, yPos;

    //constructor
    public Character(Bitmap bm) {
        bitmap = bm;

        xPos = 0;
        yPos = 0;
        positionNumber = 1;
    }

    //getters and setters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

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

package edu.ohiostate.udodge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Nate on 3/13/2018.
 */

public class Game extends View {
    int screenWidth;
    int screenHeight;
    int X;
    int Y;
    int initialY ;
    int characterWidth;
    int characterHeight;
    int angle;
    float dX;
    float acc;
    Bitmap character, background;

    public Game(Context context) {
        super(context);
        character = BitmapFactory.decodeResource(getResources(),R.drawable.test_character); //load a character image
        background = BitmapFactory.decodeResource(getResources(),R.drawable.gym); //load a background
        characterWidth = character.getWidth();
        characterHeight = character.getHeight();
        acc = 0.2f; //acceleration
        dX = 5; //vertical speed
        initialY = 100; //Initial vertical position.
        angle = 0; //Start value for rotation angle.
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        background = Bitmap.createScaledBitmap(background, w, h, true); //Resize background to fit the screen.
        X = (int) (screenWidth /2) - (characterWidth / 2) ; //Centre character into the centre of the screen.
        Y = (int) (screenHeight / 5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw background.
        canvas.drawBitmap(background, 0, 0, null);

        //Compute roughly character speed and location.
        //X+= (int) dX; //Increase or decrease horizontal position.
        //if (X > (screenWidth - characterWidth) || (X <= 0)) {
        //    dX =(-1)* dX; //Reverse speed when bottom hit.
        //}
        //dX += acc; //Increase or decrease speed.

        //Increase rotating angle.
        //if (angle++ >360)
        //    angle =0;

        //Draw character
        canvas.save(); //Save the position of the canvas.
        //canvas.rotate(angle, X + (characterWidth / 2), Y + (characterHeight / 2)); //Rotate the canvas.
        canvas.drawBitmap(character, X, Y, null); //Draw the character on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like character has rotated.

        //Call the next frame.
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                moveCharacterHorizontal(x);
                break;
            case MotionEvent.ACTION_MOVE:
                moveCharacterHorizontal(x);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void moveCharacterHorizontal(float x) {
        if (x > screenWidth - characterWidth / 2){
            X = screenWidth - characterWidth;
        } else if (x < characterWidth / 2) {
            X = 0;
        } else {
            X = (int) x - characterWidth / 2;
        }
    }
}
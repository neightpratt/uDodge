package edu.ohiostate.udodge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nate on 3/13/2018.
 */

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    //constants
    static final int MIN_SWIPE_DISTANCE = 150;
    static final int BALL_SPEED_SLOW = 10;
    static final int BALL_SPEED_MEDIUM = 15;
    static final int BALL_SPEED_FAST = 15;
    static final int TOTAL_NUM_BALLS = 50;
    static final int POINTS_PER_DODGING = 25;

    //thread
    GameThread thread;

    //game vars
    int screenWidth;
    int screenHeight;
    int characterWidth;
    int characterHeight;
    Bitmap background;
    int score;
    int countDown;
    Paint scorePaint;
    Paint countDownPaint;
    int ballToThrow;
    Random random;
    int ballThrowRateMS;
    boolean freezeGame;

    //Touch variables
    float touchX1;
    float touchX2;
    float touchY1;
    float touchY2;

    //Measure frames per second.
    long now;
    int framesCount = 0;
    int framesCountAvg = 0;
    long previousFrameTime = 0;
    long previousFrameTime2 = 0;
    Paint fpsPaint;

    //Frame speed
    long timeNow;
    long timePrev = 0;
    long timePrevFrame = 0;
    long timeDelta;

    //Character movement
    Character character;
    private ArrayList<Integer> characterXPositions;
    Bitmap charBitmap;

    //obstacles
    private ArrayList<Ball> dodgeBalls;
    private ArrayList<Integer> ballXPositions;
    Bitmap normalBallBitmap;
    Bitmap scaledBallBitmap;

    public Game(Context context) {
        super(context);
        prepareGame();
    }

    public void prepareGame () {
        //get resources
        charBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_character); //load character image
        normalBallBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.normal_ball); //load character image
        background = BitmapFactory.decodeResource(getResources(),R.drawable.gym); //load a background
        //create arrays for holding character/ball positions
        characterXPositions = new ArrayList<>(3);
        ballXPositions = new ArrayList<>(3);
        //array to hold a max of 6 dodge balls that will all be thrown randomly
        dodgeBalls = new ArrayList<>(TOTAL_NUM_BALLS);
        //initialize game stuff
        freezeGame = false;
        ballThrowRateMS = 1000;
        random = new Random();
        score = 0;
        countDown = 3;
        scorePaint = new Paint();
        countDownPaint = new Paint();
        fpsPaint = new Paint();
        scorePaint.setTextSize(50);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        countDownPaint.setTextSize(250);
        countDownPaint.setTypeface(Typeface.DEFAULT_BOLD);
        fpsPaint.setTextSize(30);

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);
    }

    public void gameOver () {
        freezeGame = true;
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        //create character object
        character = new Character(Bitmap.createScaledBitmap(charBitmap, (int)(screenWidth * 0.3), (int)(screenHeight * 0.4), false));
        characterWidth = character.getBitmap().getWidth();
        characterHeight = character.getBitmap().getHeight();
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        scaledBallBitmap = Bitmap.createScaledBitmap(normalBallBitmap, screenHeight / 9, screenHeight / 9, false);
        //add balls to dodge ball array
        for (int i = 0; i < TOTAL_NUM_BALLS; i++) {
            dodgeBalls.add(new Ball(scaledBallBitmap));
        }
        ballToThrow = 0;
        characterXPositions.add((screenWidth / 4) - (characterWidth / 2));
        characterXPositions.add((screenWidth / 2) - (characterWidth / 2));
        characterXPositions.add((3 * screenWidth / 4) - (characterWidth / 2));
        ballXPositions.add(screenWidth / 4); //since ball size changes we cant subtract half of width here like we do for character
        ballXPositions.add(screenWidth / 2);
        ballXPositions.add(3 * screenWidth / 4);
        character.setPositionNumber(1);
        character.setxPos(characterXPositions.get(character.getPositionNumber()));
        character.setyPos(screenHeight / 5);
    }

    public void myUpdate() {
        //fps stuff
        now=System.currentTimeMillis();
        framesCount++;
        //balls are thrown at the rate of 1 per second and this rate speed up as time goes by
        if(now - previousFrameTime2 > ballThrowRateMS) {
            previousFrameTime2 = now;
            score += 1;
            //randomized ball throwing
            int doubleThrow = random.nextInt(5);
            if (doubleThrow == 0) {
                dodgeBalls.get(ballToThrow).throwBall(random.nextInt(3), screenHeight);
                if (ballToThrow == dodgeBalls.size() - 1) {
                    ballToThrow = 0;
                } else {
                    ballToThrow++;
                }
                dodgeBalls.get(ballToThrow).throwBall(random.nextInt(3), screenHeight);
                if (ballToThrow == dodgeBalls.size() - 1) {
                    ballToThrow = 0;
                } else {
                    ballToThrow++;
                }
            } else {
                dodgeBalls.get(ballToThrow).throwBall(random.nextInt(3), screenHeight);
                if (ballToThrow == dodgeBalls.size() - 1) {
                    ballToThrow = 0;
                } else {
                    ballToThrow++;
                }
            }
        }
        //every second we update some fps variables
        if(now - previousFrameTime > 1000) {
            //fps
            previousFrameTime = now;
            framesCountAvg = framesCount;
            framesCount = 0;
            if (ballThrowRateMS > 450) ballThrowRateMS-=25;
            Log.d("TEST", "rate: " + ballThrowRateMS);
        }

        //game stuff

        //if ball is thrown, update y position and rescale so it looks like its moving further away
        for (Ball ball : dodgeBalls) {
            if (ball.isThrown()) {
                Bitmap currentBallBM = ball.getBitmap();
                int currentBallWidth = currentBallBM.getWidth();
                ball.setyPos(ball.getyPos() - BALL_SPEED_SLOW);
                ball.setBitmap(Bitmap.createScaledBitmap(normalBallBitmap, currentBallWidth - 1, currentBallWidth - 1, false));
                ball.setxPos(ballXPositions.get(ball.getPositionNumber()) - (ball.getBitmap().getWidth() / 2));
                if (ball.getyPos() < character.getyPos() + characterHeight / 2) {
                    if (ball.getPositionNumber() != character.getPositionNumber()) { //successful dodge
                        ball.missed();
                        ball.setBitmap(scaledBallBitmap);
                        score += POINTS_PER_DODGING;
                    } else { //got hit
                        ball.hit();
                        ball.setBitmap(scaledBallBitmap);
                        gameOver();
                    }
                }
            }
        }
    }

    public void myDraw(Canvas canvas) {

        //Draw background.
        canvas.drawBitmap(background, 0, 0, null);

        //Draw character
        canvas.drawBitmap(character.getBitmap(), character.getxPos(), character.getyPos(), null); //Draw the character on the rotated canvas.

        //Draw dodge balls
        for (Ball ball : dodgeBalls) {
            if (ball.isThrown()) {
                canvas.drawBitmap(ball.getBitmap(), ball.getxPos(), ball.getyPos(), null);
            }
        }

        //Draw HUD info
        String scoreText = "Score: " + score;
        canvas.drawText(framesCountAvg+" fps", 40, 70, fpsPaint);
        canvas.drawText(scoreText, (screenWidth / 2) - (scorePaint.measureText(scoreText) / 2), 70, scorePaint);
    }

    //touch stuff
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float eventX = event.getX();
        float eventY = event.getY();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                touchX1 = eventX;
                touchY1 = eventY;
                break;
            case MotionEvent.ACTION_UP:
                touchX2 = eventX;
                touchY2 = eventY;
                float deltaX = touchX2 - touchX1;
                if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) { // left/right swipe detected
                    if (touchX2 > touchX1) { // swipe left to right
                        moveCharacter("RIGHT");
                    } else { // swipe right to left
                        moveCharacter("LEFT");
                    }
                } else { //probably was a screen tap

                }
                float deltaY = touchY2 - touchY1;
                if (Math.abs(deltaY) > MIN_SWIPE_DISTANCE) { // left/right swipe detected
                    if (touchY2 > touchY1) { // swipe bottom to top
                        moveCharacter("UP");
                    } else { // swipe top to bottom
                        moveCharacter("DOWN");
                    }
                } else { //probably was a screen tap

                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return !freezeGame;
    }

    private void moveCharacter(String direction) {
        int positionNum = character.getPositionNumber();
        if (direction == "RIGHT" && positionNum < 2) {
            positionNum++;
            character.setPositionNumber(positionNum);
        } else if (direction == "LEFT" && positionNum > 0){
            positionNum--;
            character.setPositionNumber(positionNum);
        }
        character.setxPos(characterXPositions.get(positionNum));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private Game gameView;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, Game gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (run) {
                c = null;

                //limit frame rate to max 60fps
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if (timeDelta < 16) {
                    try {
                        Thread.sleep(16 - timeDelta);
                    }
                    catch(InterruptedException e) {

                    }
                }
                timePrevFrame = System.currentTimeMillis();

                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        //call methods to draw and process next fame
                        if (!freezeGame) gameView.myUpdate();
                        gameView.myDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}



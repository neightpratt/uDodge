package edu.ohiostate.udodge;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static android.graphics.Path.Direction.CCW;

/**
 * Created by Nate on 3/13/2018.
 */

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private Comparator<Score> comparator = new Comparator<Score>(){

        @Override
        public int compare(Score o1, Score o2){
            return o2.getScore() - o1.getScore();
        }
    };

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
    int characterBodyWidth;
    int characterBodyHeight;
    int characterHeadWidth;
    int characterHeadHeight;
    Bitmap background;
    private int mScore;
    Paint scorePaint;
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
    Bitmap charBodyBitmap;
    Bitmap charHeadBitmap;
    Bitmap scaledCharBodyBitmap;
    Bitmap scaledCharHeadBitmap;
    boolean avatarDetected;

    //obstacles
    private ArrayList<Ball> dodgeBalls;
    private ArrayList<Integer> ballXPositions;
    Bitmap normalBallBitmap;
    Bitmap scaledBallBitmap;

    private DatabaseReference mDatabase;
    private DatabaseReference minDatabase;
    private int maxList = 100;
    private static final String TAG = "Game";
    private Score minScore;
    private List<Score> scores;
    private DataSnapshot mDataSnapshot;

    public Game(Context context) {
        super(context);
        prepareGame();
    }

    public void prepareGame () {
        //get resources
        charBodyBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.avatar_body); //load character body
        // define shared preferences and its editor
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        avatarDetected = preferences.getBoolean("acceptedAvatar", false);
        if (avatarDetected) {
            String avatar_src = preferences.getString("real_avatar", null);
            if (avatar_src != null) {
                Bitmap avatarBits = BitmapFactory.decodeFile(avatar_src);
                //avatarBits = Bitmap.createScaledBitmap(avatarBits, 1480, 1020, false);
                charHeadBitmap = avatarBits;
                charHeadBitmap = getRoundedShape(charHeadBitmap);
            }
        } else {
            charHeadBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_head); //load default character head
        }

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
        mScore = 0;
        scorePaint = new Paint();
        fpsPaint = new Paint();
        scorePaint.setTextSize(50);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        fpsPaint.setTextSize(30);

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        //create character object
        scaledCharBodyBitmap = Bitmap.createScaledBitmap(charBodyBitmap, (int)(screenWidth * 0.35), (int)(screenHeight * 0.3), false);
        if (avatarDetected) scaledCharHeadBitmap = Bitmap.createScaledBitmap(charHeadBitmap, (int)(screenWidth * 0.3), (int)(screenHeight * 0.15), false);
        else scaledCharHeadBitmap = Bitmap.createScaledBitmap(charHeadBitmap, (int)(screenWidth * 0.15), (int)(screenHeight * 0.15), false);
        if (avatarDetected) {
            Matrix mat = new Matrix();
            mat.postRotate(-90);
            scaledCharHeadBitmap = Bitmap.createBitmap(scaledCharHeadBitmap, 0, 0, scaledCharHeadBitmap.getWidth(), scaledCharHeadBitmap.getHeight(), mat, false);
        }
        character = new Character(scaledCharBodyBitmap, scaledCharHeadBitmap);
        characterBodyWidth = character.getBodyBitmap().getWidth();
        characterBodyHeight = character.getBodyBitmap().getHeight();
        characterHeadWidth = character.getHeadBitmap().getWidth();
        characterHeadHeight = character.getHeadBitmap().getHeight();
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        scaledBallBitmap = Bitmap.createScaledBitmap(normalBallBitmap, (int)(screenHeight * 0.13), (int)(screenHeight * 0.13), false);
        //add balls to dodge ball array
        for (int i = 0; i < TOTAL_NUM_BALLS; i++) {
            dodgeBalls.add(new Ball(scaledBallBitmap));
        }
        ballToThrow = 0;
        characterXPositions.add((screenWidth / 4) - (characterBodyWidth / 2));
        characterXPositions.add((screenWidth / 2) - (characterBodyWidth / 2));
        characterXPositions.add((3 * screenWidth / 4) - (characterBodyWidth / 2));
        ballXPositions.add(screenWidth / 4); //since ball size changes we cant subtract half of width here like we do for character
        ballXPositions.add(screenWidth / 2);
        ballXPositions.add(3 * screenWidth / 4);
        character.setPositionNumber(1);
        character.setxPos(characterXPositions.get(character.getPositionNumber()));
        character.setyPos((int)(screenHeight * 0.25));
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int targetWidth = metrics.widthPixels;
        int targetHeight = metrics.heightPixels;

        Bitmap targetBitmap = Bitmap.createBitmap(targetHeight,
                targetWidth, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        /*path.addCircle(
                ((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);*/
        path.addOval(new RectF(205, 82, targetHeight - 40,
                targetWidth - 115), CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(
                sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
                        .getHeight()), new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
                        .getHeight()), null);
        return targetBitmap;
    }

    public void gameOver () {
        freezeGame = true;
        thread.setRunning(false);

        /*
         * Check local high score
         */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int score = preferences.getInt("score", 0);

        /*
         * If the score of the current game is greater than local high score, update local high score
         */
        if (mScore > score){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("score", mScore);
            editor.commit();
        }




        minDatabase = FirebaseDatabase.getInstance().getReference();
        minDatabase.child("min_score").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*
                 * Get lowest high score
                 */
                minScore = new Score();
                for (DataSnapshot scoreDataSnapshot : dataSnapshot.getChildren()) {
                    minScore = scoreDataSnapshot.getValue(Score.class);
                }

                Log.d(TAG, "min score: "+Integer.toString(minScore.getScore()));
                Log.d(TAG, "uid: "+minScore.getUid());

                if (mScore > minScore.getScore()){

                    Log.d(TAG, "add new score");

                    /*
                    * Pull the global database to see if the current score is in the top 100 scores
                    */
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("scores").addListenerForSingleValueEvent(new ValueEventListener(){

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mDataSnapshot = dataSnapshot;


                           /*
                             * bring up a dialog pop up window asking for a name for the score
                             */
                            // Setup the pop up window
                            AlertDialog.Builder builder = new AlertDialog.Builder(((Activity) getContext()));
                            builder.setTitle("Congratulations!");
                            builder.setMessage("Congratulations! You are in the top 100! Enter your name:");

                            final EditText input = new EditText((Activity) getContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

                            // Click listener for when the user presses okay
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "In on click");

                                    /*
                                     * Get the global high scores
                                     */
                                    scores = new ArrayList<>();
                                    for (DataSnapshot scoresDataSnapshot : mDataSnapshot.getChildren()) {
                                        Score score = scoresDataSnapshot.getValue(Score.class);
                                        scores.add(score);
                                    }

                                    Log.d(TAG, "Size of database: " + Integer.toString(scores.size()));

                                    /*
                                     * Order them greatest to least
                                     */
                                    Collections.sort(scores, comparator);


                                    /*
                                     * Remove lowest score
                                     */

                                    /*
                                    Score testscore = new Score();
                                    testscore.setUid(mDatabase.child("scores").push().getKey());
                                    testscore.setName("Boo Boo Bloop");
                                    testscore.setScore(89);
                                    mDatabase.child("scores").child(testscore.getUid()).setValue(testscore);
                                    */

                                    Log.d(TAG, "Removing score from database");
                                    mDatabase.child("scores").child(scores.get(maxList - 1).getUid()).removeValue();

                                    /*
                                     * Set new minimum score
                                     */
                                    Log.d(TAG, "Setting new score");
                                    minScore.setScore(scores.get(maxList - 2).getScore());
                                    Log.d(TAG, "got new min score: "+Integer.toString(minScore.getScore()));
                                    Log.d(TAG, "uid: "+minScore.getUid());
                                    minDatabase.child("min_score").child(minScore.getUid()).setValue(minScore);



                                    Log.d(TAG, "Adding new score");
                                    // Add the score to the database
                                    Score score = new Score();
                                    score.setUid(mDatabase.child("scores").push().getKey());
                                    score.setName(input.getText().toString());
                                    score.setScore(mScore);
                                    mDatabase.child("scores").child(score.getUid()).setValue(score);

                                    Log.d(TAG, "Finish");
                                    ((Activity) getContext()).finish();
                                }
                            });

                            // Click listener for when the user presses cancel
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((Activity) getContext()).finish();
                                }
                            });

                            builder.show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError){

                        }
                    });
                }else{
                    Log.d(TAG, "dont add new score");
                    ((Activity) getContext()).finish();

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void myUpdate() {
        //fps stuff
        now=System.currentTimeMillis();
        framesCount++;
        //balls are thrown at the rate of 1 per second and this rate speed up as time goes by
        if(now - previousFrameTime2 > ballThrowRateMS) {
            previousFrameTime2 = now;
            mScore += 1;
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
                if (ball.getyPos() < character.getyPos() + characterBodyHeight / 2) {
                    if (ball.getPositionNumber() != character.getPositionNumber()) { //successful dodge
                        ball.missed();
                        ball.setBitmap(scaledBallBitmap);
                        mScore += POINTS_PER_DODGING;
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
        int headX = character.getxPos() + (characterBodyWidth / 2) - (characterHeadWidth / 2) + 5;
        int headY = character.getyPos() - (int)(characterHeadHeight * 0.85);
        canvas.drawBitmap(character.getBodyBitmap(), character.getxPos(), character.getyPos(), null); //Draw the character on the rotated canvas.
        canvas.drawBitmap(character.getHeadBitmap(), headX, headY, null);
        //Draw dodge balls
        for (Ball ball : dodgeBalls) {
            if (ball.isThrown()) {
                canvas.drawBitmap(ball.getBitmap(), ball.getxPos(), ball.getyPos(), null);
            }
        }

        //Draw HUD info
        String scoreText = "Score: " + mScore;
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
        freezeGame = true;
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
                        if (!freezeGame){
                            gameView.myUpdate();
                            gameView.myDraw(c);
                        }

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



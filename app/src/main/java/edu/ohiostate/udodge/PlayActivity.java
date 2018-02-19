package edu.ohiostate.udodge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    // get a hold of the character
    private ImageView mCharacter;
    private TextView mScoreLabel;

    // score
    private int score = 0;

    public PlayActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mCharacter = (ImageView) findViewById(R.id.characterView);
        mScoreLabel = (TextView) findViewById(R.id.score_label);
        mScoreLabel.setText("" + score);

        mCharacter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                score++;
                mScoreLabel.setText("" + score);
                return true;
            }
        });

    }
}

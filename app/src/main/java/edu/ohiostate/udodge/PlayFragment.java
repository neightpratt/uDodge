package edu.ohiostate.udodge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nate on 3/2/2018.
 */

public class PlayFragment extends Fragment {
    private ImageView mCharacter;
    private TextView mScoreLabel;

    private int score = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_play, container, false);

        mCharacter = (ImageView) v.findViewById(R.id.characterView);
        mScoreLabel = (TextView) v.findViewById(R.id.score_label);
        mScoreLabel.setText("" + score);

        mCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score++;
                mScoreLabel.setText("" + score);
            }
        });
        return v;
    }
}

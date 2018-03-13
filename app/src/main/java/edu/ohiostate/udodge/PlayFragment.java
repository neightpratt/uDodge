package edu.ohiostate.udodge;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nate on 3/2/2018.
 */

public class PlayFragment extends Fragment {
    private Comparator<Score> comparator = new Comparator<Score>(){

        @Override
        public int compare(Score o1, Score o2){
            return o2.getScore() - o1.getScore();
        }
    };

    private ImageView mCharacter;
    private TextView mScoreLabel;
    private Button mEndButton;
    private DatabaseReference mDatabase;

    private int mScore = 0;
    private int maxList = 100;

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
        mScoreLabel.setText("" + mScore);

        mCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore++;
                mScoreLabel.setText("" + mScore);
            }
        });

/*****************************END GAME****************************************/

        /*
         * When End Game button is clicked, the game should end
         */
        mEndButton = (Button) v.findViewById(R.id.end_button);
        mEndButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

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

                /*
                 * Pull the global database to see if the current score is in the top 100 scores
                 */
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("scores").addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        /*
                         * Get the global high scores
                         */
                        List<Score> scores = new ArrayList<>();
                        for (DataSnapshot scoresDataSnapshot : dataSnapshot.getChildren()){
                            Score score = scoresDataSnapshot.getValue(Score.class);
                            scores.add(score);
                        }

                        /*
                         * Order them greatest to least
                         */
                        Collections.sort(scores, comparator);

                        /*
                         * Check if the database has less than the max amount of entries or if the current
                         * score is greater than the lowest score in the database
                         */
                        Boolean add = false;
                        if (scores.size() < maxList){
                            add = true;
                        }else if (mScore > scores.get(maxList - 1).getScore()){
                            // Remove the lowest score from the database
                            mDatabase.child("scores").child(scores.get(maxList - 1).getUid()).removeValue();
                            add = true;
                        }

                        /*
                         * If the score can be added to the database, bring up a dialog pop up window asking
                         * for a name for the score
                         */
                        if (add) {
                            // Setup the pop up window
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Congratulations!");
                            builder.setMessage("Congratulations! You are in the top 100! Enter your name:");

                            final EditText input = new EditText(getActivity());
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

                            // Click listener for when the user presses okay
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Add the score to the database
                                    Score score = new Score();
                                    score.setUid(mDatabase.child("scores").push().getKey());
                                    score.setName(input.getText().toString());
                                    score.setScore(mScore);
                                    mDatabase.child("scores").child(score.getUid()).setValue(score);

                                    getActivity().finish();
                                }
                            });

                            builder.show();
                        }else{
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError){

                    }
                });


            }
        });

        return v;
    }
}

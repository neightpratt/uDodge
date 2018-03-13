package edu.ohiostate.udodge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Random;

/**
 * Created by Greg on 3/12/2018.
 */



public class LeaderboardFragment extends Fragment {

    private Comparator<Score> comparator = new Comparator<Score>(){

        @Override
        public int compare(Score o1, Score o2){
            return o2.getScore() - o1.getScore();
        }
    };

    private DatabaseReference mDatabase;
    private RecyclerView mScoreRecyclerView;
    private ScoreAdapter mAdapter;
    private TextView mYourHighScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mScoreRecyclerView = (RecyclerView) view.findViewById(R.id.score_recycler_view);
        mScoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Score> scores = new ArrayList<>();
        mAdapter = new ScoreAdapter(scores);
        mScoreRecyclerView.setAdapter(mAdapter);

        // Update the local high score
        mYourHighScore = (TextView) view.findViewById(R.id.your_high_score);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int score = preferences.getInt("score", 0);
        mYourHighScore.setText(Integer.toString(score));

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        /*
         * Update the recyclerview when the database is updated
         */
        mDatabase.child("scores").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                // Pull the database into the list
                List<Score> scores = new ArrayList<>();
                for (DataSnapshot scoreDataSnapshot : dataSnapshot.getChildren()){
                    Score score = scoreDataSnapshot.getValue(Score.class);
                    scores.add(score);
                }

                Collections.sort(scores, comparator);

                mAdapter.updateList(scores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    /***** Recyclerview setup ********/
    private class ScoreHolder extends RecyclerView.ViewHolder {

        private TextView mNameTextView;
        private TextView mScoreTextView;
        private TextView mDateTextView;

        private Score mScore;

        // Setup view for each item in list
        public ScoreHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_score, parent, false));

            mNameTextView = (TextView) itemView.findViewById(R.id.score_name);
            mScoreTextView = (TextView) itemView.findViewById(R.id.score_score);
            mDateTextView = (TextView) itemView.findViewById(R.id.score_date);
        }

        // Set specific values for each item in the list
        public void bind(Score score){
            mScore = score;
            mNameTextView.setText(mScore.getName());
            mScoreTextView.setText(Integer.toString(mScore.getScore()));
            mDateTextView.setText(mScore.getDate().toString());
        }
    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreHolder> {

        private List<Score> mScores;

        public ScoreAdapter(List<Score> scores){
            mScores = scores;
        }

        @Override
        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new ScoreHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScoreHolder holder, int position){
            Score score = mScores.get(position);
            holder.bind(score);
        }

        @Override
        public int getItemCount(){
            return mScores.size();
        }

        // If the size of the list or values in the list change, update the list
        public void updateList(List<Score> scores){
            if (scores.size() != mScores.size() || !mScores.containsAll(scores)){
                mScores = scores;
                notifyDataSetChanged();
            }
        }
    }
}

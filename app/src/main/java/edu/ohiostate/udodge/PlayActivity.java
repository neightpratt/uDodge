package edu.ohiostate.udodge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "PlayActivity onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }
}

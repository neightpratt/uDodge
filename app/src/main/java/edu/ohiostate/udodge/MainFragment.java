package edu.ohiostate.udodge;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private Button mPlayButton;
    private Button mLeaderboardButton;
    private Button mAvatarButton;
    private ImageView mVolumeIcon;
    private Boolean mVolumeOn;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.d(TAG, "onCreate(Bundle) called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonPlay:
                        Intent intent = new Intent(getActivity(), PlayActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.buttonLeaderboard:
                        Toast.makeText(getActivity(), "LeaderBoard Button Pressed", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.buttonAvatarPicture:
                        Toast.makeText(getActivity(), "Avatar Picture Button Pressed", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.volume_icon:
                        if (mVolumeOn) {
                            mVolumeIcon.setImageResource(R.drawable.volume_off_icon);
                            mVolumeOn = false;
                        } else {
                            mVolumeIcon.setImageResource(R.drawable.volume_on_icon);
                            mVolumeOn = true;
                        }
                        break;
                }
            }
        };
        // set up buttons
        mPlayButton = (Button) v.findViewById(R.id.buttonPlay);
        mLeaderboardButton = (Button) v.findViewById(R.id.buttonLeaderboard);
        mAvatarButton = (Button) v.findViewById(R.id.buttonAvatarPicture);
        mVolumeIcon = (ImageView) v.findViewById(R.id.volume_icon);
        mPlayButton.setOnClickListener(clickListener);
        mLeaderboardButton.setOnClickListener(clickListener);
        mAvatarButton.setOnClickListener(clickListener);
        mVolumeIcon.setOnClickListener(clickListener);

        mVolumeOn = true;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}

package edu.ohiostate.udodge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private Button mPlayButton;
    private Button mLeaderboardButton;
    private Button mAvatarButton;
    //private ImageView mVolumeIcon;
    private ImageView mAvatarIcon;
    //private boolean mVolumeOn;
    private boolean mAvatarDetected;
    private static final int PICTURE_CODE = 31069;

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
                Intent intent;
                switch (v.getId()) {
                    case R.id.buttonPlay:
                        intent = new Intent(getActivity(), PlayActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.buttonLeaderboard:
                        intent = new Intent(getActivity(), LeaderboardActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.avatar_icon:
                    case R.id.buttonAvatarPicture:
                        intent = new Intent(getActivity(), AvatarActivity.class);
                        startActivity(intent);
                        break;
//                    case R.id.volume_icon:
//                        if (mVolumeOn) {
//                            mVolumeIcon.setImageResource(R.drawable.volume_off_icon);
//                            mVolumeOn = false;
//                        } else {
//                            mVolumeIcon.setImageResource(R.drawable.volume_on_icon);
//                            mVolumeOn = true;
//                        }
//                        break;
                }
            }
        };
        // set up buttons
        mPlayButton = (Button) v.findViewById(R.id.buttonPlay);
        mLeaderboardButton = (Button) v.findViewById(R.id.buttonLeaderboard);
        mAvatarButton = (Button) v.findViewById(R.id.buttonAvatarPicture);
        //mVolumeIcon = (ImageView) v.findViewById(R.id.volume_icon);
        mAvatarIcon = (ImageView) v.findViewById(R.id.avatar_icon);
        mPlayButton.setOnClickListener(clickListener);
        mLeaderboardButton.setOnClickListener(clickListener);
        mAvatarButton.setOnClickListener(clickListener);
        //mVolumeIcon.setOnClickListener(clickListener);
        mAvatarIcon.setOnClickListener(clickListener);

        // define shared preferences and its editor
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();

        // try to set the avatar icon in the bottom-right corner
        mAvatarDetected = preferences.getBoolean("acceptedAvatar", false);
        Log.d(TAG, "Avatar detected? " + mAvatarDetected);

        if (mAvatarDetected) {
            String avatar_src = preferences.getString("real_avatar", null);
            Log.d(TAG, "real_avatar: " + avatar_src);
            if (avatar_src != null) {
                Bitmap avatarBits = BitmapFactory.decodeFile(avatar_src);
                avatarBits = Bitmap.createScaledBitmap(avatarBits, 1480, 1020, false);
                mAvatarIcon.setImageBitmap(avatarBits);
                mAvatarIcon.setRotation(-90);
            } else {
                editor.putBoolean("acceptedAvatar", false);
                editor.commit();
            }
        }

        //mVolumeOn = true;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Result received");
        if (requestCode == PICTURE_CODE && resultCode == RESULT_OK) {
            // fire a different intent for the moment (until we can confirm it works)
            Log.d(TAG, "Starting main result action");
            Bundle extras = data.getExtras();
            Uri pictureURI = (Uri) extras.get("uri");

            Intent displayPictureIntent = new Intent(getActivity(), PictureActivity.class);
            displayPictureIntent.putExtra("uri", pictureURI);
            Log.d(TAG, "Starting picture preview activity");
            startActivity(displayPictureIntent);
            //Toast.makeText(getActivity(), "No permission to use camera", Toast.LENGTH_SHORT).show();
        }
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
        File avatarDirectory = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "uDodge");

    }
}

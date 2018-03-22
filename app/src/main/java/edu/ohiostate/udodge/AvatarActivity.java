package edu.ohiostate.udodge;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Path.Direction.CCW;

public class AvatarActivity extends AppCompatActivity {
        private Camera mCamera;
        private CameraPreview mCameraPreview;
        private Button mCaptureButton;
        private static final int MY_CAMERA_REQUEST_CODE = 4657;

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_avatar);
            obtainCamera();
            mCameraPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);

            mCaptureButton = (Button) findViewById(R.id.button_capture);
            mCaptureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.takePicture(null, null, mPicture);
                    Toast.makeText(AvatarActivity.this, "Taking picture", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        private void obtainCamera() {
            //Toast.makeText(this, "Attempting to get camera.", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("AvatarActivity", "Requesting appropriate permissions");
                //Toast.makeText(this, "No permission to use camera", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
                obtainCamera();
            } else {
                mCamera = getCameraInstance();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            Log.d("AvatarActivity", "onPause() called");
            /*mCamera.release();
            Log.d("PAUSE", "Camera released");
            mCamera.stopPreview();
            mCamera = null;*/
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (mCamera == null) {
                obtainCamera();
            }
            mCamera.startPreview();
        }

        /**
         * Helper method to access the camera returns null if it cannot get the
         * camera or does not exist
         *
         * @return
         */
        private Camera getCameraInstance() {
            Camera camera = null;
            try {
                camera = Camera.open(1);
                if (camera == null) {
                    camera = Camera.open(0);
                }
            } catch (Exception e) {
                Log.e("CAMERA", e.getMessage());
            }
            return camera;
        }

        Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("FILE_WRITE", "Byte array length: " + data.length);
                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    // convert data to Bitmap
                    Bitmap b = BitmapFactory.decodeByteArray(data, 0, 0);

                    // crop the image
                    //b = getRoundedShape(b);
                    Log.d("FILE_WRITE", "Creating file stream");
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    Log.d("FILE_WRITE", "Writing");
                    fos.write(data);
                    Log.d("FILE_WRITE", "Closing");
                    fos.close();
                    Log.d("FILE_WRITE", "CLOSED - file size: " + pictureFile.length());
                    Log.d("FILE_WRITE", "Alleged URI: " + pictureFile.getAbsolutePath());

                    Uri pictureURI = getOutputMediaFileUri(pictureFile);
                    Log.d("FILE_WRITE", "Get URI");

                    // store file URI in shared preferences
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Log.d("FILE_WRITE", "Avatar src: " + pictureURI.toString());
                    editor.putString("avatar", pictureURI.toString());
                    editor.commit();
                    Log.d("FILE_WRITE", "Avatar src: " + preferences.getString("avatar", null));

                    // go to PictureActivity
                    Intent intent = new Intent(AvatarActivity.this, PictureActivity.class);
                    startActivity(intent);

                } catch (FileNotFoundException e) {
                    Log.e("After_Picture_Taken", "File not found");
                } catch (IOException e) {
                    Log.e("After_Picture_Taken", "Some other I/O Exception");
                }
            }
        };

        private static File getOutputMediaFile() {
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "uDodge");
            Log.d("AvatarActivity", "Absolute path: " + mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("AvatarActivity", "failed to create directory");
                    return null;
                } else {
                    Log.i("AvatarActivity", "uDodge doesn't exist.  Should have created it.");
                }
            } else {
                Log.d("GetOutputMediaFile", "Directories created.");
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
            Log.d("GetOutputMediaFile", "File size: " + mediaFile.length());
            return mediaFile;
        }

        private static Uri getOutputMediaFileUri(File pictureFile){
            return Uri.fromFile(pictureFile);
        }
}
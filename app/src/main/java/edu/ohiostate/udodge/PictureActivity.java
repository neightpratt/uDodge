package edu.ohiostate.udodge;

import android.*;
import android.Manifest;
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
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.graphics.Path.Direction.CCW;
import static android.graphics.Path.Direction.CW;

public class PictureActivity extends AppCompatActivity {

    private ImageView mAvatarPicture;
    private Button mRetakeButton;
    private Button mOkButton;
    private Button mCancelButton;
    private Intent retakeIntent;
    private static final int READ_CODE = 79;
    private static final int RETAKE_CODE = 88;
    private static final String TAG = "PictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        // obtain references to the buttons
        mRetakeButton = (Button) findViewById(R.id.retake_button);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);

        // button click listeners
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find the file and delete it
                String fileUri = getImageURI();
                File imgFile = new File(fileUri.substring(7));
                imgFile.delete();

                // fire the AvatarActivity to retake the picture
                Intent intent = new Intent(PictureActivity.this, AvatarActivity.class);
                startActivity(intent);

            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // declare the picture the accepted avatar pic
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("acceptedAvatar", true);
                editor.putString("real_avatar", getImageURI().substring(7));
                editor.commit();

                // go back to the home screen
                Intent intent = new Intent(PictureActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to the home screen
                Intent intent = new Intent(PictureActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // obtain the picture the user just took
        getPicture();
    }

    private void getPicture() {
        Log.d(TAG, "getPicture()");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                /*|| ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED*/) {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.MANAGE_DOCUMENTS}, READ_CODE);
            Log.d(TAG, "Permissions requested");
        } else {
            findPictureInStorage();
        }
    }

    private void findPictureInStorage() {
        String imgSrc = getImageURI();
        Log.d(TAG, "Retrieved URI: " + imgSrc);
        //Log.d(TAG, "Image size: " + imgSrc.toString());

        if (imgSrc != null) {
            File imgFile = new File(imgSrc.substring(7));
            Bitmap bits = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Log.d(TAG, imgFile.getAbsolutePath());

            mAvatarPicture = (ImageView) findViewById(R.id.avatar_picture);

            // get cropped image
            bits = getRoundedShape(bits);

            mAvatarPicture.setImageBitmap(bits);
            mAvatarPicture.setRotation(-90);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bits.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            try {
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.write(byteArray);
                fos.close();
            } catch (Exception e) {
                Log.e(TAG, "Error rewriting file: " + e.getStackTrace());
            }
        }
    }

    private String getImageURI() {
        Log.d(TAG, "Intent: " + getIntent().toString());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString("temp_avatar", null);
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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
        Log.d(TAG, Integer.toString(targetBitmap.getHeight()));
        return targetBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == READ_CODE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Proper permissions were granted.");
                getPicture();
            } else {
                Log.i(TAG, "Proper permissions were NOT granted.");
                Log.d(TAG, "Read permission granted? " +
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
                Log.d(TAG, "Manage Documents permission granted? " +
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) == PackageManager.PERMISSION_GRANTED));

            }
        }
    }
}



package edu.ohiostate.udodge;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import static android.graphics.Path.Direction.CCW;
import static android.graphics.Path.Direction.CW;

public class PictureActivity extends AppCompatActivity {

    private ImageView mAvatarPicture;
    private LinearLayout mContainer;
    private static final int READ_CODE = 79;
    private static final String TAG = "PictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

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
        Log.d(TAG, "Permissions have already been obtained");
        Bundle extras = getIntent().getExtras();
        Uri imgSrc = (Uri) extras.get("uri");
        //Log.d(TAG, "Image size: " + imgSrc.toString());

        File imgFile = new File(imgSrc.toString().substring(7));
        Bitmap bits = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        mAvatarPicture = (ImageView) findViewById(R.id.avatar_picture);

        // get cropped image
        bits = getRoundedShape(bits);

        mAvatarPicture.setImageBitmap(bits);
        mAvatarPicture.setRotation(-90);
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 800;
        int targetHeight = 800;

        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(
                ((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth - 65), ((float) targetHeight) - 65) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(
                sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
                        .getHeight()), new Rect(0, 0, targetWidth,
                        targetHeight), null);
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



package edu.ohiostate.udodge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new PlayFragment();
    }
}

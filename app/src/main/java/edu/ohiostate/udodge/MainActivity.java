package edu.ohiostate.udodge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MainActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new MainFragment();
    }
}

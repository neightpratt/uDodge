package edu.ohiostate.udodge;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Greg on 3/12/2018.
 */

public class uDodgeApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

package edu.ohiostate.udodge;

import android.support.v4.app.Fragment;

/**
 * Created by Greg on 3/12/2018.
 */

public class LeaderboardActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new LeaderboardFragment();
    }
}

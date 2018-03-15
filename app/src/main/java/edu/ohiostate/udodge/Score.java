package edu.ohiostate.udodge;

import java.util.Date;

/**
 * Created by Greg on 3/12/2018.
 */

public class Score {

    private String mUid;
    private String mName;
    private int mScore;
    private Date mDate;

    public Score(){
        mDate = new Date();
    }

    public String getUid(){
        return mUid;
    }

    public void setUid(String uid){
        mUid = uid;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public int getScore(){
        return mScore;
    }

    public void setScore(int score){
        mScore = score;
    }

    public Date getDate(){
        return mDate;
    }

    public void setDate(Date date){
        mDate = date;
    }
}

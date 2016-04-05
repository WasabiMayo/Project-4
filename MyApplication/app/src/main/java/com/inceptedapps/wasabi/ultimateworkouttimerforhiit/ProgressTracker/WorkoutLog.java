package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Wasabi on 3/31/2016.
 */
public class WorkoutLog extends RealmObject {
    private Date mDate;
    private String mDay;
    private long mDateId;
    private HiitTimerSet mTodaysWorkout;

    public WorkoutLog() {
    }

    public WorkoutLog(Date mDate, String mDay, long mDateId, HiitTimerSet mTodaysWorkout) {
        this.mDate = mDate;
        this.mDateId = mDateId;
        this.mDay = mDay;
        this.mTodaysWorkout = mTodaysWorkout;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public long getmDateId() {
        return mDateId;
    }

    public void setmDateId(long mDateId) {
        this.mDateId = mDateId;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public HiitTimerSet getmTodaysWorkout() {
        return mTodaysWorkout;
    }

    public void setmTodaysWorkout(HiitTimerSet mTodaysWorkout) {
        this.mTodaysWorkout = mTodaysWorkout;
    }
}

package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MyAnimation;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PreciseCountdown;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker.LogSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker.WorkoutLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HiitTimerActivity extends AppCompatActivity {

    //TODO Adjust timer when time is not provided

    //Declaring Views and the timer set object
    TextView mTimerMinsTextView, mTimerSecsTextView, mTotalTextView, mWorkoutTextView, mRepsTextView;
    ProgressBar mProgressBar;
    HiitTimerSet hiitTimerSet;

    //Flags
    public static final String HIIT_STATUS_WARMUP = "HIIT_WARM_UP";
    public static final String HIIT_STATUS_WORK = "HIIT_WORK";
    public static final String HIIT_STATUS_REST = "HIIT_REST";
    public static final String HIIT_STATUS_COOL_DOWN = "HIIT_COOL_DOWN";
    public static final String HIIT_STATUS_COMPLETE = "HIIT_COMPLETE";

    //Day
    private int prevDay;
    private int today;
    public static final String sharedPrefDayKey
            = "com.inceptedapps.wasabi.ultimateworkouttimerforhiit.TODAY_NAME";
    private SharedPreferences sharedPref;

    //Second variables.
    int warmup;
    int work;
    int rest;
    int reps;
    int cooldown;
    int total;

    int remainingWarmupSecs;
    int remainingWorkSecs;
    int remainingRestSecs;
    int remainingCooldownSecs;
    int remainingTotalSecs;

    int completedReps;

    //Timer components
    MyAnimation anim;
    PreciseCountdown warmupTimer, workTimer, restTimer, cooldownTimer;
    Handler handler = new Handler();
    Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer);

        initiateTimerData();
        initiateTimers();
        handler.post(timerRunnable);

        sharedPref =
                HiitTimerActivity.this.
                getSharedPreferences(sharedPrefDayKey,
                Context.MODE_PRIVATE);

        prevDay = sharedPref.getInt(sharedPrefDayKey,-1);
    }

    public void initiateTimerData(){
        mTimerMinsTextView = (TextView) findViewById(R.id.hiit_timer_mins_textView);
        mTimerSecsTextView = (TextView) findViewById(R.id.hiit_timer_seconds_textView);
        mTotalTextView = (TextView) findViewById(R.id.hiit_timer_total_textView);
        mWorkoutTextView = (TextView) findViewById(R.id.hiit_timer_workout_textView);
        mRepsTextView = (TextView) findViewById(R.id.hiit_timer_reps_textView);
        mProgressBar = (ProgressBar) findViewById(R.id.hiit_timer_progressBar);

        HiitSingleton hiitSingleton = HiitSingleton.getInstance();

        if(!getIntent().hasExtra("TIMER_SET_POSITION")) {
            hiitTimerSet = hiitSingleton.getTimers().get((hiitSingleton.getTimers().size() - 1));
        } else {
            hiitTimerSet = hiitSingleton.getTimers().get(getIntent().getIntExtra("TIMER_SET_POSITION",-1));
        }

        warmup = hiitTimerSet.getWarmup();
        work = hiitTimerSet.getWork();
        rest = hiitTimerSet.getRest();
        reps = hiitTimerSet.getReps();
        cooldown = hiitTimerSet.getCooldown();
        total = hiitTimerSet.getTotal();

        remainingWarmupSecs = warmup;
        remainingWorkSecs = work;
        remainingRestSecs = rest;
        remainingCooldownSecs = cooldown;
        remainingTotalSecs = total;

        completedReps = 0;

        anim = new MyAnimation(mProgressBar, total * 100, 0);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
    }

    public void initiateTimers(){

        warmupTimer = new PreciseCountdown(warmup * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("MILLIS_Warmup", "Tick: " + millisUntilFinished);
                updateUI(remainingWarmupSecs, HIIT_STATUS_WARMUP, null);
                remainingTotalSecs--;
                remainingWarmupSecs--;
            }
            @Override
            public void onFinished() {
                mProgressBar.setMax(work * 100);
                workTimer.start();
                completedReps++;
            }
        };

        workTimer = new PreciseCountdown(work * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("MILLIS_Work", "Tick: " + millisUntilFinished);
                updateUI(remainingWorkSecs, HIIT_STATUS_WORK, null);
                remainingTotalSecs--;
                remainingWorkSecs--;
            }

            @Override
            public void onFinished() {
                mProgressBar.setMax(rest * 100);
                restTimer.restart();
            }
        };

        restTimer = new PreciseCountdown(rest * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("MILLIS_rest", "Tick: " + millisUntilFinished);
                updateUI(remainingRestSecs, HIIT_STATUS_REST, null);
                remainingTotalSecs--;
                remainingRestSecs--;
            }
            @Override
            public void onFinished() {
                if (completedReps < reps) {
                    completedReps++;
                    mProgressBar.setMax(work * 100);
                    remainingWorkSecs = work;
                    remainingRestSecs = rest;
                    workTimer.stop();
                    workTimer.restart();
                    this.stop();
                } else {
                    mProgressBar.setMax(cooldown * 100);
                    cooldownTimer.start();
                }
            }
        };

        cooldownTimer = new PreciseCountdown(cooldown * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("MILLIS_Cooldown", "Tick: " + millisUntilFinished);
                updateUI(remainingCooldownSecs, HIIT_STATUS_COOL_DOWN, null);
                remainingTotalSecs--;
                remainingCooldownSecs--;
            }
            @Override
            public void onFinished() {
                Log.d("MILLIS_Complete", "complete");
                updateUI(0, HIIT_STATUS_COMPLETE, null);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String dayName = getTodaysName();
                        Date todaysDate = Calendar.getInstance().getTime();
                        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

                        RealmConfiguration realmConfig = new RealmConfiguration.Builder(HiitTimerActivity.this).deleteRealmIfMigrationNeeded().build();
                        Realm logRealm = Realm.getInstance(realmConfig);
                        RealmResults<WorkoutLog> workoutLogs = logRealm.where(WorkoutLog.class).findAll();

                        long dateId = -1L;
                        if(workoutLogs.size()==0){
                            dateId = 0;
                        } else {
                            WorkoutLog previousWorkout = workoutLogs.get(workoutLogs.size() - 1);
                            if(prevDay != today) {
                                dateId = previousWorkout.getmDateId() + 1;
                            } else {
                                dateId = previousWorkout.getmDateId();
                            }
                        }

                        HiitSingleton hiitS = HiitSingleton.getInstance();
                        HiitTimerSet completedTimerSet;
                        if(!getIntent().hasExtra("TIMER_SET_POSITION")) {
                            completedTimerSet = hiitS.getTimers().get((hiitS.getTimers().size() - 1));
                        } else {
                            completedTimerSet = hiitS.getTimers().get(getIntent().getIntExtra("TIMER_SET_POSITION",-1));
                        }
                        WorkoutLog workoutLog = new WorkoutLog(todaysDate, dayName, dateId, completedTimerSet);
                        logRealm.beginTransaction();
                        logRealm.copyToRealm(workoutLog);
                        logRealm.commitTransaction();

                        writeTodaysDayNameToSharedPref();
                    }
                });

            }
        };

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressBar.setMax(warmup * 100);
                warmupTimer.start();
            }
        };
    }

    private String getTodaysName() {
        Calendar c = Calendar.getInstance();
        String[] days = new  String[] { "index zero placeholder", "SUN", "MON", "TUE", "WED", "THURS", "FRI", "SAT" };
        return days[c.get(Calendar.DAY_OF_WEEK)];
    }

    private void writeTodaysDayNameToSharedPref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        editor.putInt(sharedPrefDayKey, today);
        editor.commit();
    }

    public void updateUI(final int remainingSeconds, final String currentStatus, final String workoutName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animateProgressBar(remainingSeconds);
                handleTimerUI(currentStatus, remainingSeconds, workoutName);
            }
        });
    }

    public void animateProgressBar(int time) {
        long fromTo = (long) time;
        anim.setFrom(fromTo * 100);
        anim.setTo((fromTo - 1) * 100);
        mProgressBar.startAnimation(anim);
    }

    public void handleTimerUI(String currentStatus, int remainingSeconds, String workoutName) {
        switch (currentStatus) {
            case HIIT_STATUS_WARMUP:
                mWorkoutTextView.setText("Warm up");
                break;
            case HIIT_STATUS_WORK:
                if (workoutName != null) {
                    mWorkoutTextView.setText(workoutName);
                } else {
                    mWorkoutTextView.setText("Work");
                }
                break;
            case HIIT_STATUS_REST:
                mWorkoutTextView.setText("Rest");
                break;
            case HIIT_STATUS_COOL_DOWN:
                mWorkoutTextView.setText("Cool down");
                break;
            case HIIT_STATUS_COMPLETE:
                mWorkoutTextView.setText("Complete!");
                break;
            default:
                break;
        }

        String[] currentMinSec = timeConverter(remainingSeconds);
        mTimerMinsTextView.setText(currentMinSec[0]);
        mTimerSecsTextView.setText(currentMinSec[1]);
        currentMinSec = null;

        String[] totalMinSec = timeConverter(remainingTotalSecs);
        String totalTime = totalMinSec[0] + ":" + totalMinSec[1];
        totalMinSec = null;


        mRepsTextView.setText(completedReps + "/" + reps);
        mTotalTextView.setText(totalTime);
    }

    private String[] timeConverter(int rawTotalSec) {

        int rawTotalMin = 0;

        if (rawTotalSec >= 60) {
            rawTotalMin = rawTotalMin + (int) TimeUnit.SECONDS.toMinutes(rawTotalSec);
            rawTotalSec = rawTotalSec - (int) TimeUnit.SECONDS.toMinutes(rawTotalSec) * 60;
        }

        String totalMin = String.valueOf(rawTotalMin);
        String totalSec = String.valueOf(rawTotalSec);

        if (rawTotalMin < 10) {
            totalMin = "0" + rawTotalMin;
        }
        if (rawTotalSec < 10) {
            totalSec = "0" + rawTotalSec;
        }
        return new String[]{totalMin, totalSec};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO Stop runnable
        handler.removeCallbacks(timerRunnable);
        warmupTimer.dispose();
    }
}

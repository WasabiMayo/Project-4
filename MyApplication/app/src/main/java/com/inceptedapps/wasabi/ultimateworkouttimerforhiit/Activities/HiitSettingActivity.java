package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HiitSettingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editWarmup, editWork, editRest, editReps, editCooldown;
    TextView startButtonTextView, totalTextView;
    ImageView warmupLeftArrow, warmupRightArrow,
            workLeftArrow, workRightArrow,
            restLeftArrow, restRightArrow,
            repsLeftArrow, repsRightArrow,
            cooldownLeftArrow, cooldownRightArrow;
    FloatingActionButton saveFab;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_setting);

        // ======================= Reference and toolbar setting ===========================

        Toolbar hiitTimerSetToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerSetToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(hiitTimerSetToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editWarmup = (EditText) findViewById(R.id.hitt_timer_setting_warmup_edit);
        editWork = (EditText) findViewById(R.id.hitt_timer_setting_work_edit);
        editRest = (EditText) findViewById(R.id.hitt_timer_setting_rest_edit);
        editReps = (EditText) findViewById(R.id.hitt_timer_setting_reps_edit);
        editCooldown = (EditText) findViewById(R.id.hitt_timer_setting_cooldown_edit);

        startButtonTextView = (TextView) findViewById(R.id.hiit_timer_setting_start_button);
        totalTextView = (TextView) findViewById(R.id.hiit_timer_setting_total_text);

        warmupLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_warmup_left_arrow);
        warmupRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_warmup_right_arrow);
        workLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_work_left_arrow);
        workRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_work_right_arrow);
        restLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_rest_left_arrow);
        restRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_rest_right_arrow);
        repsLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_reps_left_arrow);
        repsRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_reps_right_arrow);
        cooldownLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_cooldown_left_arrow);
        cooldownRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_cooldown_right_arrow);

        saveFab = (FloatingActionButton)findViewById(R.id.hiit_timer_setting_save_button);
        frameLayout = (FrameLayout)findViewById(R.id.hiit_timer_setting_parent_framelayout);

        // ========================== Time trimming =================================

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText currentEditText = (EditText) v;
                String[] minAndSec = currentEditText.getText().toString().split(":");
                String minSec = trimTimeString(minAndSec);
                currentEditText.setText(minSec);


                totalTextView.setText(calculateTotalTime());

            }
        };
        editWarmup.setOnFocusChangeListener(focusChangeListener);
        editWork.setOnFocusChangeListener(focusChangeListener);
        editRest.setOnFocusChangeListener(focusChangeListener);
        editCooldown.setOnFocusChangeListener(focusChangeListener);

        warmupLeftArrow.setOnClickListener(this);
        warmupRightArrow.setOnClickListener(this);
        workLeftArrow.setOnClickListener(this);
        workRightArrow.setOnClickListener(this);
        restLeftArrow.setOnClickListener(this);
        restRightArrow.setOnClickListener(this);
        repsLeftArrow.setOnClickListener(this);
        repsRightArrow.setOnClickListener(this);
        cooldownLeftArrow.setOnClickListener(this);
        cooldownRightArrow.setOnClickListener(this);
        saveFab.setOnClickListener(this);


        // ====================== Passing data to service ==================================

        startButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trimAllEditText();

                int warmupTime = timeConverter(editWarmup.getText().toString());
                int workTime = timeConverter(editWork.getText().toString());
                int restTime = timeConverter(editRest.getText().toString());
                int repsTime = Integer.parseInt(editReps.getText().toString());
                int cooldownTime = timeConverter(editCooldown.getText().toString());
                int totalTime = timeConverter(calculateTotalTime());

                HiitSingleton.getInstance().addTimer(new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime, totalTime));

                Intent intent = new Intent(HiitSettingActivity.this, HiitTimerActivity.class);
                startActivity(intent);
            }
        });


    }

    private int timeConverter(String rawTime) {
        String[] rawMinSec = rawTime.split(":");
        int rawMin = Integer.parseInt(rawMinSec[0]);
        int rawSec = Integer.parseInt(rawMinSec[1]);
        int minIntoSec = (int) TimeUnit.MINUTES.toSeconds(rawMin);
        rawMinSec = null;

        return minIntoSec + rawSec;
    }

    private String[] timeConverterToString(int rawTotalSec) {

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

    private String calculateTotalTime() {
        String[] rawWarmupMinSec = editWarmup.getText().toString().split(":");
        if (rawWarmupMinSec.length == 1) {
            rawWarmupMinSec = timeConverterToString(Integer.parseInt(rawWarmupMinSec[0]));
        }
        int rawWarmupMin = Integer.valueOf(rawWarmupMinSec[0]);
        int rawWarmupSec = Integer.valueOf(rawWarmupMinSec[1]);
        rawWarmupMinSec = null;

        String[] rawWorkMinSec = editWork.getText().toString().split(":");
        if (rawWorkMinSec.length == 1) {
            rawWorkMinSec = timeConverterToString(Integer.parseInt(rawWorkMinSec[0]));
        }
        int rawWorkMin = Integer.valueOf(rawWorkMinSec[0]);
        int rawWorkSec = Integer.valueOf(rawWorkMinSec[1]);
        rawWorkMinSec = null;

        String[] rawRestMinSec = editRest.getText().toString().split(":");
        if (rawRestMinSec.length == 1) {
            rawWarmupMinSec = timeConverterToString(Integer.parseInt(rawRestMinSec[0]));
        }
        int rawRestMin = Integer.valueOf(rawRestMinSec[0]);
        int rawRestSec = Integer.valueOf(rawRestMinSec[1]);
        rawRestMinSec = null;

        String[] rawCooldownMinSec = editCooldown.getText().toString().split(":");
        if (rawCooldownMinSec.length == 1) {
            rawCooldownMinSec = timeConverterToString(Integer.parseInt(rawCooldownMinSec[0]));
        }
        int rawCooldownMin = Integer.valueOf(rawCooldownMinSec[0]);
        int rawCooldownSec = Integer.valueOf(rawCooldownMinSec[1]);
        rawCooldownMinSec = null;

        int reps = Integer.parseInt(editReps.getText().toString());

        int rawTotalMin = rawWarmupMin + ((rawWorkMin + rawRestMin) * reps) + rawCooldownMin;
        int rawTotalSec = rawWarmupSec + ((rawWorkSec + rawRestSec) * reps) + rawCooldownSec;

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

        return totalMin + ":" + totalSec;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hitt_timer_setting_warmup_left_arrow:
                editWarmup.clearFocus();
                controlArrowAction(editWarmup, 0);
                break;
            case R.id.hitt_timer_setting_warmup_right_arrow:
                editWarmup.clearFocus();
                controlArrowAction(editWarmup, 1);
                break;
            case R.id.hitt_timer_setting_work_left_arrow:
                controlArrowAction(editWork, 0);
                break;
            case R.id.hitt_timer_setting_work_right_arrow:
                controlArrowAction(editWork, 1);
                break;
            case R.id.hitt_timer_setting_rest_left_arrow:
                controlArrowAction(editRest, 0);
                break;
            case R.id.hitt_timer_setting_rest_right_arrow:
                controlArrowAction(editRest, 1);
                break;
            case R.id.hitt_timer_setting_reps_left_arrow:
                controlArrowAction(editReps, 3);
                break;
            case R.id.hitt_timer_setting_reps_right_arrow:
                controlArrowAction(editReps, 4);
                break;
            case R.id.hitt_timer_setting_cooldown_left_arrow:
                controlArrowAction(editCooldown, 0);
                break;
            case R.id.hitt_timer_setting_cooldown_right_arrow:
                controlArrowAction(editCooldown, 1);
                break;
            case R.id.hiit_timer_setting_save_button:
                saveThisPreset();
                break;
            default:
                break;
        }
    }

    public void saveThisPreset(){
        trimAllEditText();

        int warmupTime = timeConverter(editWarmup.getText().toString());
        int workTime = timeConverter(editWork.getText().toString());
        int restTime = timeConverter(editRest.getText().toString());
        int repsTime = Integer.parseInt(editReps.getText().toString());
        int cooldownTime = timeConverter(editCooldown.getText().toString());
        int totalTime = timeConverter(calculateTotalTime());

        HiitTimerSet newTimerSet = new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime, totalTime);

        HiitSingleton.getInstance().addTimer(newTimerSet);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(HiitSettingActivity.this).deleteRealmIfMigrationNeeded().build();
        Realm timerRealm = Realm.getInstance(realmConfig);
        timerRealm.beginTransaction();
        timerRealm.copyToRealm(newTimerSet);
        timerRealm.commitTransaction();

        Snackbar.make(frameLayout,"Saved this preset", Snackbar.LENGTH_LONG).show();
    }

    private void controlArrowAction(View view, int flag) {
        EditText currentEditText = (EditText) view;
        String currentTime = currentEditText.getText().toString();
        String[] currentMinSec = currentTime.split(":");
        if (currentMinSec.length == 2) {
            if (flag == 1) { // if the user clicked right arrow, add 1 more second
                if (currentMinSec[1].equals("59")) {
                    currentMinSec[1] = "00";
                    currentMinSec[0] = String.valueOf(Integer.parseInt(currentMinSec[0]) + 1);
                } else {
                    currentMinSec[1] = String.valueOf(Integer.parseInt(currentMinSec[1]) + 1);
                }
                String trimmedMinSec = trimTimeString(currentMinSec);
                ((EditText) view).setText(trimmedMinSec);
            } else if (flag == 0) { // If the user clicked left arrow, subtract 1 second from the original time
                if (!currentTime.equals("00:00")) {
                    if (currentMinSec[1].equals("00")) {
                        currentMinSec[1] = "59";
                        currentMinSec[0] = String.valueOf(Integer.parseInt(currentMinSec[0]) - 1);
                    } else {
                        currentMinSec[1] = String.valueOf(Integer.parseInt(currentMinSec[1]) - 1);
                    }
                    String trimmedMinSec = trimTimeString(currentMinSec);
                    ((EditText) view).setText(trimmedMinSec);
                }
            }
            currentMinSec = null;
        } else {
            if (flag == 3) { // If the user clicked the left arrow of the reps EditText, subtract 1 second from the original reps
                if (!currentTime.equals("0")) {
                    String currentReps = String.valueOf(Integer.parseInt(currentTime) - 1);
                    currentEditText.setText(currentReps);
                }
            } else if (flag == 4) { // If the user clicked the left arrow of the reps EditText, add 1 more rep
                String currentReps = String.valueOf(Integer.parseInt(currentTime) + 1);
                currentEditText.setText(currentReps);
            } else {
                currentEditText.setText("00:00");
            }
            trimAllEditText();
            totalTextView.setText(calculateTotalTime());
        }
        currentEditText = null;
    }

    private void trimAllEditText() {
        editWarmup.setText(trimTimeString(editWarmup.getText().toString().split(":")));
        editWork.setText(trimTimeString(editWork.getText().toString().split(":")));
        editRest.setText(trimTimeString(editRest.getText().toString().split(":")));
        editCooldown.setText(trimTimeString(editCooldown.getText().toString().split(":")));
    }

    private String trimTimeString(String[] minAndSec) {
        int min = 0;
        int sec = 0;
        if (minAndSec.length == 2) {
            if (minAndSec[0].isEmpty()) {
                minAndSec[0] = "00";
            }
            min = Integer.parseInt(minAndSec[0]);
            sec = Integer.parseInt(minAndSec[1]);
        } else if (minAndSec.length == 1) {
            if(!(minAndSec[0].isEmpty())) {
                sec = Integer.parseInt(minAndSec[0]);
            }
        }
        minAndSec = null;
        String minString = "00";
        String secString = "00";


        ////TODO 1. Text should be changed when the second is same or more than 60. ( 1:70 -> 2:10 )
        if (sec >= 60) {
            int oldMin = min;
            min = (int) (TimeUnit.SECONDS.toMinutes(sec));
            sec = (int) (sec - TimeUnit.MINUTES.toSeconds(min));
            min = min + oldMin;
        }

        //TODO 2. Text should be changed when the like these ( 1:03->01:03, 01:3->01:03, 3->00:03, :3 -> 00:03 )
        if (min < 10) {
            minString = "0" + min;
        } else {
            minString = String.valueOf(min);
        }
        if (sec < 10) {
            secString = "0" + sec;
        } else {
            secString = String.valueOf(sec);
        }

        return minString + ":" + secString;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}


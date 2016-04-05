package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerListActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker.ProgressActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mHiitButton, mPresetLoadButton, mTrackProgressButton;
    TextView mGreetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(MainActivity.this).deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<HiitTimerSet> timerSets = realm.where(HiitTimerSet.class).findAll();
        HiitSingleton.getInstance().getTimers().addAll(timerSets);

        mHiitButton = (Button)findViewById(R.id.main_hitt_timer_button);
        mPresetLoadButton = (Button)findViewById(R.id.main_load_preset_button);
        mTrackProgressButton = (Button)findViewById(R.id.main_track_progress_button);

        mHiitButton.setOnClickListener(this);
        mPresetLoadButton.setOnClickListener(this);
        mTrackProgressButton.setOnClickListener(this);

        mGreetingText = (TextView)findViewById(R.id.main_greeting_text);

        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        if(time<=10 & time>=6){
            mGreetingText.setText("Good morning!");
        }else if(time<=13){
            mGreetingText.setText("Start running");
        }else if(time<=18){
            mGreetingText.setText("How's it going today?");
        }else if(time<=20){
            mGreetingText.setText("Run! Before the sun goes away :)");
        }else if(time<=24){
            mGreetingText.setText("Good night. You were awesome today.");
        }else if(time>0 && time<6){
            mGreetingText.setText("Can't sleep?");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_hitt_timer_button:
                Intent timerIntent = new Intent(MainActivity.this, HiitSettingActivity.class);
                startActivity(timerIntent);
                break;
            case R.id.main_load_preset_button:
                Intent presetListIntent = new Intent(MainActivity.this, HiitTimerListActivity.class);
                startActivity(presetListIntent);
                break;
            case R.id.main_track_progress_button:
                Intent calendarIntent = new Intent(MainActivity.this, ProgressActivity.class);
                startActivity(calendarIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        HiitSingleton.getInstance().clearTimerList();
        AppEventsLogger.deactivateApp(this);
        super.onDestroy();
    }
}

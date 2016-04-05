package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ProgressActivity extends AppCompatActivity implements OnChartValueSelectedListener{
    private LineChart mLineChart;
    public static final String[] days = new String[]{"Index zero placeholder", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private int index = -1;
    private TextView mDateTextView, mSprintTextView, mRestTextView, mTotalTextView, mNoResultTextView;
    private ArrayList<RealmResults<WorkoutLog>> logResultList;
    private Button mShareButton;

    private CallbackManager callbackManager;
    private LoginManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar hiitTimerListToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerListToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        hiitTimerListToolbar.setTitle("HIIT Timer List");
        setSupportActionBar(hiitTimerListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.inceptedapps.wasabi.ultimateworkouttimerforhiit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mDateTextView = (TextView) findViewById(R.id.progress_date_text_view);
        mSprintTextView = (TextView) findViewById(R.id.progress_sprint_text_view);
        mRestTextView = (TextView) findViewById(R.id.progress_rest_text_view);
        mTotalTextView = (TextView) findViewById(R.id.progress_total_text_view);
        mNoResultTextView = (TextView) findViewById(R.id.progress_no_result_text_view);

        mLineChart = (LineChart) findViewById(R.id.progress_line_chart);
        mShareButton = (Button) findViewById(R.id.share_button);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callbackManager = CallbackManager.Factory.create();
                List<String> permissionNeeds = Arrays.asList("publish_actions");
                manager = LoginManager.getInstance();
                manager.logInWithPublishPermissions(ProgressActivity.this, permissionNeeds);
                manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        publishImage();
                    }

                    @Override
                    public void onCancel()
                    {
                        Toast.makeText(ProgressActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        exception.printStackTrace();
                    }
                });
            }
        });

        mLineChart.setViewPortOffsets(0, 0, 0, 0);
        mLineChart.setBackgroundColor(Color.rgb(34, 190, 214));
        mLineChart.setDescription("");
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(false);
        mLineChart.setDrawGridBackground(false);

        XAxis x = mLineChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setTextColor(Color.WHITE);
        x.setAvoidFirstLastClipping(true);
        x.setDrawGridLines(false);

        YAxis y = mLineChart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        mLineChart.getAxisRight().setEnabled(false);

        // add data
        setData(7);

        mLineChart.getLegend().setEnabled(false);
        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.animateXY(2000, 2000);
        mLineChart.invalidate();
    }

    private void publishImage() {
        Bitmap bitmap = mLineChart.getChartBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(ProgressActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(ProgressActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(ProgressActivity.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private String getTodaysDate() {
        Date rawDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy");
        return dateFormat.format(rawDate);
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode,    Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.d("VALUE SELECTED", "Selected value: "+e.getVal());
        Log.d("VALUE SELECTED", "Selected index: "+dataSetIndex);

        String value = String.valueOf((int)e.getVal());
        if(logResultList.get(e.getXIndex()).size() != 0) {
            mNoResultTextView.setVisibility(View.GONE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String date = simpleDateFormat.format((logResultList.get(e.getXIndex())).get(0).getmDate());
            int sprint = 0;
            int rest = 0;
            int total = 0;
            for (int i = 0; i < logResultList.get(e.getXIndex()).size(); i++) {
                sprint += (logResultList.get(e.getXIndex())).get(i).getmTodaysWorkout().getWork();
                rest += (logResultList.get(e.getXIndex())).get(i).getmTodaysWorkout().getRest();
                total += (logResultList.get(e.getXIndex())).get(i).getmTodaysWorkout().getTotal();
            }
            mDateTextView.setText(date);
            mTotalTextView.setText(timeConverterToString(total));
            mSprintTextView.setText(timeConverterToString(sprint));
            mRestTextView.setText(timeConverterToString(rest));
        }else{
            mNoResultTextView.setVisibility(View.VISIBLE);
            mNoResultTextView.setText("No workout log for this day :(");
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private String timeConverterToString(int rawTotalSec) {

        int rawTotalMin = 0;

        if (rawTotalSec >= 60) {
            rawTotalMin = rawTotalMin + (int) TimeUnit.SECONDS.toMinutes(rawTotalSec);
            rawTotalSec = rawTotalSec - (int) TimeUnit.SECONDS.toMinutes(rawTotalSec) * 60;
        }

        String totalMin = String.valueOf(rawTotalMin);
        String totalSec = String.valueOf(rawTotalSec);

        if (rawTotalSec < 10) {
            totalSec = "0" + rawTotalSec;
        }
        return totalMin+ ":" + totalSec;
    }

    public void setData(int count){

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(ProgressActivity.this).deleteRealmIfMigrationNeeded().build();
        Realm logRealm = Realm.getInstance(realmConfig);
        RealmResults<WorkoutLog> results = logRealm.where(WorkoutLog.class).findAll();
        for (int i = 0; i < results.size(); i++) {

            Log.d("FOUND DATA", "Date ID : " + results.get(i).getmDateId() + ", Date : " + results.get(i).getmDate() +
                    ", Workout details : "+ results.get(i).getmTodaysWorkout().getTotal() + ", Day: " + results.get(i).getmDay());

        }
        if(results.size() == 0){
            return;
        }else {
        long latestDateId = results.get(results.size()-1).getmDateId();
        RealmResults<WorkoutLog> workoutLogsOfTheDay = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay1 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-1).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay2 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-2).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay3 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-3).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay4 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-4).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay5 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-5).findAll();
        RealmResults<WorkoutLog> workoutLogsOfPrevDay6 = logRealm.where(WorkoutLog.class).equalTo("mDateId",latestDateId-6).findAll();

        logResultList = new ArrayList<>();
        logResultList.add(workoutLogsOfPrevDay6);
        logResultList.add(workoutLogsOfPrevDay5);
        logResultList.add(workoutLogsOfPrevDay4);
        logResultList.add(workoutLogsOfPrevDay3);
        logResultList.add(workoutLogsOfPrevDay2);
        logResultList.add(workoutLogsOfPrevDay1);
        logResultList.add(workoutLogsOfTheDay);


            ArrayList<String> xVals = new ArrayList<>();
            xVals.add(getXvals(workoutLogsOfPrevDay6, 6));
            xVals.add(getXvals(workoutLogsOfPrevDay5, 5));
            xVals.add(getXvals(workoutLogsOfPrevDay4, 4));
            xVals.add(getXvals(workoutLogsOfPrevDay3, 3));
            xVals.add(getXvals(workoutLogsOfPrevDay2, 2));
            xVals.add(getXvals(workoutLogsOfPrevDay1, 1));
            xVals.add(getXvals(workoutLogsOfTheDay, 0));

            ArrayList<Entry> vals1 = new ArrayList<>();
            vals1.add(getYVals(workoutLogsOfPrevDay6));
            vals1.add(getYVals(workoutLogsOfPrevDay5));
            vals1.add(getYVals(workoutLogsOfPrevDay4));
            vals1.add(getYVals(workoutLogsOfPrevDay3));
            vals1.add(getYVals(workoutLogsOfPrevDay2));
            vals1.add(getYVals(workoutLogsOfPrevDay1));
            vals1.add(getYVals(workoutLogsOfTheDay));


            LineDataSet set1 = new LineDataSet(vals1, "Total workout time"); set1.setDrawCubic(true);
            set1.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            LineData data = new LineData(xVals, set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            mLineChart.setData(data);
        }

    }

    private String getXvals(RealmResults<WorkoutLog> workoutLogs, int dayNum) {
        if(workoutLogs.size() != 0){
            return workoutLogs.get(0).getmDay();
        }else{
            return calculateDayName(dayNum);
        }
    };

    private Entry getYVals(RealmResults<WorkoutLog> workoutLogs) {
        long totalWorkoutTimeOfTheDay = 0L;
        index += 1;
        if(workoutLogs.size() != 0) {
            for (int i = 0; i < workoutLogs.size(); i++) {
                totalWorkoutTimeOfTheDay += workoutLogs.get(i).getmTodaysWorkout().getTotal();
                Log.d("ADDED DATA : ", "Date ID : " + workoutLogs.get(i).getmDateId() + ", Date : " + workoutLogs.get(i).getmDate() +
                ", Workout details : "+ workoutLogs.get(i).getmTodaysWorkout().getTotal() + ", Day: " + workoutLogs.get(i).getmDay());
            }
            return new Entry(totalWorkoutTimeOfTheDay, index);
        } else {
            return new Entry(0, index);
        }
    }

    private String calculateDayName(int dayNum){
        Calendar c = Calendar.getInstance();
        int todayInt = c.get(Calendar.DAY_OF_WEEK);

        if(todayInt-dayNum < 1){
            return days[7+(todayInt-dayNum)];
        }else {
            return days[todayInt - dayNum];
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}

package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import io.realm.RealmConfiguration;

public class HiitTimerListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HiitPresetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer_list);

        Toolbar hiitTimerListToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerListToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        hiitTimerListToolbar.setTitle("HIIT Timer List");
        setSupportActionBar(hiitTimerListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView)findViewById(R.id.hiit_timer_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mAdapter = new HiitPresetAdapter(this, HiitSingleton.getInstance().getTimers());
        mRecyclerView.setAdapter(mAdapter);
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

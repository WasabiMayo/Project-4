package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.HiitTimerActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.MainActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wasabi on 3/30/2016.
 */
public class HiitPresetAdapter extends RecyclerView.Adapter<HiitPresetAdapter.ViewHolder>{

    private ArrayList<HiitTimerSet> mPresets;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView timerNameTextView, warmupTimeTextView, workTimeTextView, restTimeTextView,
                repsTimeTextView, cooldownTimeTextView, totalTimeTextView;
        ImageView deleteIcon;
        Button startButton;


        public ViewHolder(View itemView) {
            super(itemView);

            //TODO Complete the constructor!
            timerNameTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_timer_name);
            warmupTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_warmup_time);
            workTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_work_time);
            restTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_rest_time);
            repsTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_rounds);
            cooldownTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_cooldown_time);
            totalTimeTextView = (TextView)itemView.findViewById(R.id.hiit_timer_list_total_time);

            deleteIcon = (ImageView) itemView.findViewById(R.id.hiit_timer_list_delete_button);
            startButton = (Button) itemView.findViewById(R.id.hiit_timer_list_start_button);
        }
    }

    public HiitPresetAdapter(Context context, ArrayList<HiitTimerSet> mPresets) {
        this.context = context;
        this.mPresets = mPresets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_list_card_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        HiitTimerSet timerSet = mPresets.get(position);

        holder.timerNameTextView.setText(timerSet.getTimerName());
        holder.warmupTimeTextView.setText(timeConverterToString(timerSet.getWarmup()));
        holder.workTimeTextView.setText(timeConverterToString(timerSet.getWork()));
        holder.restTimeTextView.setText(timeConverterToString(timerSet.getRest()));
        holder.repsTimeTextView.setText(String.valueOf(timerSet.getReps()));
        holder.totalTimeTextView.setText(timeConverterToString(timerSet.getTotal()));

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresets.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        holder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(context, HiitTimerActivity.class);
                startIntent.putExtra("TIMER_SET_POSITION",holder.getAdapterPosition());
                context.startActivity(startIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPresets.size();
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


}

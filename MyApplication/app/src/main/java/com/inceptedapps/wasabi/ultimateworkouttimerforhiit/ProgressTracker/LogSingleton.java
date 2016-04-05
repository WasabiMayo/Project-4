package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitSingleton;

import java.util.ArrayList;

/**
 * Created by Wasabi on 3/31/2016.
 */
public class LogSingleton {
    private static LogSingleton instance;
    private static ArrayList<WorkoutLog> logs;

    private LogSingleton() {
        this.logs = new ArrayList<WorkoutLog>();
    }

    public static LogSingleton getInstance(){
        if(instance == null){
            instance = new LogSingleton();
        }
        return instance;
    }


    public ArrayList<WorkoutLog> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<WorkoutLog> logs) {
        LogSingleton.logs = logs;
    }

    public void addLog(WorkoutLog workoutLog){
        logs.add(workoutLog);
    }
}

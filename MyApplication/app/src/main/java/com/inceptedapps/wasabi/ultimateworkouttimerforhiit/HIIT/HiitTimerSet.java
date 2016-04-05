package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by Wasabi on 3/25/2016.
 */
@RealmClass
public class HiitTimerSet extends RealmObject{

    //TODO Make timer name dialog and revise the constructor!

    private String timerName;
    private int warmup;
    private int work;
    private int rest;
    private int reps;
    private int cooldown;
    private int total;

    public HiitTimerSet() {
    }

    public HiitTimerSet(int warmup, int work, int rest, int reps, int cooldown, int total) {
        this.warmup = warmup;
        this.work = work;
        this.rest = rest;
        this.reps = reps;
        this.cooldown = cooldown;
        this.total = total;
        this.timerName = "Placeholder";
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getWarmup() {
        return warmup;
    }

    public void setWarmup(int warmup) {
        this.warmup = warmup;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

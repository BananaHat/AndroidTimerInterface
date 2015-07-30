package com.httpeffectivemobilesolutions.timerinterface.model;

import android.os.CountDownTimer;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Gabriel on 7/9/2015.
 * This is a model class the will represent an Arduino timer.
 */
public class TimerModel {

    private static final String TAG = TimerModel.class.getSimpleName();
    /**
     * Static global to hold all the timers in our session
     */
    private static ArrayList<TimerModel> allTimers = new ArrayList();
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of minuets the relay will be on.
     */
    int onMin;
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of 10ths of seconds the relay will be on after the minuets expire.
     */
    int onSec;
    long onMillis;
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of minuets the relay will be off.
     */
    int offMin;
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of 10ths of seconds the relay will be off after the minuets expire.
     */
    int offSec;
    long offMillis;
    /**
     * This tells us how many minuets are left in the current cycle.
     */
    int leftMin;
    /**
     * This tells us how many 10ths of seconds are left in the current cycle.
     */
    int leftSec;
    long leftMillis;
    /**
     * Tells ust the state of the relay 1 is on 0 is off.
     */
    int relayState;
    /**
     * Id of the timer. Hopefully we can make it a uuid.
     */
    String id;
    /**
     * Name of the timer, it can be set by the user.
     */
    String name;
    /**
     * Current ip address of the timer.
     */
    InetAddress address;

    /**
     * time when the timer was last updated
     */
    long timeCaptured;

    CountDownTimer timeLeft;

    /**
     * Creates a new Timer model from a JSON string.
     * @param jsonString
     */
    public TimerModel(String jsonString, InetAddress address, Handler handler){
        this.address = address;
        try {

            timeCaptured = System.currentTimeMillis();
            JSONObject json = new JSONObject(jsonString);
            JSONObject vars = json.getJSONObject("variables");
            onMin = vars.getInt("onmin");
            onSec = vars.getInt("onsec");
            onMillis = (onMin * 60 * 1000) + onSec * 10;
            offMin = vars.getInt("offmin");
            offSec = vars.getInt("offsec");
            offMillis = (offMin *60 * 1000 ) + offSec * 10;
            leftMin = vars.getInt("LeftMin");
            leftSec = vars.getInt("LeftSec");
            leftMillis = (leftMin * 60 * 1000) + leftSec * 10;
            relayState = vars.getInt("relaystate");
            id = json.getString("id");
            name = json.getString("name");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    timeLeft = new LeftTimer(leftMillis, 100);
                    timeLeft.start();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getRealyState(){
        if( relayState == 1){
            return "On";
        }else {
            return "Off";
        }
    }

    public String getName(){
        return name;
    }

    public String getLeftTime(){
        return String.format("%1$d:%2$02d:%3$d", leftMin, leftSec/10, leftSec%10);
    }

    public String getOnTime(){
        return String.format("%1$d:%2$02d:%3$d", onMin, onSec/10, onSec%10);
    }

    public String getOffTime(){
        return String.format("%1$d:%2$02d:%3$d", offMin, offSec/10, offSec%10);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Adds a timer to the global session list of timers .
     * @param timer
     */
    public static void addTimer(TimerModel timer){
        if(!allTimers.contains(timer)){
            allTimers.add(timer);
        }
    }

    public static ArrayList<TimerModel> getTimers(){
        return allTimers;
    }

    public static ArrayList getTimerNames(){
        return allTimers;
    }

    private class LeftTimer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and
         *                          {@link #onFinish()} is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public LeftTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftMillis = millisUntilFinished;
            leftMin = (int)(leftMillis/60)/1000;
            leftSec = (int)(leftMillis/100)%600;
        }

        @Override
        public void onFinish() {

            if(relayState == 1){
                relayState = 0;
                timeLeft = new LeftTimer(offMillis, 100);
                timeLeft.start();
            }else {
                relayState = 1;
                timeLeft = new LeftTimer(onMillis, 100);
                timeLeft.start();
            }
        }
    }
}

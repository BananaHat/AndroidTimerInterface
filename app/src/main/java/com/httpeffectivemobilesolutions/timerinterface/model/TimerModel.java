package com.httpeffectivemobilesolutions.timerinterface.model;

import org.json.JSONException;
import org.json.JSONObject;

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
     * This is the number of minuets the relay will be off.
     */
    int offMin;
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of 10ths of seconds the relay will be on after the minuets expire.
     */
    int onSec;
    /**
     * Since the Arduino can only output integer variables the time needs to be split between
     * tenths seconds and minuets.
     *
     * This is the number of 10ths of seconds the relay will be off after the minuets expire.
     */
    int offSec;
    /**
     * This tells us how many minuets are left in the current cycle.
     */
    int leftMin;
    /**
     * This tells us how many 10ths of seconds are left in the current cycle.
     */
    int leftSec;
    /**
     * tells ust the state of the relay 1 is on 0 is off.
     */
    int relayState;
    String id, name;


    /**
     * Creates a new Timer model from a JSON string.
     * @param jsonString
     */
    public TimerModel(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject vars = json.getJSONObject("variables");
            onMin = vars.getInt("onmin");
            offMin = vars.getInt("offmin");
            onSec = vars.getInt("onsec");
            offSec = vars.getInt("offsec");
            leftMin = vars.getInt("LeftMin");
            leftSec = vars.getInt("LeftSec");
            relayState = vars.getInt("relaystate");
            id = json.getString("id");
            name = json.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}

package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/25/2015.
 */
public class FullHandsetReport {
    public int id;
    public String user;
    public String model;
    public String serial;
    public String location;
    public String date;
    public int physicalDamage;
    public int physicalRepair;
    public int lowBattery;
    public int changeBattery;
    public int lowLiquid;
    public int changeLiquid;
    public int trayClean;
    public int machineClean;
    public String comment;
    public int state;

    public FullHandsetReport(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.user = object.getString("user_name");
            this.location = object.getString("location");
            this.date = object.getString("reported_at");
            this.physicalDamage = object.getInt("physicalDamage");
            this.physicalRepair = object.getInt("physicalRepair");
            this.lowBattery = object.getInt("lowBattery");
            this.changeBattery = object.getInt("changeBattery");
            this.lowLiquid = object.getInt("lowLiquid");
            this.changeLiquid = object.getInt("changeLiquid");
            this.trayClean = object.getInt("trayClean");
            this.machineClean = object.getInt("machineClean");
            this.comment = object.getString("comment");
            this.state = object.getInt("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<FullHandsetReport> fromJson(JSONArray jsonObjects) {
        ArrayList<FullHandsetReport> handset = new ArrayList<FullHandsetReport>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                handset.add(new FullHandsetReport(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return handset;
    }
}

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
    public String physicalDamage;
    public String physicalRepair;
    public String lowBattery;
    public String changeBattery;
    public String lowLiquid;
    public String changeLiquid;
    public String comment;
    public String state;

    public FullHandsetReport(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.user = object.getString("user_name");
            this.model = object.getString("model");
            this.serial = object.getString("serial_number");
            this.location = object.getString("location");
            this.date = object.getString("created_at");
            this.physicalDamage = object.getString("physicalDamage");
            this.physicalRepair = object.getString("physicalRepair");
            this.lowBattery = object.getString("lowBattery");
            this.changeBattery = object.getString("changeBattery");
            this.lowLiquid = object.getString("lowLiquid");
            this.changeLiquid = object.getString("changeLiquid");
            this.comment = object.getString("comment");
            this.state = object.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<FullHandsetReport> fromJson(JSONArray jsonObjects) {
        ArrayList<FullHandsetReport> handset = new ArrayList<>();
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

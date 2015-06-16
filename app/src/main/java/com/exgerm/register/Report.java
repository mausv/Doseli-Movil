package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mauricio on 11/06/2015.
 */
public class Report {
    public int id;
    public String username;
    public String date;
    public String status;

    public Report(int id, String name){
        this.id = id;
        this.username = name;
        this.date = date;
        this.status = status;
    }

    public Report(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.username = object.getString("user_name");
            this.date = object.getString("created_at");
            this.status = object.getString("comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Report(String username, String date, String status) {
        this.username = username;
        this.date = date;
        this.status = status;
    }

    public static ArrayList<Report> fromJson(JSONArray jsonObjects) {
        ArrayList<Report> reports = new ArrayList<Report>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                reports.add(new Report(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reports;
    }

    public String getId () {
        return String.valueOf(this.id);
    }
}

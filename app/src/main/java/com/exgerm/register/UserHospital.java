package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mausv on 12/3/2015.
 */
public class UserHospital {

    public int id;
    public String name;
    public String frequence;
    public int total;
    public int checked;

    public UserHospital(int id, String name, String frequence, int total, int checked) {
        this.id = id;
        this.name = name;
        this.frequence = frequence;
        this.total = total;
        this.checked = checked;
    }

    public UserHospital(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.frequence = object.getString("frequence");
            this.total = object.getInt("reports");
            this.checked = object.getInt("checked_reports");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<UserHospital> fromJson(JSONArray jsonObjects) {
        ArrayList<UserHospital> userHospital = new ArrayList<>();
        for(int i = 0; i < jsonObjects.length(); i++) {
            try {
                userHospital.add(new UserHospital(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return userHospital;
    }

}

package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/27/2015.
 */
public class AdmHospital {
    public int id;
    public String name;
    public String checked_15;
    public String missing_15;
    public String checked_30;
    public String missing_30;

    public AdmHospital (JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.checked_15 = object.getString("checked_15");
            this.missing_15 = object.getString("missing_15");
            this.checked_30 = object.getString("checked_30");
            this.missing_30 = object.getString("missing_30");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<AdmHospital> fromJson (JSONArray jsonObjects) {
        ArrayList<AdmHospital> hospital = new ArrayList<AdmHospital>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                hospital.add(new AdmHospital(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return hospital;
    }
}

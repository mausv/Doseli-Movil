package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/19/2015.
 */
public class Missing {
    public int id;
    public String model;
    public String serial_number;
    public String qr;
    public String reference;

    public Missing(JSONObject object) {
        try {
            this.id = object.getInt("mid");
            this.model = object.getString("model");
            this.serial_number = object.getString("serial_number");
            this.qr = object.getString("qr");
            this.reference = object.getString("reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Missing> fromJson(JSONArray jsonObjects) {
        ArrayList<Missing> missing = new ArrayList<Missing>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                missing.add(new Missing(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return missing;
    }

}

package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/24/2015.
 */
public class Handset {
    public int id;
    public String model;
    public String serial_number;
    public String qr;
    public String location;
    public String reference;

    public Handset(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.model = object.getString("model");
            this.serial_number = object.getString("serial_number");
            this.qr = object.getString("qr");
            this.location = object.getString("location");
            this.reference = object.getString("reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Handset> fromJson(JSONArray jsonObjects) {
        ArrayList<Handset> handset = new ArrayList<Handset>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                handset.add(new Handset(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return handset;
    }

}

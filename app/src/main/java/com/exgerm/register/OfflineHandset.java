package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mausv on 12/16/2015.
 */
public class OfflineHandset {
    public int id;
    public String qrId;
    public String token;
    public String model;
    public String serialNumber;
    public String reference;
    public String location;

    public OfflineHandset(JSONObject object) {
        try {
            this.id = object.getInt("mid");
            this.qrId = object.getString("qr");
            this.token = object.getString("uuid");
            this.model = object.getString("model");
            this.serialNumber = object.getString("serial_number");
            this.reference = object.getString("reference");
            this.location = object.getString("location");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public OfflineHandset() {

    }

    public static ArrayList<OfflineHandset> fromJson(JSONArray jsonObjects) {
        ArrayList<OfflineHandset> offlineHandsets = new ArrayList<OfflineHandset>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                offlineHandsets.add(new OfflineHandset(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return offlineHandsets;
    }
}

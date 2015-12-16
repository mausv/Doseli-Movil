package com.exgerm.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mausv on 12/16/2015.
 */
public class OfflineMissingHandsets {
    private ArrayList<OfflineHandset> missingHandsets = new ArrayList<>();

    public void addMissingHandset (ArrayList<OfflineHandset> offlineHandset) {
        missingHandsets = offlineHandset;
    }

    public ArrayList<OfflineHandset> getList() {
        return missingHandsets;
    }

    public void removeHandsetIfExists(String token) {
        boolean found = false;

        int pos = findHandsetPositionByToken(token);

        if(pos != -1) {
            found = true;
        }

        if(found) {
            removeHandsetFromList(pos);
        }
    }

    private void removeHandsetFromList(int pos) {
        missingHandsets.remove(pos);
    }

    public int findHandsetPositionByToken(String token) {
        int pos = -1;

        for(int i = 0; i < missingHandsets.size(); i++) {
            if(token.equals(missingHandsets.get(i).token)){
                pos = i;
            }
        }

        return pos;
    }
}

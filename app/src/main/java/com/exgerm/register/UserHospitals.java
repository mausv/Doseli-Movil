package com.exgerm.register;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserHospitals extends Activity {

    private ListView lvUserHospitals;

    JSONParser jsonParser = new JSONParser();

    JSONArray arrayOfHospitals2;
    UserHospitalsAdapter userHospitalsAdapter;
    ArrayList<UserHospital> arrayOfHospitals;

    private static String url_get_user_hospitals = LoginActivity.main_url + "get_user_hospitals.php";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hospitals);

        lvUserHospitals = (ListView) findViewById(R.id.lvUserHospitals);

        arrayOfHospitals = new ArrayList<UserHospital>();
        userHospitalsAdapter = new UserHospitalsAdapter(this, arrayOfHospitals);

        lvUserHospitals.setAdapter(userHospitalsAdapter);

        new GetHospitals().execute();
    }

    private class GetHospitals extends AsyncTask<Void, Void, Void> {
        int success = 0;
        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("user_id", LoginActivity.userId));

            JSONObject jsonUserHospitals = jsonParser.makeHttpRequest(url_get_user_hospitals, "POST", param);

            Log.d("JSONObject", jsonUserHospitals.toString());
            if(jsonUserHospitals != null) {
                try {
                    success = jsonUserHospitals.getInt("success");
                    if(success == 1) {
                        arrayOfHospitals2 = (JSONArray) jsonUserHospitals.get("hospitals");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(success == 1) {
                userHospitalsAdapter.clear();
                ArrayList<UserHospital> userHospitals = UserHospital.fromJson(arrayOfHospitals2);
                userHospitalsAdapter.addAll(userHospitals);
            } else {
                userHospitalsAdapter.clear();
            }

        }
    }

}

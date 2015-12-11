package com.exgerm.register;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    ArrayList<AdmHospital> arrayOfAdmin;
    JSONArray arrayOfAdmin2;

    TextView textViewTotalHospitals;
    TextView textViewTotalHandsets;

    AdmHospitalAdapter admHospitalAdapter;

    private static String url_get_all_admin_objects = LoginActivity.main_url + "get_all_admin_objects.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        textViewTotalHospitals = (TextView) findViewById(R.id.textViewHospitalsAdmin);
        textViewTotalHandsets = (TextView) findViewById(R.id.textViewHandsetsAdmin);

        arrayOfAdmin = new ArrayList<>();

        admHospitalAdapter = new AdmHospitalAdapter(this, arrayOfAdmin);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(admHospitalAdapter);

        new GetAdminObjects().execute();
    }

    public class GetAdminObjects extends AsyncTask<Void, Void, Void> {
        String totalHospitals;
        String totalHandsets;

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramsAdm = new ArrayList<>();
            paramsAdm.add(new BasicNameValuePair("userType_id", LoginActivity.userType));

            JSONObject jsonAdm = jsonParser.makeHttpRequest(url_get_all_admin_objects, "POST", paramsAdm);

            Log.e("Response: ", "> " + jsonAdm);
            try {
                arrayOfAdmin2 = (JSONArray) jsonAdm.get("hospitals");

                JSONArray innerArray = (JSONArray) jsonAdm.get("count");

                JSONObject innerObj = (JSONObject) innerArray.get(0);

                totalHospitals = innerObj.getString("hospitals");
                totalHandsets = innerObj.getString("handsets");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            textViewTotalHospitals.setText(totalHospitals);
            textViewTotalHandsets.setText(totalHandsets);

            admHospitalAdapter.clear();
            ArrayList<AdmHospital> latestAdmHospital = AdmHospital.fromJson(arrayOfAdmin2);
            admHospitalAdapter.addAll(latestAdmHospital);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

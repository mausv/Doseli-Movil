package com.exgerm.register;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StatusDetailView extends Activity {

    String objectId;
    String hspOb;
    protected TextView mStatus;
    protected TextView mUser;
    protected TextView mDate;
    protected TextView mHsp;

    protected CheckBox funciona;
    protected CheckBox errores;
    protected CheckBox error1CB;
    protected CheckBox error2CB;
    protected CheckBox error3CB;
    protected CheckBox sol1CB;
    protected CheckBox sol2CB;
    protected CheckBox sol3CB;
    protected CheckBox maint1CB;
    protected CheckBox maint2CB;

    JSONParser jsonParser = new JSONParser();

    protected static final String get_specs_url = LoginActivity.main_url + "get_machine_details_specs.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail_view);

        //Initialize
        mUser = (TextView) findViewById(R.id.statusDetailViewUser);
        mStatus = (TextView)findViewById(R.id.statusDetailView);
        mDate = (TextView) findViewById(R.id.statusDetailViewDate);
        mHsp = (TextView) findViewById(R.id.statusDetailViewHsp);
        funciona = (CheckBox) findViewById(R.id.perFinal);
        errores = (CheckBox) findViewById (R.id.erroresFinal);
        error1CB = (CheckBox) findViewById(R.id.error1CB);
        error2CB = (CheckBox) findViewById(R.id.error2CB);
        error3CB = (CheckBox) findViewById(R.id.error3CB);
        sol1CB = (CheckBox) findViewById(R.id.solucion1CB);
        sol2CB = (CheckBox) findViewById(R.id.solucion2CB);
        sol3CB = (CheckBox) findViewById(R.id.solucion3CB);
        maint1CB = (CheckBox) findViewById(R.id.maint1);
        maint2CB = (CheckBox) findViewById(R.id.maint2);

        //Get the intent that started the activity
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectID");

        Log.e("Obj Id: ", objectId);

        new GetMachineDetails().execute();


    }

    class GetMachineDetails extends AsyncTask<String, String, String> {
        int success;
        String username;
        String userStatus;
        String userDate;
        String userHsp;
        String state;
        String error1Checked;
        String error2Checked;
        String error3Checked;
        String sol1Checked;
        String sol2Checked;
        String sol3Checked;
        String maint1Checked;
        String maint2Checked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("code", objectId));

            JSONObject json = jsonParser.makeHttpRequest(get_specs_url, "POST", param);

            Log.i("Machine Specs: ", json.toString());

            try {
                success = json.getInt("success");

                if(success == 1) {

                    JSONArray array = (JSONArray) json.get("reports");

                    JSONObject innerObj = (JSONObject) array.get(0);

                    state = String.valueOf(innerObj.get("state"));
                    error1Checked = String.valueOf(innerObj.get("physicalDamage"));
                    error2Checked = String.valueOf(innerObj.get("lowBattery"));
                    error3Checked = String.valueOf(innerObj.get("lowLiquid"));
                    sol1Checked = String.valueOf(innerObj.get("physicalRepair"));
                    sol2Checked = String.valueOf(innerObj.get("changeBattery"));
                    sol3Checked = String.valueOf(innerObj.get("changeLiquid"));
                    maint1Checked = String.valueOf(innerObj.get("trayClean"));
                    maint2Checked = String.valueOf(innerObj.get("machineClean"));

                    username = String.valueOf(innerObj.get("user_name"));
                    userStatus = String.valueOf(innerObj.get("comment"));
                    userDate = String.valueOf(innerObj.get("created_at"));
                    userHsp = String.valueOf(innerObj.get("hospital_name"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Get if checked
            String x = "1";

            if (!(state.equals(x))) {
                errores.setChecked(true);
            } else {
                funciona.setChecked(true);
            }
            if (error1Checked.equals(x)) {
                error1CB.setChecked(true);
            }
            if (error2Checked.equals(x)) {
                error2CB.setChecked(true);
            }
            if (error3Checked.equals(x)) {
                error3CB.setChecked(true);
            }
            if (sol1Checked.equals(x)) {
                sol1CB.setChecked(true);
            }
            if (sol2Checked.equals(x)) {
                sol2CB.setChecked(true);
            }
            if (sol3Checked.equals(x)) {
                sol3CB.setChecked(true);
            }
            if (maint1Checked.equals(x)) {
                maint1CB.setChecked(true);
            }
            if (maint2Checked.equals(x)) {
                maint2CB.setChecked(true);
            }

            mUser.setText(username);
            mStatus.setText(userStatus);
            mDate.setText(userDate);
            mHsp.setText(userHsp);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status_detail_view, menu);
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

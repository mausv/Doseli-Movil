package com.exgerm.register;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class FullReportDetailView extends ListActivity {

    String objectId;
    String handset;
    String location;
    String reference;

    JSONParser jsonParser = new JSONParser();
    ArrayList<FullHandsetReport> arrayOfHandsets;
    JSONArray arrayOfHandsets2;

    FullHandsetReportAdapter adapter;

    private static String url_get_machine_history = LoginActivity.main_url + "get_machine_history.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_report_detail_view);
        TextView tvHandset = (TextView) findViewById(R.id.textViewHandset);
        TextView tvLocation = (TextView) findViewById(R.id.textViewLocation);
        TextView tvReference = (TextView) findViewById(R.id.textViewReference);

        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectID");
        handset = intent.getStringExtra("handset");
        location = intent.getStringExtra("location");
        reference = intent.getStringExtra("reference");

        tvHandset.setText(handset);
        tvLocation.setText(location);
        tvReference.setText(reference);

        arrayOfHandsets = new ArrayList<>();

        adapter = new FullHandsetReportAdapter(this, arrayOfHandsets);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        new GetHandsetHistory().execute();
    }

    public class GetHandsetHistory extends AsyncTask<Void, Void, Void> {

        int success;

        @Override
        protected Void doInBackground(Void... params) {

            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("machine_code", objectId));

            JSONObject json = jsonParser.makeHttpRequest(url_get_machine_history, "POST", param);

            Log.e("Machine Details: ", "> " + json);

            if (json != null) {
                try {
                    success = json.getInt("success");

                    if (success == 1) {

                        arrayOfHandsets2 = (JSONArray) json.get("reports");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (success == 1) {
                ArrayList<FullHandsetReport> handsetsList = FullHandsetReport.fromJson(arrayOfHandsets2);
                adapter.addAll(handsetsList);
            } else {
                adapter.clear();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_report_detail_view, menu);
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

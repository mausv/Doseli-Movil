package com.exgerm.register;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Handsets extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    ArrayList<Handset> arrayOfHandsets;
    JSONArray arrayOfHandsets2;

    HandsetAdapter adapter;

    private static String url_get_handsets_list = LoginActivity.main_url + "get_all_handsets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handsets);
        TextView textViewHospital = (TextView) findViewById(R.id.textViewHandsetHospital);

        textViewHospital.setText(LoginActivity.hspName);

        arrayOfHandsets = new ArrayList<Handset>();

        adapter = new HandsetAdapter(this, arrayOfHandsets);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new onListItemClick());

        new GetHandsets().execute();

    }

    public class GetHandsets extends AsyncTask<Void, Void, Void> {

        int success;

        @Override
        protected Void doInBackground(Void... params) {

            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("hsp_code", LoginActivity.hospitalSelectedId));

            JSONObject json = jsonParser.makeHttpRequest(url_get_handsets_list, "POST", param);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    success = json.getInt("success");

                    if (success == 1) {

                        arrayOfHandsets2 = (JSONArray) json.get("devices");

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
            if(arrayOfHandsets2 != null) {
                if (success == 1) {
                    ArrayList<Handset> handsetsList = Handset.fromJson(arrayOfHandsets2);
                    adapter.addAll(handsetsList);
                } else {
                    adapter.clear();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handsets, menu);
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

    private class onListItemClick implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Handset statusObject = adapter.getItem(position);
            String objectId = statusObject.qr;
            String handset = statusObject.model + statusObject.serial_number + " - QR: " + statusObject.qr;
            String location = statusObject.location;
            String reference = statusObject.reference;

            Intent goToDetailView = new Intent(Handsets.this, FullReportDetailView.class);
            goToDetailView.putExtra("objectID", objectId);
            goToDetailView.putExtra("handset", handset);
            goToDetailView.putExtra("location", location);
            goToDetailView.putExtra("reference", reference);
            startActivity(goToDetailView);
        }
    }
}

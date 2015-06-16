package com.exgerm.register;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomepageActivity extends ListActivity {

    String hspOb;

    protected TextView mTitle;
    protected Spinner filterCat;
    protected Spinner filterDate;
    protected Button filterBtn;
    protected String selCat = "Sin Filtro";
    protected String selDate = "Sin Filtro";
    protected Date dateAmpLess;
    protected Date dateAmpMore;
    protected int dateAmpSel = 0;

    ArrayList<Report> arrayOfReports;
    JSONArray arrayOfReports2;
    JSONArray categories;
    ReportsAdapter adapter;

    //JSON Parser
    JSONParser jsonParser = new JSONParser();

    private String reportsIdentifier = "reports";
    private String reportsArrayIdentifier = "report";

    Report cat2 = new Report("Mauricio", "July","Good");

    private static String url_get_reports = LoginActivity.main_url + "get_all_series.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mTitle = (TextView) findViewById(R.id.title);
        filterCat = (Spinner) findViewById(R.id.filterCat);
        filterDate = (Spinner) findViewById(R.id.filterDate);
        filterBtn = (Button) findViewById(R.id.filterBtn);

        hspOb = LoginActivity.hspName;

        Log.d("HSP: ", hspOb);

        arrayOfReports = new ArrayList<Report>();
        adapter = new ReportsAdapter(this, arrayOfReports);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);


        new GetReports().execute();


        String[] modCat = new String [7];
        modCat[0] = "Sin Filtro";
        modCat[1] = "Daño Físico";
        modCat[2] = "Batería Baja";
        modCat[3] = "Falta Líquido";
        modCat[4] = "Reparación";
        modCat[5] = "Cambio de Baterías";
        modCat[6] = "Cambio de Líquido";

        ArrayAdapter<String> spinnerArrayAdapterCat = new ArrayAdapter<String>(HomepageActivity.this, android.R.layout.simple_spinner_item, modCat);
        spinnerArrayAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        filterCat.setAdapter(spinnerArrayAdapterCat);

        String[] modDate = new String [3];
        modDate[0] = "Sin Filtro";
        modDate[1] = "Esta Semana";
        modDate[2] = "Este Mes";

        ArrayAdapter<String> spinnerArrayAdapterDate = new ArrayAdapter<String>(HomepageActivity.this, android.R.layout.simple_spinner_item, modDate);
        spinnerArrayAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        filterDate.setAdapter(spinnerArrayAdapterDate);

        filterCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterSel = filterCat.getSelectedItem().toString();
                selCat = filterSel;
                if (filterSel == "Sin Filtro") {
                    selCat = "Sin Filtro";
                } else if (filterSel == "Batería Baja") {
                    selCat = "bateriaBaja";
                } else if (filterSel == "Falta Líquido") {
                    selCat = "faltaLiquido";
                } else if (filterSel == "Daño Físico") {
                    selCat = "problemaFisico";
                } else if (filterSel == "Reparación") {
                    selCat = "reparacion";
                } else if (filterSel == "Cambio de Baterías") {
                    selCat = "cambioBaterias";
                } else if (filterSel == "Cambio de Líquido") {
                    selCat = "cambioLiquido";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filterDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String filterSel = filterDate.getSelectedItem().toString();
                 selDate = filterSel;
                 if (filterSel == "Sin Filtro") {
                     selDate = "Sin Filtro";
                 } else if (filterSel == "Esta Semana") {
                     dateAmpSel = 7;
                 } else if (filterSel == "Este Mes") {
                     dateAmpSel = 30;
                 }

                 LocalDate date = LocalDate.now();
                 DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                 String str = date.toString(fmt);
                 DateTime str2 = fmt.parseDateTime(str);
                 System.out.println(str);
                 String dob = date.minusDays(dateAmpSel).toString(fmt);
                 //String dob = date.toString("20-03-2015");
                 DateTime dob2 = fmt.parseDateTime(dob);

                 dateAmpLess = dob2.toDate();
                 dateAmpMore = str2.toDate();

                 System.out.println("Dif: " + Days.daysBetween(dob2.withTimeAtStartOfDay(),str2.withTimeAtStartOfDay()).getDays());

                 /*ParseQuery<ParseObject> query = new ParseQuery<>(hspOb);
                 query.orderByDescending("createdAt");
                 query.whereGreaterThanOrEqualTo("createdAt", dob3);
                 query.whereLessThanOrEqualTo("createdAt", str3);
                 query.findInBackground(new FindCallback<ParseObject>() {
                     @Override
                     public void done(List<ParseObject> status, ParseException e) {
                         if (e == null) {
                             //success

                             mStatus = status;

                             StatusAdapter adapter = new StatusAdapter(getListView().getContext(), mStatus);
                             setListAdapter(adapter);

                         } else {
                             //error
                             Toast.makeText(HomepageActivity.this, "Hubo un error en la conexión, vuelve a ingresar.", Toast.LENGTH_LONG).show();
                         }
                     }
                 });*/

             }


             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });

                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*ParseQuery<ParseObject> query = new ParseQuery<>(hspOb);
                        query.orderByDescending("createdAt");
                        if(!(selCat == "Sin Filtro")){
                            query.whereContains(selCat, "X");
                        }
                        if(!(selDate == "Sin Filtro")){
                            query.whereGreaterThanOrEqualTo("createdAt", dateAmpLess);
                            query.whereLessThanOrEqualTo("createdAt", dateAmpMore);
                        }
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> status, ParseException e) {
                                if (e == null) {
                                    //success

                                    mStatus = status;

                                    StatusAdapter adapter = new StatusAdapter(getListView().getContext(), mStatus);
                                    setListAdapter(adapter);

                                } else {
                                    //error
                                    Toast.makeText(HomepageActivity.this, "Hubo un error en la conexión, vuelve a ingresar.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                        if (selCat == "Sin Filtro") {
                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(hspOb);
                            query.orderByDescending("createdAt");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> status, ParseException e) {
                                    if (e == null) {
                                        //success

                                        mStatus = status;

                                        StatusAdapter adapter = new StatusAdapter(getListView().getContext(), mStatus);
                                        setListAdapter(adapter);

                                    } else {
                                        //error
                                        Toast.makeText(HomepageActivity.this, "Hubo un error en la conexión, vuelve a ingresar.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(hspOb);
                            query.orderByDescending("createdAt");
                            query.whereContains(selCat, "X");
                            query.whereGreaterThanOrEqualTo("createdAt", dob3);
                            query.whereLessThanOrEqualTo("createdAt", str3);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> status, ParseException e) {
                                    if (e == null) {
                                        //success

                                        mStatus = status;

                                        StatusAdapter adapter = new StatusAdapter(getListView().getContext(), mStatus);
                                        setListAdapter(adapter);

                                    } else {
                                        //error
                                        Toast.makeText(HomepageActivity.this, "Hubo un error en la conexión, vuelve a ingresar.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }*/
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.update_status:
                //Take user to update status activity
                Intent intent = new Intent(this, UpdateStatusActivity.class);
                startActivity(intent);

                break;
            case R.id.update_register_device:
                //Take user to update status activity
                Intent locationR = new Intent(this, HandsetRegister.class);
                startActivity(locationR);

                break;
            case R.id.update_location:
                //Take user to update status activity
                Intent location = new Intent(this, HandsetLocation.class);
                startActivity(location);

                break;
            case R.id.update_stolen:
                //Take user to update status activity
                Intent stolen = new Intent(this, ReportStolenActivity.class);
                startActivity(stolen);

                break;
            case R.id.logout:
                //Take the user back to the login activity
                Intent takeUserToLogin = new Intent(this, LoginActivity.class);
                startActivity(takeUserToLogin);

                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetReports extends AsyncTask<Void, Void, Void> {

        int success;

        @Override
        protected Void doInBackground(Void... params) {

            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("code", LoginActivity.hospitalSelectedId));

            JSONObject json = jsonParser.makeHttpRequest(url_get_reports, "POST", param);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    success = json.getInt("success");

                    if (success == 1) {

                        arrayOfReports2 = (JSONArray) json.get("reports");

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
                ArrayList<Report> latestReports = Report.fromJson(arrayOfReports2);
                adapter.addAll(latestReports);
            }

        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Report statusObject = adapter.getItem(position);
        String objectId = statusObject.getId();

        Intent goToDetailView = new Intent(HomepageActivity.this, StatusDetailView.class);
        goToDetailView.putExtra("objectID", objectId);
        startActivity(goToDetailView);

    }
}

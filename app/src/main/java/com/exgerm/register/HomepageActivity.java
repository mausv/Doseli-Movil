package com.exgerm.register;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

    public List<PendingReport> pendingArray;
    public List<PendingRegisterHandset> pendingRegisterArray;
    public List<PendingRegisterLocation> pendingLocationArray;

    private String reportsIdentifier = "reports";
    private String reportsArrayIdentifier = "report";

    //Progress Dialog
    private ProgressDialog pDialog;

    private static String url_get_reports = LoginActivity.main_url + "get_all_series.php";
    private static String url_get_details = LoginActivity.main_url + "get_machine_details_pending.php";
    private static String url_pending_report = LoginActivity.main_url + "pending_report.php";
    private static String url_pending_register_handset = LoginActivity.main_url + "pending_register_machine.php";
    private static String url_pending_register_location = LoginActivity.main_url + "pending_register_location.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(LoginActivity.hospitalSelected);
        PieChart pieChart = (PieChart) findViewById(R.id.totalChart);

        int totalHandsets = 8;
        int totalChecked = 6;
        int missing = totalHandsets - totalChecked;

        ArrayList<Entry> valsChecked = new ArrayList<Entry>();
        valsChecked.add(new Entry(missing, 0));
        valsChecked.add(new Entry(totalChecked, 1));

        String[] xVals = new String[] { "Total", "Checked"};

        PieDataSet  dataSet = new PieDataSet(valsChecked, "Total values");
        dataSet.setColors(new int[] {getResources().getColor(R.color.redColor), getResources().getColor(R.color.greenColor)});
        PieData data = new PieData(xVals, dataSet);

        pieChart.setData(data);
        pieChart.invalidate();

        //Setting the tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("summary");
        tabSpec.setContent(R.id.tabSummary);
        tabSpec.setIndicator("Resumen");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("reports");
        tabSpec.setContent(R.id.tabReports);
        tabSpec.setIndicator("Reportes");
        tabHost.addTab(tabSpec);

        hspOb = LoginActivity.hspName;

        Log.d("HSP: ", hspOb);

        arrayOfReports = new ArrayList<Report>();
        adapter = new ReportsAdapter(this, arrayOfReports);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);


        new GetReports().execute();


        /*String[] modCat = new String [7];
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

                 *//*ParseQuery<ParseObject> query = new ParseQuery<>(hspOb);
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
                 });*//*

             }


             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });

                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        *//*ParseQuery<ParseObject> query = new ParseQuery<>(hspOb);
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
                        }*//*
                    }
                });*/

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
            case R.id.send_pending:
                //Send pending from SQLite
                pendingArray = getAll();
                pendingRegisterArray = getRegisterHandset();
                pendingLocationArray = getRegisterLocation();
                /*for(int i = 0; i < pendingArray.size(); i++){
                    Log.i("0: ", String.valueOf(pendingArray.get(i).getToken()));
                }*/

                if(isOnline() == true) {

                    new SendPending().execute();

                } else if(isOnline() == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                    builder.setMessage("Necesitas conexion para mandar tus pendientes");
                    builder.setTitle("Falta conexion");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SendPending extends AsyncTask<Void, Void, Void> {
        int status = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomepageActivity.this);
            pDialog.setMessage("Enviando pendientes...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(pendingRegisterArray.size() > 0 || pendingLocationArray.size() > 0 || pendingArray.size() > 0) {
                if (pendingRegisterArray.size() > 0) {
                    for (int i = 0; i < pendingRegisterArray.size(); i++) {
                        int successGD;
                        String mid = "";

                        String tokenT = pendingRegisterArray.get(i).getToken();

                        // Check for success tag
                        // Building Parameters
                        List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                        paramsGetDetails.add(new BasicNameValuePair("token", tokenT));

                        // Building Parameters
                        List<NameValuePair> paramsP = new ArrayList<NameValuePair>();
                        paramsP.add(new BasicNameValuePair("token", pendingRegisterArray.get(i).getToken()));
                        paramsP.add(new BasicNameValuePair("model", pendingRegisterArray.get(i).getModel()));
                        paramsP.add(new BasicNameValuePair("serial_number", pendingRegisterArray.get(i).getSerial_number()));
                        paramsP.add(new BasicNameValuePair("user", pendingRegisterArray.get(i).getAssociated_by()));

                        // getting JSON Object
                        // Note that create product url accepts POST method
                        JSONObject jsonP = jsonParser.makeHttpRequest(url_pending_register_handset,
                                "POST", paramsP);

                        // check log cat fro response
                        Log.d("Create Response", jsonP.toString());

                        // check for success tag
                        try {
                            int success = jsonP.getInt("success");

                            if (success == 1) {
                                // successfully created product
            /*Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
            startActivity(i);*/
                                Log.i("Report status: ", "sent");

                                // closing this screen
                            } else {
                                // failed to create product
                                Log.i("Report status: ", "failed");
                            }

                            Log.i("SId: ", String.valueOf(pendingRegisterArray.get(i).getId()));

                            LoginActivity.offlineDb.delete("DoseliAltas", "id = " + String.valueOf(pendingRegisterArray.get(i).getId()), null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (pendingLocationArray.size() > 0) {
                    for (int i = 0; i < pendingLocationArray.size(); i++) {
                        int successGD;
                        String mid = "";

                        // Check for success tag

                        // Building Parameters
                        List<NameValuePair> paramsP = new ArrayList<NameValuePair>();
                        paramsP.add(new BasicNameValuePair("token", pendingLocationArray.get(i).getToken()));
                        paramsP.add(new BasicNameValuePair("group_id", pendingLocationArray.get(i).getGroup_id()));
                        paramsP.add(new BasicNameValuePair("hospital_id", pendingLocationArray.get(i).getHospital_id()));
                        paramsP.add(new BasicNameValuePair("area_id", pendingLocationArray.get(i).getArea_id()));
                        paramsP.add(new BasicNameValuePair("location_id", pendingLocationArray.get(i).getLocation_id()));
                        paramsP.add(new BasicNameValuePair("reference", pendingLocationArray.get(i).getReference()));

                        // getting JSON Object
                        // Note that create product url accepts POST method
                        JSONObject jsonP = jsonParser.makeHttpRequest(url_pending_register_location,
                                "POST", paramsP);

                        // check log cat fro response
                        Log.d("Create Response", jsonP.toString());

                        // check for success tag
                        try {
                            int success = jsonP.getInt("success");

                            if (success == 1) {
                                // successfully created product
            /*Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
            startActivity(i);*/
                                Log.i("Report status: ", "sent");

                                // closing this screen
                            } else {
                                // failed to create product
                                Log.i("Report status: ", "failed");
                            }

                            Log.i("SId: ", String.valueOf(pendingLocationArray.get(i).getId()));

                            LoginActivity.offlineDb.delete("DoseliPosicion", "id = " + String.valueOf(pendingLocationArray.get(i).getId()), null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (pendingArray.size() > 0) {
                    for (int i = 0; i < pendingArray.size(); i++) {
                        int successGD;
                        String mid = "";

                        String tokenT = pendingArray.get(i).getToken();

                        // Check for success tag
                        try {
                            // Building Parameters
                            List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                            paramsGetDetails.add(new BasicNameValuePair("token", tokenT));

                            // getting product details by making HTTP request
                            // Note that product details url will use GET request
                            JSONObject json = jsonParser.makeHttpRequest(
                                    url_get_details, "POST", paramsGetDetails);

                            // check your log for json response
                            Log.d("Single Product Details", json.toString());

                            // json success tag
                            successGD = json.getInt("success");
                            if (successGD == 1) {
                                Log.i("Token status: ", "valid");
                                // successfully received product details
                                JSONArray productObj = json
                                        .getJSONArray("product"); // JSON Array

                                // get first product object from JSON Array
                                JSONObject product = productObj.getJSONObject(0);

                                // product with this pid found
                                String aparato = product.getString("model") + product.getString("serial_number");
                                mid = product.getString("id");
                                Log.d("Aparato: ", aparato);

                                // Building Parameters
                                List<NameValuePair> paramsP = new ArrayList<NameValuePair>();
                                paramsP.add(new BasicNameValuePair("machines_id", mid));
                                paramsP.add(new BasicNameValuePair("state", pendingArray.get(i).getState()));
                                paramsP.add(new BasicNameValuePair("comment", pendingArray.get(i).getDevice_comment()));
                                paramsP.add(new BasicNameValuePair("users_id", pendingArray.get(i).getUsers_id()));
                                paramsP.add(new BasicNameValuePair("user_name", pendingArray.get(i).getUser_name()));
                                paramsP.add(new BasicNameValuePair("lowBattery", pendingArray.get(i).getLowBattery()));
                                paramsP.add(new BasicNameValuePair("changeBattery", pendingArray.get(i).getChangeBattery()));
                                paramsP.add(new BasicNameValuePair("lowLiquid", pendingArray.get(i).getLowLiquid()));
                                paramsP.add(new BasicNameValuePair("changeLiquid", pendingArray.get(i).getChangeLiquid()));
                                paramsP.add(new BasicNameValuePair("physicalDamage", pendingArray.get(i).getPhysicalDamage()));
                                paramsP.add(new BasicNameValuePair("physicalRepair", pendingArray.get(i).getPhysicalRepair()));
                                paramsP.add(new BasicNameValuePair("hospitals_id", pendingArray.get(i).getHospitals_id()));
                                paramsP.add(new BasicNameValuePair("hospital_name", pendingArray.get(i).getHospital_name()));

                                // getting JSON Object
                                // Note that create product url accepts POST method
                                JSONObject jsonP = jsonParser.makeHttpRequest(url_pending_report,
                                        "POST", paramsP);

                                // check log cat fro response
                                Log.d("Create Response", jsonP.toString());

                                // check for success tag
                                try {
                                    int success = jsonP.getInt("success");

                                    if (success == 1) {
                                        // successfully created product
                    /*Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    startActivity(i);*/
                                        Log.i("Report status: ", "sent");

                                        // closing this screen
                                    } else {
                                        // failed to create product
                                        Log.i("Report status: ", "failed");
                                    }

                                    Log.i("SId: ", String.valueOf(pendingArray.get(i).getId()));

                                    LoginActivity.offlineDb.delete("DoseliOffline", "id = " + String.valueOf(pendingArray.get(i).getId()), null);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } else if (successGD == 0) {
                                Log.i("Token status: ", "invalid");

                                LoginActivity.offlineDb.delete("DoseliOffline", "id = " + pendingArray.get(i).getId(), null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                status = 1;
            } else {
                status = 0;
            }

//            LoginActivity.offlineDb.delete("DoseliOffline", null, null);
//            LoginActivity.offlineDb.execSQL("CREATE TABLE IF NOT EXISTS DoseliOffline(id INTEGER PRIMARY KEY, token VARCHAR, state VARCHAR, device_comment VARCHAR, users_id VARCHAR, user_name VARCHAR, lowBattery VARCHAR, changeBattery VARCHAR, lowLiquid VARCHAR, changeLiquid VARCHAR, physicalDamage VARCHAR, physicalRepair VARCHAR, hospitals_id VARCHAR, hospital_name VARCHAR);");
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(status == 1) {
                pDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                builder.setTitle("Listo");
                builder.setMessage("Reportes pendientes enviados");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else if (status == 0){
                pDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                builder.setTitle("Todo completo");
                builder.setMessage("No hay pendientes");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
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

    public class PendingReport {
        private int id;
        private String token;
        private String state;
        private String device_comment;
        private String users_id;
        private String user_name;
        private String lowBattery;
        private String changeBattery;
        private String lowLiquid;
        private String changeLiquid;
        private String physicalDamage;
        private String physicalRepair;
        private String hospitals_id;
        private String hospital_name;

        public String getHospital_name() {
            return hospital_name;
        }

        public void setHospital_name(String hospital_name) {
            this.hospital_name = hospital_name;
        }

        public String getLowBattery() {
            return lowBattery;
        }

        public void setLowBattery(String lowBattery) {
            this.lowBattery = lowBattery;
        }

        public String getChangeBattery() {
            return changeBattery;
        }

        public void setChangeBattery(String changeBattery) {
            this.changeBattery = changeBattery;
        }

        public String getLowLiquid() {
            return lowLiquid;
        }

        public void setLowLiquid(String lowLiquid) {
            this.lowLiquid = lowLiquid;
        }

        public String getChangeLiquid() {
            return changeLiquid;
        }

        public void setChangeLiquid(String changeLiquid) {
            this.changeLiquid = changeLiquid;
        }

        public String getPhysicalDamage() {
            return physicalDamage;
        }

        public void setPhysicalDamage(String physicalDamage) {
            this.physicalDamage = physicalDamage;
        }

        public String getPhysicalRepair() {
            return physicalRepair;
        }

        public void setPhysicalRepair(String physicalRepair) {
            this.physicalRepair = physicalRepair;
        }

        public String getHospitals_id() {
            return hospitals_id;
        }

        public void setHospitals_id(String hospitals_id) {
            this.hospitals_id = hospitals_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public void setId (int id){
            this.id = id;
        }

        public void setToken (String token) {
            this.token = token;
        }

        public int getId () {
            return this.id;
        }

        public String getToken() {
            return this.token;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDevice_comment() {
            return device_comment;
        }

        public void setDevice_comment(String device_comment) {
            this.device_comment = device_comment;
        }

        public String getUsers_id() {
            return users_id;
        }

        public void setUsers_id(String users_id) {
            this.users_id = users_id;
        }
    }

    public class PendingRegisterHandset {
        private int id;
        private String model;
        private String serial_number;
        private String associated_by;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getSerial_number() {
            return serial_number;
        }

        public void setSerial_number(String serial_number) {
            this.serial_number = serial_number;
        }

        public String getAssociated_by() {
            return associated_by;
        }

        public void setAssociated_by(String associated_by) {
            this.associated_by = associated_by;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

    }

    public class PendingRegisterLocation {

        private int id;
        private String token;
        private String group_id;
        private String hospital_id;
        private String area_id;
        private String location_id;
        private String reference;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getHospital_id() {
            return hospital_id;
        }

        public void setHospital_id(String hospital_id) {
            this.hospital_id = hospital_id;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getLocation_id() {
            return location_id;
        }

        public void setLocation_id(String location_id) {
            this.location_id = location_id;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }

    public List<PendingRegisterHandset> getRegisterHandset() {
        List<PendingRegisterHandset> pendingRegister = new ArrayList<PendingRegisterHandset>();
        PendingRegisterHandset member = null;
        Cursor c = null;
        try {
            c = LoginActivity.offlineDb.rawQuery("Select * from DoseliAltas", null);
            if (c.moveToFirst()) {
                do {
                    member = new PendingRegisterHandset();
                    member.setId(c.getInt(c.getColumnIndex("id")));
                    member.setToken(c.getString(c.getColumnIndex("token")));
                    member.setModel(c.getString(c.getColumnIndex("model")));
                    member.setSerial_number(c.getString(c.getColumnIndex("serial_number")));
                    member.setAssociated_by(c.getString(c.getColumnIndex("associated_by")));
                    pendingRegister.add(member);
                } while (c.moveToNext());
            }
            Log.i("PReports: ", pendingRegister.toString());
            return pendingRegister;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public List<PendingRegisterLocation> getRegisterLocation() {
        List<PendingRegisterLocation> pendingLocation = new ArrayList<PendingRegisterLocation>();
        PendingRegisterLocation member = null;
        Cursor c = null;
        try {
            c = LoginActivity.offlineDb.rawQuery("Select * from DoseliPosicion", null);
            if (c.moveToFirst()) {
                do {
                    member = new PendingRegisterLocation();
                    member.setId(c.getInt(c.getColumnIndex("id")));
                    member.setToken(c.getString(c.getColumnIndex("token")));
                    member.setGroup_id(c.getString(c.getColumnIndex("group_id")));
                    member.setHospital_id(c.getString(c.getColumnIndex("hospital_id")));
                    member.setArea_id(c.getString(c.getColumnIndex("area_id")));
                    member.setLocation_id(c.getString(c.getColumnIndex("location_id")));
                    member.setReference(c.getString(c.getColumnIndex("reference")));
                    pendingLocation.add(member);
                } while (c.moveToNext());
            }
            Log.i("PLocations: ", pendingLocation.toString());
            return pendingLocation;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public List<PendingReport> getAll() {
        List<PendingReport> pendingReports = new ArrayList<PendingReport>();
        PendingReport member = null;
        Cursor c = null;
        try {
            c = LoginActivity.offlineDb.rawQuery("Select * from DoseliOffline", null);
            if (c.moveToFirst()) {
                do {
                    member = new PendingReport();
                    member.setId(c.getInt(c.getColumnIndex("id")));
                    member.setToken(c.getString(c.getColumnIndex("token")));
                    member.setState(c.getString(c.getColumnIndex("state")));
                    member.setDevice_comment(c.getString(c.getColumnIndex("device_comment")));
                    member.setUsers_id(c.getString(c.getColumnIndex("users_id")));
                    member.setUser_name(c.getString(c.getColumnIndex("user_name")));
                    member.setLowBattery(c.getString(c.getColumnIndex("lowBattery")));
                    member.setChangeBattery(c.getString(c.getColumnIndex("changeBattery")));
                    member.setLowLiquid(c.getString(c.getColumnIndex("lowLiquid")));
                    member.setChangeLiquid(c.getString(c.getColumnIndex("changeLiquid")));
                    member.setPhysicalDamage(c.getString(c.getColumnIndex("physicalDamage")));
                    member.setPhysicalRepair(c.getString(c.getColumnIndex("physicalRepair")));
                    member.setHospitals_id(c.getString(c.getColumnIndex("hospitals_id")));
                    member.setHospital_name(c.getString(c.getColumnIndex("hospital_name")));
                    pendingReports.add(member);
                } while (c.moveToNext());
            }
            Log.i("PReports: ", pendingReports.toString());
            return pendingReports;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

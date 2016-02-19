package com.exgerm.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomepageActivity extends AppCompatActivity {

    String hspOb;

    ArrayList<Report> arrayOfReports;
    JSONArray arrayOfReports2;
    ReportsAdapter adapter;

    ArrayList<Missing> arrayOfMissing;
    JSONArray arrayOfMissing2;
    MissingAdapter missingAdapter;

    //JSON Parser
    JSONParser jsonParser = new JSONParser();

    PieChart pieChart;
    int totalHandsets;
    int totalChecked15;
    int totalChecked30;
    int missing;
    private Switch switchDay;
    private Switch switchMissing;
    int daysChoice = 15;
    int missingDaysChoice = 15;
    private TextView txtTotalHandsets;

    public List<PendingReport> pendingArray;
    public List<PendingRegisterHandset> pendingRegisterArray;
    public List<PendingRegisterLocation> pendingLocationArray;
    public List<PendingReportStolen> pendingReportStolenArray;

    //Progress Dialog
    private ProgressDialog pDialog;

    private String[] mTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    HomepageDrawerAdapter homepageDrawerAdapter;
    ArrayList<HomepageDrawerItem> arrayOfDrawerItems;
    private Button btnOfflineMissingHandsets;

    private static String url_get_reports = LoginActivity.main_url + "get_all_series.php";
    private static String url_get_details = LoginActivity.main_url + "get_machine_details_pending.php";
    private static String url_pending_report = LoginActivity.main_url + "pending_report.php";
    private static String url_pending_register_handset = LoginActivity.main_url + "pending_register_machine.php";
    private static String url_pending_register_location = LoginActivity.main_url + "pending_register_location.php";
    private static String url_pending_report_stolen = LoginActivity.main_url + "pending_report_stolen.php";
    private static String url_get_checked_devices = LoginActivity.main_url + "get_checked_devices.php";
    private static String url_get_missing_check_devices = LoginActivity.main_url + "get_missing_check_devices.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(LoginActivity.hospitalSelected);
        }

        pieChart = (PieChart) findViewById(R.id.totalChart);
        switchDay = (Switch) findViewById(R.id.switchDays);
        switchMissing = (Switch) findViewById(R.id.switchMissing);
        txtTotalHandsets = (TextView) findViewById(R.id.tvTotalHandsets);
        mTitles = getResources().getStringArray(R.array.drawer_main_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        btnOfflineMissingHandsets = (Button) findViewById(R.id.btnOfflineMissingHandsets);
        mActivityTitle = getTitle().toString();

        setupDrawer();

        Snackbar snackbarWelcome = Snackbar.make(
                findViewById(R.id.coordinator_homepage_layout),
                "Bienvenido a " + LoginActivity.hospitalSelected + ", " + LoginActivity.userName,
                Snackbar.LENGTH_SHORT);
        snackbarWelcome.show();

        arrayOfDrawerItems = new ArrayList<>();

        for(int i = 0; i < mTitles.length; i++) {
            HomepageDrawerItem tempDrawerItem = new HomepageDrawerItem(i, mTitles[i], mTitles[i]);
            arrayOfDrawerItems.add(tempDrawerItem);
        }

        homepageDrawerAdapter = new HomepageDrawerAdapter(this, arrayOfDrawerItems);

        mDrawerList.setAdapter(homepageDrawerAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        new GetCheckedDevices().execute();

        switchDay.setChecked(false);

        switchDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    daysChoice = 15;
                } else {
                    daysChoice = 30;
                }

                new GetCheckedDevices().execute();
            }
        });

        switchMissing.setChecked(false);

        switchMissing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    missingDaysChoice = 15;
                } else {
                    missingDaysChoice = 30;
                }

                new GetMissing().execute();
            }
        });

        //Setting the tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("summary");
        tabSpec.setContent(R.id.tabSummary);
        tabSpec.setIndicator("Resumen");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("handsets");
        tabSpec.setContent(R.id.tabHandsets);
        tabSpec.setIndicator("Aparatos");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("reports");
        tabSpec.setContent(R.id.tabReports);
        tabSpec.setIndicator("Reportes");
        tabHost.addTab(tabSpec);

        hspOb = LoginActivity.hspName;

        Log.d("HSP: ", hspOb);

        arrayOfReports = new ArrayList<Report>();
        arrayOfMissing = new ArrayList<Missing>();
        adapter = new ReportsAdapter(this, arrayOfReports);
        missingAdapter = new MissingAdapter(this, arrayOfMissing);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListItemClickListener());
        ListView missingView = (ListView) findViewById(R.id.listMissing);
        missingView.setAdapter(missingAdapter);


        new GetMissing().execute();
        new GetReports().execute();

        btnOfflineMissingHandsets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOffline();
            }
        });

    }

    private void goToOffline() {
        Intent goToOffline = new Intent(this, OfflineMissingHandsetsActivity.class);
        startActivity(goToOffline);
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
        /*switch (id){
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
            case R.id.handsets:
                Intent takeToHandsetList = new Intent(this, Handsets.class);
                startActivity(takeToHandsetList);

                break;
            case R.id.admin:
                if(Integer.parseInt(LoginActivity.userType) == 2) {
                    Intent takeToAdmin = new Intent(this, AdminActivity.class);
                    startActivity(takeToAdmin);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Faltan permisos");
                    builder.setMessage("No tienes el permiso para acceder a esta parte del sistema");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.send_pending:
                //Send pending from SQLite
                pendingArray = getAll();
                pendingRegisterArray = getRegisterHandset();
                pendingLocationArray = getRegisterLocation();
                *//*for(int i = 0; i < pendingArray.size(); i++){
                    Log.i("0: ", String.valueOf(pendingArray.get(i).getToken()));
                }*//*

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
        }*/

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Herramientas");
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(LoginActivity.hospitalSelected);
                }
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            if(pendingRegisterArray.size() > 0 || pendingLocationArray.size() > 0 || pendingArray.size() > 0 || pendingReportStolenArray.size() > 0) {
                if (pendingReportStolenArray.size() > 0) {
                    for (int i = 0; i < pendingReportStolenArray.size(); i++) {
                        int successGD;
                        String mid = "";

                        String tokenT = pendingReportStolenArray.get(i).getToken();

                        // Check for success tag
                        // Building Parameters
                        List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                        paramsGetDetails.add(new BasicNameValuePair("token", tokenT));

                        // Building Parameters
                        List<NameValuePair> paramsP = new ArrayList<NameValuePair>();
                        paramsP.add(new BasicNameValuePair("token", pendingReportStolenArray.get(i).getToken()));
                        paramsP.add(new BasicNameValuePair("deleted_by", pendingReportStolenArray.get(i).getDeleted_by()));

                        // getting JSON Object
                        // Note that create product url accepts POST method
                        JSONObject jsonP = jsonParser.makeHttpRequest(url_pending_report_stolen,
                                "POST", paramsP);

                        // check log cat fro response
                        Log.d("Create Response", jsonP.toString());

                        // check for success tag
                        try {
                            int success = jsonP.getInt("success");

                            if (success == 1) {
                                // successfully created product
                                Log.i("Report status: ", "sent");

                                // closing this screen
                            } else {
                                // failed to create product
                                Log.i("Report status: ", "failed");
                            }

                            Log.i("SId: ", String.valueOf(pendingReportStolenArray.get(i).getId()));

                            LoginActivity.offlineDb.delete("DoseliBajas", "id = " + String.valueOf(pendingReportStolenArray.get(i).getId()), null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

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
                        paramsP.add(new BasicNameValuePair("location_set_by", pendingLocationArray.get(i).getLocation_set_by()));

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
                                paramsP.add(new BasicNameValuePair("trayClean", pendingArray.get(i).getTrayClean()));
                                paramsP.add(new BasicNameValuePair("machineClean", pendingArray.get(i).getMachineClean()));
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
                new GetCheckedDevices().execute();
                new GetMissing().execute();
                new GetReports().execute();
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

    public class GetMissing extends AsyncTask<Void, Void, Void> {

        int success;

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("hsp_code", LoginActivity.hospitalSelectedId));
            param.add(new BasicNameValuePair("days", String.valueOf(missingDaysChoice)));

            JSONObject jsonMissing = jsonParser.makeHttpRequest(url_get_missing_check_devices, "POST", param);

            if(jsonMissing != null) {
                try {
                    success = jsonMissing.getInt("success");

                    if(success == 1) {

                        arrayOfMissing2 = (JSONArray) jsonMissing.get("devices");

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
                missingAdapter.clear();
                ArrayList<Missing> latestMissing = Missing.fromJson(arrayOfMissing2);
                missingAdapter.addAll(latestMissing);
            } else {
                missingAdapter.clear();
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
                adapter.clear();
                ArrayList<Report> latestReports = Report.fromJson(arrayOfReports2);
                adapter.addAll(latestReports);
            }

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
                    member.setLocation_set_by(c.getString(c.getColumnIndex("location_set_by")));
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

    public class GetCheckedDevices extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> checkedParams = new ArrayList<>();
            checkedParams.add(new BasicNameValuePair("hsp_code", LoginActivity.hspCode));

            JSONObject jsonObjCheck = jsonParser.makeHttpRequest(url_get_checked_devices, "POST", checkedParams);

            if(jsonObjCheck != null) {
                try {
                    JSONArray devices = jsonObjCheck.getJSONArray("devices");
                    JSONObject totalDevices = (JSONObject) devices.get(0);
                    JSONObject totalCheckedDevicesFifteen = (JSONObject) devices.get(1);
                    JSONObject totalCheckedDevicesThirty = (JSONObject) devices.get(2);

                    totalHandsets = totalDevices.getInt("count");
                    totalChecked15 = totalCheckedDevicesFifteen.getInt("checked_count_15");
                    totalChecked30 = totalCheckedDevicesThirty.getInt("checked_count_30");

                    if(daysChoice == 15) {
                        missing = totalHandsets - totalChecked15;
                    } else {
                        missing = totalHandsets - totalChecked30;
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

            txtTotalHandsets.setText(String.valueOf(totalHandsets));

            ArrayList<Entry> valsChecked = new ArrayList<>();
            valsChecked.add(new Entry(missing, 0));
            if(daysChoice == 15) {
                valsChecked.add(new Entry(totalChecked15, 1));
            } else {
                valsChecked.add(new Entry(totalChecked30, 1));
            }

            String[] xVals = new String[] { "Faltan", "Revisados"};

            pieChart.setCenterTextColor(getResources().getColor(R.color.whiteColor));

            PieDataSet  dataSet = new PieDataSet(valsChecked, "Equipos revisados");
            pieChart.setDescription("");
            dataSet.setColors(new int[]{getResources().getColor(R.color.orangeColor), getResources().getColor(R.color.blueColor)});
            dataSet.setValueTextColor(getResources().getColor(R.color.whiteColor));
            PieData data = new PieData(xVals, dataSet);
            dataSet.setValueFormatter(new CheckedFormatter());

            pieChart.animateXY(2000,2000);

            pieChart.spin(1000, 0, -260f, Easing.EasingOption.EaseInOutQuad);

            pieChart.setData(data);

            pieChart.invalidate();

        }
    }

    public class CheckedFormatter implements ValueFormatter {
        private DecimalFormat mFormat;

        public CheckedFormatter () {
            mFormat = new DecimalFormat("###,###,##0");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
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
                    member.setTrayClean(c.getString(c.getColumnIndex("trayClean")));
                    member.setMachineClean(c.getString(c.getColumnIndex("machineClean")));
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

    public List<PendingReportStolen> getReportStolen() {
        List<PendingReportStolen> pendingReportStolen = new ArrayList<PendingReportStolen>();
        PendingReportStolen member = null;
        Cursor c = null;
        try {
            c = LoginActivity.offlineDb.rawQuery("Select * from DoseliBajas", null);
            if (c.moveToFirst()) {
                do {
                    member = new PendingReportStolen();
                    member.setId(c.getInt(c.getColumnIndex("id")));
                    member.setToken(c.getString(c.getColumnIndex("token")));
                    member.setDeleted_by(c.getString(c.getColumnIndex("deleted_by")));
                    pendingReportStolen.add(member);
                } while (c.moveToNext());
            }
            Log.i("PReports: ", pendingReportStolen.toString());
            return pendingReportStolen;
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

    private Snackbar createSnackbar(String message, int duration) {
        Snackbar newSnackbar = Snackbar.make(
          findViewById(R.id.coordinator_homepage_layout), message, duration
        );
        return newSnackbar;
    }

    private class ListItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Report statusObject = adapter.getItem(position);
            String objectId = statusObject.getId();
            String qr = statusObject.getQrs_id();

            if(isOnline()) {
                Intent goToDetailView = new Intent(HomepageActivity.this, StatusDetailView.class);
                goToDetailView.putExtra("objectID", objectId);
                goToDetailView.putExtra("qrs_id", qr);
                startActivity(goToDetailView);
            } else {
                Snackbar snackbarGoToDetailViewOffline = createSnackbar("No tienes internet para cargar el reporte.", Snackbar.LENGTH_SHORT);
                snackbarGoToDetailViewOffline.show();
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Drawer Item", mTitles[position]);
            switch (mTitles[position]){
                case "Reportar":
                    //Take user to update status activity
                    Intent intent = new Intent(HomepageActivity.this, UpdateStatusActivity.class);
                    startActivity(intent);

                    break;
                case "Altas":
                    Intent locationR = new Intent(HomepageActivity.this, HandsetRegister.class);
                    startActivity(locationR);

                    break;
                case "Posicionar Aparato":
                    Intent location = new Intent(HomepageActivity.this, HandsetLocation.class);
                    startActivity(location);

                    break;
                case "Bajas":
                    Intent stolen = new Intent(HomepageActivity.this, ReportStolenActivity.class);
                    startActivity(stolen);

                    break;
                case "Lista de Aparatos":
                    if(isOnline()) {
                        Intent takeToHandsetList = new Intent(HomepageActivity.this, Handsets.class);
                        startActivity(takeToHandsetList);
                    } else {
                        Snackbar snackbarHandsetListOffline = createSnackbar(
                                "No tienes internet para cargar la lista.", Snackbar.LENGTH_SHORT);
                        snackbarHandsetListOffline.show();
                    }

                    break;
                case "Mis Hospitales":
                    if(isOnline()) {
                        Intent takeUserToHospitalList = new Intent(HomepageActivity.this, UserHospitals.class);
                        startActivity(takeUserToHospitalList);
                    } else {
                        Snackbar snackbarHospitalsOffline = createSnackbar(
                                "No tienes internet para cargar la lista.", Snackbar.LENGTH_SHORT);
                        snackbarHospitalsOffline.show();
                    }

                    break;
                case "Enviar Pendientes":
                    //Send pending from SQLite
                    pendingArray = getAll();
                    pendingRegisterArray = getRegisterHandset();
                    pendingLocationArray = getRegisterLocation();
                    pendingReportStolenArray = getReportStolen();
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
                case "Administrador":
                    if(isOnline()) {
                        if (Integer.parseInt(LoginActivity.userType) == 2) {
                            Intent takeToAdmin = new Intent(HomepageActivity.this, AdminActivity.class);
                            startActivity(takeToAdmin);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                            builder.setTitle("Faltan permisos");
                            builder.setMessage("No tienes el permiso para acceder a esta parte del sistema");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    } else {
                        Snackbar snackbarAdminOffline = createSnackbar(
                                "No tienes internet para cargar la lista.", Snackbar.LENGTH_SHORT
                        );
                        snackbarAdminOffline.show();
                    }
                    break;

            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

}

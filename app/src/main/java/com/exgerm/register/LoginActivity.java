package com.exgerm.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LoginActivity extends Activity {

    //User's info
    protected Spinner groupSpinner;
    protected Spinner hospitalSpinner;
    public static String imei;
    public static String currentVersion;
    public static String userId;
    public static String userName;
    public static String hspName;
    public static String hspCode;
    public static String userType;
    protected Button mLogin;
    protected TextView textLat;
    protected TextView textLong;
    public Boolean geoBoxCorrect = false;
    public TextView versionTV;

    //Offline
    public static ArrayList<Category> groupsOff;
    public static ArrayList<Category> hospitalsOff;
    public static ArrayList<Category> modelsOff;
    public static ArrayList<Category> areasOff;
    public static ArrayList<Category> locationsOff;

    /*protected double fixedLatTop = 25.66896339;
    protected double fixedLongTop = -100.32322168;
    protected double fixedLatLow = 25.6671841;
    protected double fixedLongLow = -100.32149971;*/

    BarChart challengeChart;

    protected double lat;
    protected double lon;
    protected double alt;

    protected double fixedLatTop;
    protected double fixedLongTop;
    protected double fixedLatLow;
    protected double fixedLongLow;

    public static SQLiteDatabase offlineDb;


    private ArrayList<Category> groupsList;
    private String groupIdentifier = "groups";
    private String groupCategoryIdentifier = "group";
    public static String groupSelectedId = "0";
    public static String groupSelected = "";

    private ArrayList<Category> hospitalsList;
    private String hospitalIdentifier = "hospitals";
    private String hospitalCategoryIdentifier = "hospital";
    public static String hospitalSelectedId = "0";
    public static String hospitalSelected = "";

    private ArrayList<ChallengeUser> challengeList;

    //Progress Dialog
    private ProgressDialog pDialog;

    //public static String main_url = "http://exgerm.marpanet.com/doselimovil/";
    public static String main_url = "http://192.168.1.152/doseli/";

    File fileDir;


    //URLs
    private static String url_login = main_url + "login.php";
    private static String url_get_groups = main_url + "get_groups.php";
    private static String url_get_hospitals = main_url + "get_hospitals.php";
    private static String url_get_hospital_geo = main_url + "get_hospital_geo.php";
    private static String url_get_latest_version = main_url + "get_latest_version.php";
    private static String url_get_off = main_url + "get_hospital_offline.php";
    private static String url_get_challenge = main_url + "challenge_summary.php";

    //Column variables
    private static String TAG_CODE = "code";
    private static String TAG_IMEI = "imei";
    private static String TAG_LATITUDE = "latitude";
    private static String TAG_LONGITUDE = "longitude";
    private static String TAG_ALTITUDE = "altitude";
    private static final String TAG_SUCCESS = "success";

    //JSON Parser
    JSONParser jsonParser = new JSONParser();

    protected String username;
    protected String password;
    protected String id;
    public String hspObtained = "";
    public String codeObtained;

    /*protected double fixedLatTop = 25.669733;
    protected double fixedLongTop = -100.323889;
    protected double fixedLatLow = 25.665826;
    protected double fixedLongLow = -100.320123;*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initialize protected objects
        groupSpinner = (Spinner) findViewById(R.id.loginGroupSpinner);
        hospitalSpinner = (Spinner) findViewById(R.id.loginHospitalSpinner);
        mLogin = (Button) findViewById(R.id.loginButton);
        textLat = (TextView) findViewById(R.id.latitudeTextView);
        textLong = (TextView) findViewById(R.id.longitudeTextView);
        versionTV = (TextView) findViewById(R.id.versionTV);
        challengeChart = (BarChart) findViewById(R.id.challengeChart);
        getImei();

        fileDir = getFilesDir();

        System.out.println("DIR: " + this.getFilesDir());

        Log.d("IMEI: ", imei);

        boolean locationEnabled = isLocationEnabled(this);
        if(locationEnabled == false) {
            Toast.makeText(LoginActivity.this, "Prende el GPS", Toast.LENGTH_LONG).show();

            Intent settings = new Intent("com.google.android.gms.location.settings.GOOGLE_LOCATION_SETTINGS");
            startActivity(settings);
        }

        offlineDb = openOrCreateDatabase("Doseli.db", MODE_PRIVATE, null);
        offlineDb.execSQL("CREATE TABLE IF NOT EXISTS DoseliOffline" +
                "(id INTEGER PRIMARY KEY, token VARCHAR, " +
                "state VARCHAR, device_comment VARCHAR, " +
                "users_id VARCHAR, user_name VARCHAR, " +
                "lowBattery VARCHAR, changeBattery VARCHAR, " +
                "lowLiquid VARCHAR, changeLiquid VARCHAR, " +
                "physicalDamage VARCHAR, physicalRepair VARCHAR, " +
                "hospitals_id VARCHAR, hospital_name VARCHAR);");
        offlineDb.execSQL("CREATE TABLE IF NOT EXISTS DoseliAltas" +
                "(id INTEGER PRIMARY KEY, mId VARCHAR, model VARCHAR, " +
                "serial_number VARCHAR, associated_by VARCHAR, " +
                "token VARCHAR);");
        offlineDb.execSQL("CREATE TABLE IF NOT EXISTS DoseliPosicion" +
                "(id INTEGER PRIMARY KEY, token VARCHAR, " +
                "group_id VARCHAR, " +
                "hospital_id VARCHAR, " +
                "area_id VARCHAR, " +
                "location_id VARCHAR, " +
                "reference VARCHAR);");

        groupsList = new ArrayList<>();


        if(isOnline() == false){
            Log.i("Internet status: ", "Not Available");
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Esta aplicacion necesita internet como minimo para el inicio de sesion");
            builder.setTitle("Fuera de linea");
            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            Log.i("Internet status: ", "Available");

            new CheckInternetConnection().execute();
        }

        //Initialize LocationManager and LocationListener
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener ll = new LocationListener() {
            protected double pLat;
            protected double pLong;


            public void onLocationChanged(Location location) {
                System.out.println("Location Changed");
                lat = location.getLatitude();
                lon = location.getLongitude();
                alt = location.getAltitude();

                textLat.setText("" + lat);
                textLong.setText("" + lon);

                /*if (location != null) {
                    this.pLat = location.getLatitude();
                    this.pLong = location.getLongitude();

                    if (((pLat <= fixedLatTop) && (pLat >= fixedLatLow) && (pLong <= fixedLongLow) && (pLong >= fixedLongTop))) {
                        geoBoxCorrect = true;
                        System.out.println("geoBoxCorrect=true");
                    } else {
                        geoBoxCorrect = false;
                        System.out.println("geoBoxCorrect=false");
                    }
                } else {
                    geoBoxCorrect = false;
                }*/
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {

            }


            public void onProviderEnabled(String provider) {

            }


            public void onProviderDisabled(String provider) {


            }
        };

        //Start the location manager
        Boolean op1 = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Boolean op2 = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(op1){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, ll);
        }
        if(op2){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, ll);
        }

        //Listen to the login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*//Hide keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);*/

                //History
                //new AddHistory().execute();

                //Login
                hspName = hospitalSelected;
                if (groupSelectedId.equals("0")) {
                    AdviceUser(1);
                } else if (hospitalSelectedId.equals("0")) {
                    AdviceUser(2);
                } else {
                    new Login().execute();
                }

            }

            /*private void runQuery() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Hospitales");
                query.whereEqualTo("Codigo", id);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            Log.d("score", "The getFirst request failed.");
                        } else {
                            Log.d("id", "Retrieved the object.");
                            String status = object.getString("Hospital");
                            String status2 = object.getString("Codigo");
                            System.out.println("Hospital: " + status);
                            System.out.println("Codigo: " + status2);
                            hspObtained = status;
                            codeObtained = status2;
                            System.out.println(lat + ", " + lon);
                            fixedLatTop = object.getDouble("latTop");
                            fixedLatLow = object.getDouble("latLow");
                            fixedLongTop = object.getDouble("longTop");
                            fixedLongLow = object.getDouble("longLow");
                            if (((lat <= fixedLatTop) && (lat >= fixedLatLow) && (lon <= fixedLongLow) && (lon >= fixedLongTop))) {
                                geoBoxCorrect = true;
                                System.out.println("geoBoxCorrect=true");
                            } else {
                                geoBoxCorrect = false;
                                System.out.println("geoBoxCorrect=false");
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }*/


        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupSelected = groupSpinner.getSelectedItem().toString();
                int groupId = (int) groupSpinner.getSelectedItemId();
                Category cat = new Category(groupsList.get(groupId));
                groupSelectedId = String.valueOf(cat.getId());
                Log.d("Seleccionado: ", groupSelectedId);

                switch (groupSelected) {
                    case "Escoge":
                        Log.d("Case ", "nothing");
                        hospitalsList = new ArrayList<>();
                        break;
                    default:
                        new GetHospitals().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hospitalSelected = hospitalSpinner.getSelectedItem().toString();
                int hospitalId = (int) hospitalSpinner.getSelectedItemId();
                Category cat = new Category(hospitalsList.get(hospitalId));
                hospitalSelectedId = String.valueOf(cat.getId());
                Log.d("Seleccionado: ", hospitalSelectedId);

                switch (hospitalSelected) {
                    case "Escoge":
                        Log.d("Case ", "nothing");
                        break;
                    default:
                        new GetGeo().execute();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class Login extends AsyncTask<String, String, String> {

        AlertDialog.Builder alertDialog;

        int success;
        int geo;

        Array nameArray[];
        JSONObject jsonA;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Iniciando sesión...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            alertDialog = new AlertDialog.Builder(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... args) {

            TAG_CODE = hospitalSelectedId;
            hspCode = hospitalSelectedId;
            TAG_IMEI = imei;

            //Build the parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("code", TAG_CODE));
            params.add(new BasicNameValuePair("imei", TAG_IMEI));
            params.add(new BasicNameValuePair("ver", currentVersion));
            params.add(new BasicNameValuePair("long", String.valueOf(lon)));
            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
            params.add(new BasicNameValuePair("alt", String.valueOf(alt)));

            //Getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_login, "POST", params);

            //Check logcat for response
            Log.d("Create Response", json.toString());

            try {
                success = json.getInt(TAG_SUCCESS);
                geo = json.getInt("geo");

                //userId = ;
                Log.d("User's id: ", String.valueOf(json.get("id")));

                JSONArray array = (JSONArray) json.get("id");

                JSONObject innerObj = (JSONObject) array.get(0);

                userName = String.valueOf(innerObj.get("name"));

                userId = String.valueOf(innerObj.get("id"));

                userType = String.valueOf(innerObj.get("userType_id"));

                Log.d("User's name: ", userName);

                if (success == 1) {
                    // finish closes the current Activity
                    // finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (geo == 1) {
                if (((lat <= fixedLatTop) && (lat >= fixedLatLow) && (lon <= fixedLongLow) && (lon >= fixedLongTop))) {
                    geoBoxCorrect = true;
                    System.out.println("geoBoxCorrect=true");
                    if (success == 1) {

                        new GetOffModels().execute();

                    } else if (success == 3) {

                        alertDialog.setTitle("No hay permiso");
                        alertDialog.setMessage("No puedes acceder porque no tienes permiso");
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();

                    }
                } else {
                    geoBoxCorrect = false;
                    System.out.println("geoBoxCorrect=false");
                    alertDialog.setTitle("Locacion");
                    alertDialog.setMessage("No puedes acceder porque no estas en el area permitida");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            } else if (geo == 0) {

                if (success == 1) {

                    new GetOffModels().execute();

                } else if (success == 3) {

                    alertDialog.setTitle("No hay permiso");
                    alertDialog.setMessage("No puedes acceder porque no tienes permiso");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            }
        }
    }

    void AdviceUser (int code) {
        if(code == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Escoge un grupo primero");
            builder.setTitle("Falta un grupo");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(code == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Escoge un hospital primero");
            builder.setTitle("Falta un hospital");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(code == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("No se puede establecer una conexion con la Base de Datos. Contactar a Sistemas.");
            builder.setTitle("Error en Base de Datos");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public String getImei () {
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = mgr.getDeviceId();

        return imei;
    }

    public String getCurrentVer () {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        currentVersion = pInfo.versionName;
        versionTV.setText("ver: " + currentVersion);
        Log.i("Current version: ", currentVersion);
        new GetLatestVersion().execute();


        return currentVersion;
    }

    private class GetChallenge extends AsyncTask<Void, Void, Void> {
        int success = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<NameValuePair> paramsGetChallenge = new ArrayList<>();
                paramsGetChallenge.add(new BasicNameValuePair("user", "0"));

                JSONObject json = jsonParser.makeHttpRequest(url_get_challenge, "POST", paramsGetChallenge);

                System.out.println("ChallengeJsonObj: " + json.toString());

                success = json.getInt("success");

                if(success == 1) {
                    JSONArray usersArray = (JSONArray) json.get("users");
                    challengeList = new ArrayList<>();

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject tempInnerObj = (JSONObject) usersArray.get(i);
                        ChallengeUser tempObj = new ChallengeUser();
                        tempObj.setId(tempInnerObj.getInt("user_id"));
                        tempObj.setName(tempInnerObj.getString("user_name"));
                        tempObj.setPercent(tempInnerObj.getInt("user_percent"));

                        challengeList.add(i, tempObj);

                        System.out.println("Challenge object: " + challengeList.get(i).getName() + challengeList.get(i).getPercent());
                    }

                    System.out.println("Users array: " + usersArray);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayList<BarEntry> valsChallenge;
            ArrayList<String> ar = new ArrayList<>();
            ArrayList<BarDataSet> dataSets = new ArrayList<>();

            for(int o = 0; o < challengeList.size(); o++) {
                valsChallenge = new ArrayList<>();
                valsChallenge.add(new BarEntry(Float.parseFloat(String.valueOf(challengeList.get(o).getPercent())), 0));

                ar = new ArrayList<>();
                ar.add("Cumplimiento del mes actual");

                // generate the random integers for r, g and b value
                Random rand = new Random();
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);

                int randomColor = Color.rgb(r,g,b);

                BarDataSet bdS = new BarDataSet(valsChallenge, challengeList.get(o).getName());
                bdS.setColor(randomColor);

                dataSets.add(bdS);
            }

            String[] xVals = new String[ar.size()];
            xVals = ar.toArray(xVals);
            challengeChart.setDescription("");
            BarData data = new BarData(xVals, dataSets);
            YAxis yAxis = challengeChart.getAxisLeft();

            yAxis.setAxisMaxValue(100f);

            Legend legend = challengeChart.getLegend();

            legend.setWordWrapEnabled(true);

            challengeChart.setData(data);

            challengeChart.invalidate();
        }
    }

    private class GetLatestVersion extends AsyncTask<Void, Void, Void>{
        int update;
        String urlDwn;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> paramsGetVersion = new ArrayList<>();
                paramsGetVersion.add(new BasicNameValuePair("version", currentVersion));

                JSONObject json = jsonParser.makeHttpRequest(url_get_latest_version, "POST", paramsGetVersion);

                Log.i("Check version: ", json.toString());

                update = json.getInt("update");

                if(update == 1){
                    JSONArray array = (JSONArray) json.get("version");

                    JSONObject innerObj = (JSONObject) array.get(0);
                    Log.i("Update status:", "Available");
                    urlDwn = String.valueOf(innerObj.get("download"));
                } else if (update == 0) {
                    Log.i("Update status:", "Latest");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(update == 1) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Existe una  nueva version de Doseli");
                builder.setTitle("Actualizacion disponible");
                builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*String url = "http://" + urlDwn;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);*/

                        new Thread (new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(LoginActivity.main_url + "doseli.apk");
                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                    urlConnection.setRequestMethod("GET");
                                    urlConnection.connect();

                                    FileOutputStream fileOutput = openFileOutput("doseli.apk", Context.MODE_WORLD_READABLE);
                                    InputStream inputStream = urlConnection.getInputStream();

                                    byte[] buffer = new byte[1024];
                                    int bufferLength = 0;

                                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                                        fileOutput.write(buffer, 0, bufferLength);
                                    }
                                    fileOutput.close();


                                    File apkFile = new File(fileDir + "/doseli.apk");
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                                    startActivity(intent);
                                    finishAffinity();

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

        }
    }

    private class GetGroups extends AsyncTask<Void, Void, Void>{
        int code = 0;

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler jsonParsers = new ServiceHandler();
            String json = jsonParsers.makeServiceCall(url_get_groups, ServiceHandler.POST);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray(groupIdentifier);
                        int size = categories.length();

                        groupsList.add(0, new Category(0,"Escoge"));

                        for (int i = 0; i < size; i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Category cat = new Category(catObj.getInt("id"),
                                    catObj.getString(groupCategoryIdentifier));
                            groupsList.add(cat);
                        }
                        Log.d(groupIdentifier + ": ", groupsList.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    code = 3;
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
                code = 3;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateSpinner(groupCategoryIdentifier);
            if (code == 3) {
                AdviceUser(3);
            }
        }
    }

    private class GetHospitals extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int success;
            hospitalsList = new ArrayList<>();

            try {

                List<NameValuePair> paramsGetTargets = new ArrayList<>();
                paramsGetTargets.add(new BasicNameValuePair("group", groupSelectedId));

                JSONObject json = jsonParser.makeHttpRequest
                        (url_get_hospitals, "POST", paramsGetTargets);

                Log.d ("Check target: ", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if(success == 1){

                    Log.d("Product: ", json.getJSONArray("hospitals").toString());

                    if (json != null) {
                        try {
                            JSONArray categories = json
                                    .getJSONArray("hospitals");
                            int size = categories.length();

                            hospitalsList.add(0, new Category(0,"Escoge"));

                            for (int i = 0; i < size; i++) {
                                JSONObject catObj = (JSONObject) categories.get(i);
                                Category cat = new Category(catObj.getInt("id"),
                                        catObj.getString("hospital"));
                                hospitalsList.add(cat);
                            }
                            Log.d("Hospitals: ", hospitalsList.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("JSON Data", "Didn't receive any data from server!");
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateSpinner("hospital");
        }
    }

    private class GetGeo extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int success;

            try {

                List<NameValuePair> paramsGetTargets = new ArrayList<>();
                paramsGetTargets.add(new BasicNameValuePair("hospital", hospitalSelectedId));

                JSONObject json = jsonParser.makeHttpRequest
                        (url_get_hospital_geo, "POST", paramsGetTargets);

                Log.d ("Check target: ", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if(success == 1){

                    Log.d("Product: ", json.getJSONArray("geo").toString());

                    if (json != null) {
                        try {
                            JSONArray array = (JSONArray) json.get("geo");

                            JSONObject innerObj = (JSONObject) array.get(0);

                            fixedLatTop = Double.parseDouble(String.valueOf(innerObj.get("lat_top")));
                            fixedLongTop = Double.parseDouble(String.valueOf(innerObj.get("long_top")));
                            fixedLatLow = Double.parseDouble(String.valueOf(innerObj.get("lat_low")));
                            fixedLongLow = Double.parseDouble(String.valueOf(innerObj.get("long_low")));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("JSON Data", "Didn't receive any data from server!");
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void populateSpinner(String option) {
        List<String> lables = new ArrayList<String>();

        switch (option) {
            case "group":
                for (int i = 0; i < groupsList.size(); i++) {
                    lables.add(groupsList.get(i).getName());
                }
                break;
            case "hospital":
                for (int i = 0; i < hospitalsList.size(); i++) {
                    lables.add(hospitalsList.get(i).getName());
                }
                break;
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        switch (option) {
            case "group": groupSpinner.setAdapter(spinnerAdapter);
                break;
            case "hospital": hospitalSpinner.setAdapter(spinnerAdapter);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private class CheckInternetConnection extends AsyncTask <Void,Void,Void> {
        int result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean checkNtwk = CheckNetwork.isConnectedToServer(LoginActivity.this);

            if(checkNtwk == true) {
                result = 1;
            } else {
                result = 2;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(result == 1) {
                Log.i("Internet status: ", "Available");

                getCurrentVer();

                new GetChallenge().execute();

                new GetGroups().execute();
            } else if (result == 2) {
                Log.i("Internet status: ", "Not Available");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Esta aplicacion necesita internet como minimo para el inicio de sesion");
                builder.setTitle("Fuera de linea");
                builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        }
    }

    private class GetOffModels extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            groupsOff = new ArrayList<Category>();
            hospitalsOff = new ArrayList<Category>();
            modelsOff = new ArrayList<Category>();
            areasOff = new ArrayList<Category>();
            locationsOff = new ArrayList<Category>();

            ServiceHandler jsonParsers = new ServiceHandler();
            String json = jsonParsers.makeServiceCall(main_url + "get_models.php", ServiceHandler.POST);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray("models");
                        int size = categories.length();

                        modelsOff.add(0, new Category(0,"Escoge"));

                        for (int i = 0; i < size; i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Category cat = new Category(catObj.getInt("id"),
                                    catObj.getString("model"));
                            modelsOff.add(cat);
                        }
                        Log.d("Models: ", modelsOff.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("hospital_id", hospitalSelectedId));

            JSONObject jsonObj = jsonParser.makeHttpRequest(
                    url_get_off, "POST", param);

            Log.i("AP: ", jsonObj.toString());

            try {
                int success = jsonObj.getInt("success");

                if(success == 1){

                    if (jsonObj != null) {

                        groupsOff.add(0, new Category(Integer.parseInt(groupSelectedId), groupSelected));

                        hospitalsOff.add(0, new Category(Integer.parseInt(hospitalSelectedId), hospitalSelected));

                        JSONArray innerObjAreas = jsonObj.getJSONArray("areas");

                        int sizeAreas = innerObjAreas.length();

                        areasOff.add(0, new Category(0, "Escoge"));

                        for (int i = 0; i < sizeAreas; i++) {
                            JSONObject areaObj = (JSONObject) innerObjAreas.get(i);
                            Category catArea = new Category(areaObj.getInt("id"),
                                    areaObj.getString("name"));
                            areasOff.add(catArea);
                        }

                        Log.i("Areas: ", areasOff.toString());

                        JSONArray innerObjLocations = jsonObj.getJSONArray("locations");

                        int sizeLocations = innerObjLocations.length();

                        locationsOff.add(0, new Category(0, "Escoge"));

                        for (int i = 0; i < sizeLocations; i++) {
                            JSONObject locationsObj = (JSONObject) innerObjLocations.get(i);
                            Category catArea = new Category(locationsObj.getInt("id"),
                                    locationsObj.getString("name"));
                            locationsOff.add(catArea);
                        }

                        Log.i("Locations: ", locationsOff.toString());

                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(LoginActivity.this, "Bienvenido a " + hspName + ", " + userName, Toast.LENGTH_SHORT).show();

            Intent goHome = new Intent(LoginActivity.this, HomepageActivity.class);
            startActivity(goHome);
        }
    }

}



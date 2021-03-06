package com.exgerm.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class HandsetLocation extends AppCompatActivity {

    String hspOb;

    protected Spinner groupSpinner;
    protected Spinner hospitalSpinner;
    protected Spinner areaSpinner;
    protected Spinner locationSpinner;
    protected EditText ref;
    protected Button scan;
    protected TextView qrId;
    protected TextView qrResult;
    protected TextView qrToken;
    protected String aparato;
    protected Button update;
    protected String handset;
    protected String mid;

    protected String groupVaul = "";
    protected String hospitalVaul = "";
    protected String areaVaul = "";
    protected String locationVaul = "";
    protected String roomVaul = "";
    protected Boolean light = true;
    protected Boolean lightTwo = false;
    protected Boolean sLight = false;

    protected String hospital;

    private ArrayList<Category> groupsList;
    private String groupIdentifier = "groups";
    private String groupCategoryIdentifier = "group";
    private String groupSelectedId = "";
    private String groupSelected = "";

    private ArrayList<Category> hospitalsList;
    private String hospitalIdentifier = "hospitals";
    private String hospitalCategoryIdentifier = "hospital";
    private String hospitalSelectedId = "";
    private String hospitalSelected = "";

    private ArrayList<Category> areasList;
    private String areaCategoryIdentifier = "area";
    private String areaSelectedId = "";
    private String areaSelectedIdentifier = "";
    private String areaSelected = "";

    private ArrayList<Category> locationsList;
    private String locationCategoryIdentifier = "location";
    private String locationSelectedId = "";
    private String locationSelectedIdentifier = "";
    private String locationSelected = "";

    private ArrayList<Category> roomsList;
    private String roomCategoryIdentifier = "room";
    private String roomSelectedIdentifier = "";
    private String roomSelected = "";

    private Boolean area;
    private Boolean location;
    private Boolean room;

    private int firstBoot = 2;
    private int hospitalFirstCheck = 1;

    List<String> lablesE = new ArrayList<String>();

    // Creating adapter for spinner
    ArrayAdapter<String> spinnerAdapterE;

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;

    public String uuid;
    public Boolean uuidExists = false;

    public boolean networkAvailable;

    private static String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_ID = "id";
    private static final String TAG_MODEL = "model";
    private static final String TAG_SERIAL_NUMBER = "serial_number";

    private static String url_get_groups = LoginActivity.main_url + "get_groups";
    private static String url_get_hospitals = LoginActivity.main_url + "get_hospitals";
    private static String url_get_areas = LoginActivity.main_url + "get_areas";
    private static String url_get_locations = LoginActivity.main_url + "get_locations";
    private static String url_get_rooms = LoginActivity.main_url + "get_rooms";
    private static String url_get_qr_location = LoginActivity.main_url + "get_qr_location";
    private static String url_register_location = LoginActivity.main_url + "register_location";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handset_location);

        sharedPreferences = getSharedPreferences("DoseliPreferences", Context.MODE_PRIVATE);

        groupsList = new ArrayList<>();

        // Drop down layout style - list view with radio button
        spinnerAdapterE = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lablesE);
        spinnerAdapterE
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Intent intent = getIntent();
        groupSpinner = (Spinner) findViewById(R.id.spinnerGroupHandsetLocation);
        hospitalSpinner = (Spinner) findViewById(R.id.spinnerHospitalHandsetLocation);
        areaSpinner = (Spinner) findViewById(R.id.spinnerAreaHandsetLocation);
        locationSpinner = (Spinner) findViewById(R.id.spinnerLocationHandsetLocation);
        ref = (EditText) findViewById(R.id.etReferenceHandsetLocation);
        scan = (Button) findViewById(R.id.btnScanHandsetLocation);
        qrId = (TextView) findViewById(R.id.tvHandsetHandsetLocation);
        qrResult = (TextView) findViewById(R.id.idQr);
        qrToken = (TextView) findViewById(R.id.token);
        update = (Button) findViewById(R.id.updateLocation);

        networkAvailable = isOnline();

        if(networkAvailable == true) {

            new GetGroups().execute();

        } else if (networkAvailable == false) {

            groupsList = LoginActivity.groupsOff;
            hospitalsList = LoginActivity.hospitalsOff;
            areasList = LoginActivity.areasOff;
            locationsList = LoginActivity.locationsOff;

            populateSpinner("group");
            populateSpinner("hospital");
            populateSpinner("area");
            populateSpinner("location");

        }

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //new GetHospitals().execute();
                groupSelected = groupSpinner.getSelectedItem().toString();
                int groupId = (int) groupSpinner.getSelectedItemId();
                Category cat = new Category(groupsList.get(groupId));
                groupSelectedId = String.valueOf(cat.getId());
                Log.d("Seleccionado: ", groupSelectedId);
                Log.i("Boot: ", String.valueOf(firstBoot));

                if (networkAvailable == true) {
                    switch (groupSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            hospitalSpinner.setAdapter(spinnerAdapterE);
                            areaSpinner.setAdapter(spinnerAdapterE);
                            areaSelectedId = "";
                            locationSpinner.setAdapter(spinnerAdapterE);
                            locationSelectedId = "";
                            break;
                        default:
                            light = true;
                            new GetHospitals().execute();
                    }
                } else if (networkAvailable == false) {
                    switch (groupSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            areaSelectedId = "";
                            locationSelectedId = "";
                            break;
                        default:
                            light = true;
                    }
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

                if (networkAvailable == true) {
                    switch (hospitalSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            areaSpinner.setAdapter(spinnerAdapterE);
                            areaSelectedId = "";
                            locationSpinner.setAdapter(spinnerAdapterE);
                            locationSelectedId = "";
                            break;
                        default:
                            area = false;
                            light = true;
                            new GetAreas().execute();
                    }
                } else if (networkAvailable == false) {
                    switch (hospitalSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            areaSelectedId = "";
                            locationSelectedId = "";
                            break;
                        default:
                            area = false;
                            light = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSelected = areaSpinner.getSelectedItem().toString();

                if(networkAvailable == true) {

                    switch (areaSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            locationSpinner.setAdapter(spinnerAdapterE);
                            locationSelectedId = "";
                            break;
                        default:
                            int areaId = (int) areaSpinner.getSelectedItemId();
                            Category cat = new Category(areasList.get(areaId));
                            areaSelectedId = String.valueOf(cat.getId());
                            Log.d("Seleccionado: ", areaSelectedId);
                            area = true;
                            light = true;
                            new GetLocations().execute();
                    }

                } else if (networkAvailable == false) {
                    switch (areaSelected) {
                        case "Escoge":
                            Log.d("Case ", "nothing");
                            light = false;
                            locationSelectedId = "";
                            break;
                        default:
                            int areaId = (int) areaSpinner.getSelectedItemId();
                            Category cat = new Category(areasList.get(areaId));
                            areaSelectedId = String.valueOf(cat.getId());
                            Log.d("Seleccionado: ", areaSelectedId);
                            area = true;
                            light = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationSelected = locationSpinner.getSelectedItem().toString();

                switch (locationSelected) {
                    case "Escoge":
                        Log.d("Case ", "nothing");
                        light = false;
                        break;
                    default:
                        int locationId = (int) locationSpinner.getSelectedItemId();
                        Category cat = new Category(locationsList.get(locationId));
                        locationSelectedId = String.valueOf(cat.getId());
                        Log.d("Seleccionado: ", locationSelectedId);
                        location = true;
                        light = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(HandsetLocation.this);
                integrator.setCaptureActivity(CaptureLayout.class);
                integrator.setPrompt("Escanea un dosificador");
                integrator.initiateScan();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupSpinner.getSelectedItem().toString() != null) {
                    String gVal = groupSpinner.getSelectedItem().toString();
                    roomSelected = ref.getText().toString();
                /*if(areaSpinner.getSelectedItem() == null){
                    areaVaul = "";
                    area = false;
                } else {
                    String aVal = areaSpinner.getSelectedItem().toString();
                    area = true;
                }

                if(locationSpinner.getSelectedItem() == null){
                    areaVaul = "";
                    location = false;
                } else {
                    String lVal = locationSpinner.getSelectedItem().toString();
                    location = true;

                }

                if(roomSpinner.getSelectedItem() == null){
                    roomVaul = "";
                    room = false;
                } else {
                    String rVal = roomSpinner.getSelectedItem().toString();
                    room = true;
                }*/
                    if ((!(groupSpinner.getSelectedItem() == null)) && (!(gVal.equals("Escoge")))) {
                        System.out.println("Group correct");
                        groupVaul = groupSpinner.getSelectedItem().toString();
                    } else {
                        System.out.println("Red light");
                        light = false;
                    }
                    if ((!(hospitalSpinner.getSelectedItem() == null)) && (!(hospitalSpinner.getSelectedItem().toString().equals("Escoge")))) {
                        System.out.println("Hospital correct");
                        hospitalVaul = hospitalSpinner.getSelectedItem().toString();
                    } else {
                        System.out.println("Red light");
                        light = false;
                    }
                    if (light && sLight == true) {

                        if (networkAvailable == true) {

                            new SetLocation().execute();

                        } else if (networkAvailable == false) {
                            LoginActivity.offlineDb.execSQL("INSERT INTO DoseliPosicion " +
                                    "(token, group_id, hospital_id, area_id, location_id, reference, location_set_by) " +
                                    "VALUES ('" + uuid + "', '" + groupSelectedId + "', " +
                                    "'" + hospitalSelectedId + "', " +
                                    "'" + areaSelectedId + "', " +
                                    "'" + locationSelectedId + "', " +
                                    "'" + roomSelected + "', " +
                                    "'" + sharedPreferences.getString("user_id", "34") + "')");
                            AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                            builder.setTitle("Fuera de linea");
                            builder.setMessage("Guardado en pendientes para mandar despues");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder.show();

                        }

                    } else if (sLight == false) {
                        Log.d("error", "Falta aparato.");
                        AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                        builder.setMessage("Debes escanear un aparato para poder posicionarlo");
                        builder.setTitle("Escanea un aparato");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //Close the dialog
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Log.d("error", "Falta campos.");
                        AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                        builder.setMessage("Debes llenar todos los campos para poder actualizar los datos");
                        builder.setTitle("Revisa tus datos");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //Close the dialog
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (resultCode == RESULT_OK) {
                qrId.setText("Cargando");
                String contents = intent.getStringExtra("SCAN_RESULT"); //this is the result
                qrResult.setText(contents);

                final URI uri = URI.create(contents);
                final String path = uri.getPath();

                int pos = -1;

                for (int i = 1; i <= 1; i++) {
                    pos = path.indexOf('/', pos + 1);

                }

                //Ej de URL exgerm.com/bdb75f0f769444989a02c8da465c6819
                //Token: /08-01-2015/fecha /1/hospital /2/aparato
                String forId = path.substring(path.lastIndexOf('/') + 1);

                //tvHandsetName.setText(forAid);
                qrToken.setText("" + forId);
                uuid = forId;

                if (networkAvailable == true) {
                    new CheckQR().execute();
                } else if (networkAvailable == false) {
                    sLight = true;
                    qrId.setText("Fuera de linea");
                }


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    private void runQuery() {
        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Series");
        query.whereEqualTo("UUID", qrToken.getText());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                Boolean used = object.getBoolean("Usado");
                String mod = object.getString("Modelo");
                String status = object.getString("Serie");
                aparato = mod + status;
                if (!aparato.equals("nullnull")) {
                    tvHandsetName.setText(aparato);
                    sLight = true;
                } else {
                    tvHandsetName.setText("");
                    lightTwo = false;
                }

                if (object == null || used == null) {
                    Log.d("error", "Aparato no existente.");
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                    builder.setMessage("Este aparato no existe");
                    builder.setTitle("Vuelve a intentar");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //Close the dialog
                            dialogInterface.dismiss();
                        }
                    });

                    tvHandsetName.setText("");
                    lightTwo = false;

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (used == false){
                    Log.d("error", "Aparato sin alta.");
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                    builder.setMessage("Este aparato no esta dado de alta");
                    builder.setTitle("Vuelve a intentar");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //Close the dialog
                            dialogInterface.dismiss();
                        }
                    });

                    tvHandsetName.setText("");
                    lightTwo = false;

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Boolean updated = object.getBoolean("location_complete");
                    if(updated == true){
                        Log.d("error", "Aparato con locación previa");
                        AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                        builder.setMessage("Este aparato ya ha sido posicionado, puede volver a establecer la locación para corregir errores");
                        builder.setTitle("Locación detectada");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //Close the dialog
                                dialogInterface.dismiss();
                            }
                        });


                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if (updated == false) {

                        if (!aparato.equals("nullnull")) {
                            tvHandsetName.setText(aparato);
                            sLight = true;
                        } else {
                            tvHandsetName.setText("");
                            //There was an error
                            AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                            builder.setMessage("Este aparato no esta dado de alta");
                            builder.setTitle("Vuelve a intentar");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //Close the dialog
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }


                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException valPhysicalDamage) {
                        valPhysicalDamage.printStackTrace();
                    }
                }
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handset_location, menu);
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
            case "area":
                for (int i = 0; i < areasList.size(); i++) {
                    lables.add(areasList.get(i).getName());
                }
                break;
            case "location":
                for (int i = 0; i < locationsList.size(); i++) {
                    lables.add(locationsList.get(i).getName());
                }
                break;
            case "room":
                for (int i = 0; i < roomsList.size(); i++) {
                    lables.add(roomsList.get(i).getName());
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
            case "area": areaSpinner.setAdapter(spinnerAdapter);
                break;
            case "location": locationSpinner.setAdapter(spinnerAdapter);
                break;
        }

        firstBoot();
    }

    public void firstBoot () {

        if (firstBoot == 1){
            int flag = -1;

            for (int i = 0; i < hospitalsList.size(); i++) {

                if (hospitalsList.get(i).getId() == Integer.parseInt(LoginActivity.hospitalSelectedId)){
                    flag = i;
                }

            }

            if(flag != -1) {
                hospitalSpinner.setSelection(flag);
            }

            firstBoot--;
        }

        if (firstBoot == 2) {
            int flag = -1;

            for (int i = 0; i < groupsList.size(); i++) {

                if (groupsList.get(i).getId() == Integer.parseInt(LoginActivity.groupSelectedId)){
                    flag = i;
                }

            }

            if(flag != -1) {
                groupSpinner.setSelection(flag);
            }

            firstBoot--;
        }

    }

    class CheckQR extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HandsetLocation.this);
            pDialog.setMessage("Cargando codigo. Espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            String tokenT = uuid;

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                paramsGetDetails.add(new BasicNameValuePair("token", tokenT));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_qr_location, "POST", paramsGetDetails);

                // check your log for json response
                Log.d("Single Product Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    sLight = true;

                    uuidExists = true;
                    // successfully received product details
                    JSONArray productObj = json
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    JSONObject product = productObj.getJSONObject(0);

                    // product with this pid found
                    handset = product.getString(TAG_MODEL) + product.getString(TAG_SERIAL_NUMBER);
                    mid = product.getString(TAG_ID);
                    Log.d("Aparato: ", handset);


                } else {
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            // The upwards operation is async, you need to wait until it finishes to asign the variable
            if (uuidExists == true) {
                qrId.setText(handset);
            } else if(uuidExists == false){
                qrId.setText("");
                AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                builder.setMessage("Este codigo no existe");
                builder.setTitle("Vuelve a intentar");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    private class SetLocation extends AsyncTask<String, String, String> {

        int success;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HandsetLocation.this);
            pDialog.setMessage("Posicionando aparato...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", uuid));
            params.add(new BasicNameValuePair("group", groupSelected));
            params.add(new BasicNameValuePair("group_id", groupSelectedId));
            params.add(new BasicNameValuePair("hospital", hospitalSelected));
            params.add(new BasicNameValuePair("hospital_id", hospitalSelectedId));
            params.add(new BasicNameValuePair("area", areaSelected));
            params.add(new BasicNameValuePair("area_id", areaSelectedId));
            params.add(new BasicNameValuePair("location", locationSelected));
            params.add(new BasicNameValuePair("location_id", locationSelectedId));
            params.add(new BasicNameValuePair("location_set_by", LoginActivity.userId));

            if(!roomSelected.equals("")) {
                Log.i("Added", "r");
                params.add(new BasicNameValuePair("room", roomSelected));
            }

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_register_location,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    /*Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    startActivity(i);*/

                    // closing this screen
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                builder.setMessage("Aparato posicionado con exito");
                builder.setTitle("Listo");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                if(!isFinishing()) {
                    builder.show();
                }

                sLight = false;
            } else if (success == 3) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HandsetLocation.this);
                builder.setMessage("No tienes permiso para realizar esta acción");
                builder.setTitle("Falta permiso");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                if(!isFinishing()) {
                    builder.show();
                }
            }
        }
    }

    private class GetGroups extends AsyncTask<Void, Void, Void>{

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
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateSpinner(groupCategoryIdentifier);
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

    private class GetAreas extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int success;
            areasList = new ArrayList<>();

            try {

                List<NameValuePair> paramsGetTargets = new ArrayList<>();
                paramsGetTargets.add(new BasicNameValuePair(hospitalCategoryIdentifier, hospitalSelectedId));

                JSONObject json = jsonParser.makeHttpRequest
                        (url_get_areas, "POST", paramsGetTargets);

                Log.d ("Check area: ", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if(success == 1){

                    Log.d("Product: ", json.getJSONArray("areas").toString());

                    if (json != null) {
                        try {
                            JSONArray categories = json
                                    .getJSONArray("areas");
                            int size = categories.length();

                            areasList.add(0, new Category(0,"Escoge"));

                            for (int i = 0; i < size; i++) {
                                JSONObject catObj = (JSONObject) categories.get(i);
                                Category cat = new Category(catObj.getInt("id"),
                                        catObj.getString("area"));
                                areasList.add(cat);
                            }
                            Log.d("Areas: ", areasList.toString());


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
            populateSpinner("area");
        }
    }

    private class GetLocations extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int success;
            locationsList = new ArrayList<>();

            try {

                List<NameValuePair> paramsGetTargets = new ArrayList<>();
                paramsGetTargets.add(new BasicNameValuePair(hospitalCategoryIdentifier, hospitalSelectedId));

                JSONObject json = jsonParser.makeHttpRequest
                        (url_get_locations, "POST", paramsGetTargets);

                Log.d ("Check locations: ", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if(success == 1){

                    Log.d("Product: ", json.getJSONArray("locations").toString());

                    if (json != null) {
                        try {
                            JSONArray categories = json
                                    .getJSONArray("locations");
                            int size = categories.length();

                            locationsList.add(0, new Category(0,"Escoge"));

                            for (int i = 0; i < size; i++) {
                                JSONObject catObj = (JSONObject) categories.get(i);
                                Category cat = new Category(catObj.getInt("id"),
                                        catObj.getString("location"));
                                locationsList.add(cat);
                            }
                            Log.d("Locations: ", locationsList.toString());


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
            populateSpinner("location");
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}

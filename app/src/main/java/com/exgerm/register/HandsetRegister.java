package com.exgerm.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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


public class HandsetRegister extends AppCompatActivity {

    private Button scan;
    private Button link;
    private Spinner spin;


    private EditText serie;
    private TextView state;

    private TextView id;

    private static String url_register_machine = LoginActivity.main_url + "register_machine.php";

    private static String url_get_qr_details = LoginActivity.main_url + "get_qr_details.php";

    private static String url_get_qr_availability = LoginActivity.main_url + "get_qr_availability.php";

    private static String url_get_models = LoginActivity.main_url + "get_models.php";

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private boolean networkConnection;
    
    //Array for spinner
    private ArrayList<Category> modelsList;

    public String uuid;
    public String uuidId;
    public Boolean uuidUsed;
    public Boolean uuidExists = false;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_TOKEN = "token";
    private static String TAG_MODEL = "model";
    private static String TAG_SERIAL_NUMBER = "serial_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handset_register);

        modelsList = new ArrayList<Category>();

        scan = (Button) findViewById(R.id.scan);
        link = (Button) findViewById(R.id.link);
        spin = (Spinner) findViewById(R.id.spinner);
        serie = (EditText) findViewById(R.id.serie);
        state = (TextView) findViewById(R.id.state);
        id = (TextView) findViewById(R.id.id);

        //Add items
        networkConnection = isOnline();

        if(networkConnection == true) {

            new GetCategories().execute();

        } else {
            modelsList = LoginActivity.modelsOff;
            populateSpinner();
        }

        scan.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        IntentIntegrator integrator = new IntentIntegrator(HandsetRegister.this);
                                        integrator.setCaptureActivity(CaptureLayout.class);
                                        integrator.setPrompt("Escanea un dosificador");
                                        integrator.initiateScan();
                                    }
                                }

        );

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence sta = state.getText();
                final String modelo = spin.getSelectedItem().toString();
                final String unique = serie.getText().toString();
                if (sta.equals("DISPONIBLE") || sta.equals("Fuera de linea")) {
                    if (unique.length() < 7) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
                        builder.setTitle("Longitud incompleta");
                        builder.setMessage("La serie debe ser de minimo 7 digitos despues del tipo de aparato");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //Close the dialog
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if (!modelo.equals("Escoge")) {
                        if (networkConnection == true) {
                            new RegisterQR().execute();
                        } else if (networkConnection == false) {
                            String modText = spin.getSelectedItem().toString();
                            String serieText = serie.getText().toString();
                            LoginActivity.offlineDb.execSQL("INSERT INTO DoseliAltas " +
                                    "(model, serial_number, associated_by, token) " +
                                    "VALUES ('" + modText + "', '" + serieText + "', " +
                                    "'" + LoginActivity.userId + "', '" + uuid + "');");
                            AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
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
                    } else {
                        //There was an error
                        AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
                        builder.setMessage("Escoge un tipo de modelo");
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
                } else if (sta.equals("USADO")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
                    builder.setMessage("Este codigo ya fue asignado a un aparato, utiliza otro");
                    builder.setTitle("Codigo usado");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //Close the dialog
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (sta.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
                    builder.setMessage("Debes escanear un codigo antes de continuar");
                    builder.setTitle("Codigo invalido");
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
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (resultCode == RESULT_OK) {
                //Handle result
                id.setText("Cargando");
                state.setText("CARGANDO");
                String contents = intent.getStringExtra("SCAN_RESULT"); //this is the result

                final URI uri = URI.create(contents);
                final String path = uri.getPath();

                int pos = -1;

                for (int i = 1; i <= 1; i++) {
                    pos = path.indexOf('/', pos + 1);
                }

                String forId = path.substring(path.lastIndexOf('/') + 1);

                id.setText("" + forId);

                uuid = forId;

                if(networkConnection == true) {

                    new CheckQR().execute();

                } else if (networkConnection == false) {

                    id.setText("Fuera de linea");
                    state.setText("Fuera de linea");

                }


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    class CheckQR extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HandsetRegister.this);
            pDialog.setMessage("Cargando codigo. Espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                paramsGetDetails.add(new BasicNameValuePair("uuid", uuid));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_qr_details, "POST", paramsGetDetails);

                // check your log for json response
                Log.d("Check QR: ", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productObj = json
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    JSONObject product = productObj.getJSONObject(0);

                    uuidId = product.getString("id");

                    Log.d("ID: ", uuidId);

                    // product with this pid found
                    uuidExists = true;

                    // Check for success tag
                    int success2;
                    try {
                        // Building Parameters
                        List<NameValuePair> paramsGetDetails2 = new ArrayList<NameValuePair>();
                        paramsGetDetails2.add(new BasicNameValuePair("uuid", uuid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json2 = jsonParser.makeHttpRequest(
                                url_get_qr_availability, "POST", paramsGetDetails);

                        // check your log for json response
                        Log.d("QR Availablity: ", json2.toString());

                        // json success tag
                        success2 = json2.getInt(TAG_SUCCESS);

                        if (success2 == 1) {
                            // successfully received product details
                            JSONArray productObj2 = json2
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product2 = productObj2.getJSONObject(0);
                            uuidUsed = true;

                            // product with this pid found
                            Log.d("exito", "QR Usado");
                            /*state.setTextColor(0xFFA91304);
                            state.setText("USADO");*/

                        }else{
                            uuidUsed = false;
                            // product with pid not found
                            Log.d("exito", "QR Disponible");
                            /*state.setTextColor(0xFF00B006);
                            state.setText("DISPONIBLE");*/
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    // product with pid not found
                    uuidExists = false;
                    Log.d("exito", "QR sin existencia");
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
                if (uuidUsed) {
                    state.setTextColor(0xFFA91304);
                    state.setText("USADO");
                } else {
                    state.setTextColor(0xFF00B006);
                    state.setText("DISPONIBLE");
                }
            } else if(uuidExists == false){
                state.setText("");
                AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
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

    class RegisterQR extends AsyncTask<String, String, String> {

        int success;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HandsetRegister.this);
            pDialog.setMessage("Ligando aparato...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            TAG_MODEL = spin.getSelectedItem().toString();
            TAG_SERIAL_NUMBER = serie.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", uuidId));
            params.add(new BasicNameValuePair("token", uuid));
            params.add(new BasicNameValuePair("model", TAG_MODEL));
            params.add(new BasicNameValuePair("serial_number", TAG_SERIAL_NUMBER));
            params.add(new BasicNameValuePair("user", LoginActivity.userId));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_register_machine,
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HandsetRegister.this);
                builder.setMessage("El aparato ha sido ligado con el codigo");
                builder.setTitle("Listo");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

    }

    private class GetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HandsetRegister.this);
            pDialog.setMessage("Obteniendo modelos...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(url_get_models, ServiceHandler.POST);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray("models");
                        int size = categories.length();

                        modelsList.add(0, new Category(0,"Escoge"));

                        for (int i = 0; i < size; i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Category cat = new Category(catObj.getInt("id"),
                                    catObj.getString("model"));
                            modelsList.add(cat);
                        }
                        Log.d("Models: ", modelsList.toString());
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
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            populateSpinner();
        }

    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < modelsList.size(); i++) {
            lables.add(modelsList.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spin.setAdapter(spinnerAdapter);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}



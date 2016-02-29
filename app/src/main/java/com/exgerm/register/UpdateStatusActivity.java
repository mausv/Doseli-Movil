package com.exgerm.register;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class UpdateStatusActivity extends AppCompatActivity {

    String hspOb = LoginActivity.hspCode;

    protected Button mStatusButton;
    protected Button mQrButton;
    protected TextView tvHandsetName;
    protected String aparato = "";
    protected String comment = "";

    // Report items
    protected EditText tvComment;
    protected CheckBox cbPhysicalDamage;
    protected CheckBox cbLowBattery;
    protected CheckBox cbLowLiquid;
    protected CheckBox cbPhysicalRepair;
    protected CheckBox cbChangeBattery;
    protected CheckBox cbChangeLiquid;
    protected CheckBox cbTrayClean;
    protected CheckBox cbHandsetClean;
    protected CheckBox cbError;
    protected CheckBox cbNoError;

    // Report items values
    protected String valPhysicalDamage = "0";
    protected String valLowBattery = "0";
    protected String valLowLiquid = "0";
    protected String valPhysicalRepair = "0";
    protected String valChangeBattery = "0";
    protected String valChangeLiquid = "0";
    protected String valTrayClean = "0";
    protected String valHandsetClean = "0";
    protected String mid = "0";
    protected String valHandsetStatus = "";
    protected String valHandsetStatus2 = "-1";

    protected View.OnClickListener checkBoxListener1;
    protected View.OnClickListener checkBoxListener2;

    private static String url_report = LoginActivity.main_url + "create_report.php";

    private static String url_get_details = LoginActivity.main_url + "get_machine_details.php";

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    public String token = "";
    public Boolean uuidExists = false;

    //public Boolean networkConnection = false;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_ID = "id";
    private static final String TAG_MODEL = "model";
    private static final String TAG_SERIAL_NUMBER = "serial_number";
    private static final String TAG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        Log.d("IMEI: ", LoginActivity.imei);

        /*if(!isOnline()){
            Log.i("Internet status: ", "Not Available");
            networkConnection = false;
        } else {
            Log.i("Internet status: ", "Available");
            networkConnection = true;
        }*/

        //Initialize objects
        tvComment = (EditText) findViewById(R.id.tvComment);
        mStatusButton = (Button) findViewById(R.id.btnSendReport);
        mQrButton = (Button) findViewById(R.id.btnQrScan);
        tvHandsetName = (TextView) findViewById(R.id.tvHandsetId);

        cbPhysicalDamage = (CheckBox) findViewById(R.id.cbErrorPhysicalDamage);
        cbLowBattery = (CheckBox) findViewById(R.id.cbLowBattery);
        cbLowLiquid = (CheckBox) findViewById(R.id.cbLowLiquid);
        cbPhysicalRepair = (CheckBox) findViewById(R.id.cbPhysicalRepair);
        cbChangeBattery = (CheckBox) findViewById(R.id.cbChangeBattery);
        cbChangeLiquid = (CheckBox) findViewById(R.id.cbChangeLiquid);
        cbTrayClean = (CheckBox) findViewById(R.id.cbTrayClean);
        cbHandsetClean = (CheckBox) findViewById(R.id.cbHandsetClean);
        cbError = (CheckBox) findViewById(R.id.cbError);
        cbNoError = (CheckBox) findViewById(R.id.cbNoError);

        checkBoxListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbNoError.isChecked()) {
                    valHandsetStatus = ("" + cbNoError.getText().toString());
                    cbError.setChecked(false);
                }

                if (cbError.isChecked()) {
                    valHandsetStatus = ("" + cbError.getText().toString());
                    cbNoError.setChecked(false);
                }

                if (!cbError.isChecked() && !cbNoError.isChecked()) {
                    valHandsetStatus = ("");
                }
            }
        };

        checkBoxListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbError.isChecked()) {
                    valHandsetStatus = ("" + cbError.getText().toString());
                    cbNoError.setChecked(false);
                }

                if (cbNoError.isChecked()) {
                    valHandsetStatus = ("" + cbNoError.getText().toString());
                    cbError.setChecked(false);
                }

                if (!cbError.isChecked() && !cbNoError.isChecked()) {
                    valHandsetStatus = ("");
                }
            }
        };

        cbNoError.setOnClickListener(checkBoxListener1);
        cbError.setOnClickListener(checkBoxListener2);


        //Set listener to button click
        mStatusButton.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {

                                                 Boolean verifyStatus = false;

                                                 //Get the status the user entered and convert to string
                                                 comment = tvComment.getText().toString();
                                                 if(!valHandsetStatus.equals("")) {
                                                     if (valHandsetStatus.equals("Funciona")) {
                                                         valHandsetStatus2 = "1";
                                                     } else if (valHandsetStatus.equals("Errores")) {
                                                         valHandsetStatus2 = "0";
                                                     }
                                                     verifyStatus = true;
                                                 }
                                                 String id = tvHandsetName.getText().toString();



                                                 if (id.isEmpty()) {
                                                     //There was an error
                                                     AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                                                     builder.setMessage("Debes escanear un aparato primero");
                                                     builder.setTitle("Volver a intentar");
                                                     builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int which) {
                                                             //Close the dialog
                                                             dialogInterface.dismiss();
                                                         }
                                                     });

                                                     AlertDialog dialog = builder.create();
                                                     dialog.show();

                                                 } else if (id.equals("Cargando")) {
                                                     //There was an error
                                                     AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                                                     builder.setMessage("Debes esperar a que termine de cargar el aparato");
                                                     builder.setTitle("Volver a intentar");
                                                     builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int which) {
                                                             //Close the dialog
                                                             dialogInterface.dismiss();
                                                         }
                                                     });

                                                     AlertDialog dialog = builder.create();
                                                     dialog.show();

                                                 } else if (token.equals("") || token.equals("loading")) {
                                                     //There was an error
                                                     AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                                                     builder.setMessage("Debes escanear un aparato");
                                                     builder.setTitle("Volver a intentar");
                                                     builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int which) {
                                                             //Close the dialog
                                                             dialogInterface.dismiss();
                                                         }
                                                     });

                                                     AlertDialog dialog = builder.create();
                                                     dialog.show();

                                                 } else if (!verifyStatus){
                                                     //There was an error
                                                     AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                                                     builder.setMessage("No puedes dejar el estado sin contestar");
                                                     builder.setTitle("Volver a intentar");
                                                     builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int which) {
                                                             //Close the dialog
                                                             dialogInterface.dismiss();
                                                         }
                                                     });

                                                     AlertDialog dialog = builder.create();
                                                     dialog.show();
                                                 } else if (verifyStatus && !token.equals("loading") && !id.equals("Cargando")) {

                                                     if (comment.isEmpty()) {
                                                         comment = "";
                                                     }

                                                     if (cbPhysicalDamage.isChecked()) {
                                                         valPhysicalDamage = "1";
                                                     }

                                                     if (cbLowBattery.isChecked()) {
                                                         valLowBattery = "1";
                                                     }

                                                     if (cbLowLiquid.isChecked()) {
                                                         valLowLiquid = "1";
                                                     }

                                                     if (cbPhysicalRepair.isChecked()) {
                                                         valPhysicalRepair = "1";
                                                     }

                                                     if (cbChangeBattery.isChecked()) {
                                                         valChangeBattery = "1";
                                                     }

                                                     if (cbChangeLiquid.isChecked()) {
                                                         valChangeLiquid = "1";
                                                     }

                                                     if (cbTrayClean.isChecked()) {
                                                         valTrayClean = "1";
                                                     }

                                                     if (cbHandsetClean.isChecked()) {
                                                         valHandsetClean = "1";
                                                     }

                                                     //Store the status entered by user
                                                     //String hsp = currentUser.get("Hospital").toString();
                                                     System.out.println(hspOb);
                                                     // Building Parameters
                                                     boolean networkConnection = isOnline();
                                                     if(networkConnection){
                                                         new CreateNewProduct().execute();
                                                     } else if(!networkConnection){
                                                         LoginActivity.offlineDb.execSQL("INSERT INTO DoseliOffline " +
                                                                 "(token, state, device_comment, users_id, user_name, " +
                                                                 "lowBattery, changeBattery, lowLiquid, changeLiquid, " +
                                                                 "physicalDamage, physicalRepair, trayClean, machineClean, " +
                                                                 "hospitals_id, hospital_name) " +
                                                                 "VALUES('" + token + "', '" + valHandsetStatus2 + "', " +
                                                                 "'" + comment + "', '" + LoginActivity.userId + "', " +
                                                                 "'" + LoginActivity.userName + "', '" + valLowBattery + "', '" + valChangeBattery + "', '" + valLowLiquid + "', " +
                                                                 "'" + valChangeLiquid + "', '" + valPhysicalDamage + "', '" + valPhysicalRepair + "', '" + valTrayClean + "', '" + valHandsetClean + "', " +
                                                                 "'" + LoginActivity.hospitalSelectedId + "', " +
                                                                 "'" + LoginActivity.hospitalSelected + "');");
                                                         LoginActivity.offlineMissingHandsets.removeHandsetIfExists(token);
                                                         AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                                                         builder.setTitle("Fuera de línea");
                                                         builder.setMessage("Guardado en pendientes para mandar despues");
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
                                         }

        );

        mQrButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 IntentIntegrator integrator = new IntentIntegrator(UpdateStatusActivity.this);
                 integrator.setCaptureActivity(CaptureLayout.class);
                 integrator.setPrompt("Escanea un dosificador");
                 integrator.initiateScan();

             }
         }

        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {

            if (resultCode == RESULT_OK) {

                tvHandsetName.setText("Cargando");
                token = "loading";
                String contents = intent.getStringExtra("SCAN_RESULT"); //this is the result

                final URI uri = URI.create(contents);
                final String path = uri.getPath();
                //String token = path.substring(path.lastIndexOf('/') + 1); // will return what you want
                //String aid = path.substring(path.indexOf('/', path.indexOf('/')) + 1);

                int pos = -1;
                //int pos2 = 0;
                //int pos2Fin = 0;
                //int pos2Fin2 = 0;

                for (int i = 1; i <= 1; i++) {
                    pos = path.indexOf('/', pos + 1);
                    /*if (i == 3) {
                        pos2 = pos;
                    }

                    if (i == 4) {
                        pos2Fin = pos;
                    }

                    if (i == 5) {
                        pos2Fin2 = pos;
                    }*/

                }

                //Ej de URL exgerm.com/bdb75f0f769444989a02c8da465c6819
                String forId = path.substring(path.lastIndexOf('/') + 1);
                token = "" + forId;
                System.out.println(token);

                boolean networkConnection = isOnline();

                if(networkConnection) {

                    new GetProductDetails().execute();

                } else if (!networkConnection) {

                    tvHandsetName.setText("Fuera de línea");

                }


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateStatusActivity.this);
            pDialog.setMessage("Creando ...");
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
            params.add(new BasicNameValuePair("machines_id", mid));
            params.add(new BasicNameValuePair("state", valHandsetStatus2));
            params.add(new BasicNameValuePair("comment", comment));
            params.add(new BasicNameValuePair("users_id", LoginActivity.userId));
            params.add(new BasicNameValuePair("user_name", LoginActivity.userName));
            params.add(new BasicNameValuePair("lowBattery", valLowBattery));
            params.add(new BasicNameValuePair("changeBattery", valChangeBattery));
            params.add(new BasicNameValuePair("lowLiquid", valLowLiquid));
            params.add(new BasicNameValuePair("changeLiquid", valChangeLiquid));
            params.add(new BasicNameValuePair("physicalDamage", valPhysicalDamage));
            params.add(new BasicNameValuePair("physicalRepair", valPhysicalRepair));
            params.add(new BasicNameValuePair("trayClean", valTrayClean));
            params.add(new BasicNameValuePair("machineClean", valHandsetClean));
            params.add(new BasicNameValuePair("hospitals_id", LoginActivity.hospitalSelectedId));
            params.add(new BasicNameValuePair("hospital_name", LoginActivity.hospitalSelected));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_report,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            LoginActivity.offlineMissingHandsets.removeHandsetIfExists(token);
            Toast.makeText(UpdateStatusActivity.this, "Reporte enviado", Toast.LENGTH_SHORT).show();
            Intent a = new Intent(UpdateStatusActivity.this, HomepageActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            finish();
        }

    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent a = new Intent(this, HomepageActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductDetails extends AsyncTask<String, String, String> {

        int success;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateStatusActivity.this);
            pDialog.setMessage("Cargando aparato. Espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

                    // Check for success tag
                    try {
                        // Building Parameters
                        List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                        paramsGetDetails.add(new BasicNameValuePair("token", token));
                        paramsGetDetails.add(new BasicNameValuePair("current_hospitals_id", hspOb));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_get_details, "POST", paramsGetDetails);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            aparato = product.getString(TAG_MODEL) + product.getString(TAG_SERIAL_NUMBER);
                            mid = product.getString(TAG_ID);
                            Log.d("Aparato: ", aparato);
                            uuidExists = true;
                        } else if (success == 0){
                            // product with pid not found
                            uuidExists = false;
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
                if (!aparato.equals("")) {
                    tvHandsetName.setText(aparato);
                } else {
                    tvHandsetName.setText("");
                    //There was an error
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
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

                if (success == 2) {
                    // Wrong hospital
                    Log.d("Wrong hospital", "bleh");
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                    builder.setMessage("Este aparato no corresponde a este hospital");
                    builder.setTitle("Vuelve a intentar");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    tvHandsetName.setText("");
                }
            } else if (uuidExists == false) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                builder.setTitle("Vuelve a intentar");
                builder.setMessage("Este codigo no existe o no corresponde a este hospital");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                tvHandsetName.setText("");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_status, menu);
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



package com.exgerm.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpdateStatusActivity extends Activity {

    String hspOb = LoginActivity.hspCode;

    protected EditText mStatusText;
    protected Button mStatusButton;
    protected Button mQrButton;
    protected TextView qrResult;
    protected TextView qrToken;
    protected TextView qrId;
    protected TextView qrHsp;
    protected String aparato = "";
    protected String newStatus = "";
    protected String estado;
    public TextView idMachine;

    protected CheckBox cbE1;
    protected CheckBox cbE2;
    protected CheckBox cbE3;
    protected CheckBox cbS1;
    protected CheckBox cbS2;
    protected CheckBox cbS3;
    public String e1 = "0";
    public String e2 = "0";
    public String e3 = "0";
    public String s1 = "0";
    public String s2 = "0";
    public String s3 = "0";
    public String mid = "0";

    protected CheckBox cbS;
    protected CheckBox cbW;
    protected View.OnClickListener checkBoxListener1;
    protected View.OnClickListener checkBoxListener2;

    protected TextView tv;

    private static String url_report = LoginActivity.main_url + "create_report.php";

    private static String url_get_details = LoginActivity.main_url + "get_machine_details.php";

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    public String token;
    public Boolean uuidExists = false;

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

        //Initialize objects
        mStatusText = (EditText) findViewById(R.id.statusTextBox);
        mStatusButton = (Button) findViewById(R.id.statusButton);
        mQrButton = (Button) findViewById(R.id.qrButton);
        qrResult = (TextView) findViewById(R.id.url);
        qrToken = (TextView) findViewById(R.id.token);
        qrId = (TextView) findViewById(R.id.aparatoID);
        qrHsp = (TextView) findViewById(R.id.hospital);
        idMachine = (TextView) findViewById(R.id.idMachine);

        cbE1 = (CheckBox) findViewById(R.id.error1CB);
        cbE2 = (CheckBox) findViewById(R.id.error2CB);
        cbE3 = (CheckBox) findViewById(R.id.error3CB);

        cbS1 = (CheckBox) findViewById(R.id.solucion1CB);
        cbS2 = (CheckBox) findViewById(R.id.solucion2CB);
        cbS3 = (CheckBox) findViewById(R.id.solucion3CB);

        cbS = (CheckBox) findViewById(R.id.erroresFinal);
        cbW = (CheckBox) findViewById(R.id.perFinal);
        tv = (TextView) findViewById(R.id.tvDetails);

        checkBoxListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbW.isChecked()) {
                    tv.setText("" + cbW.getText().toString());
                    cbS.setChecked(false);
                }

                if (cbS.isChecked()) {
                    tv.setText("" + cbS.getText().toString());
                    cbW.setChecked(false);
                }

                if (!cbS.isChecked() && !cbW.isChecked()) {
                    tv.setText("");
                }
            }
        };

        checkBoxListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbS.isChecked()) {
                    tv.setText("" + cbS.getText().toString());
                    cbW.setChecked(false);
                }

                if (cbW.isChecked()) {
                    tv.setText("" + cbW.getText().toString());
                    cbS.setChecked(false);
                }

                if (!cbS.isChecked() && !cbW.isChecked()) {
                    tv.setText("");
                }
            }
        };

        cbW.setOnClickListener(checkBoxListener1);
        cbS.setOnClickListener(checkBoxListener2);


        //Set listener to button click
        mStatusButton.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {

                                                 //Get the status the user entered and convert to string
                                                 newStatus = mStatusText.getText().toString();
                                                 estado = tv.getText().toString();
                                                 if (tv.getText().toString().equals("Funciona")){
                                                     estado = "1";
                                                 } else if (tv.getText().toString().equals("Errores")){
                                                     estado = "0";
                                                 }
                                                 String id = qrId.getText().toString();
                                                 String token = qrToken.getText().toString();
                                                 SimpleDateFormat gmtDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                 //gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                                 String fecha = gmtDateFormat.format(new Date());
                                                 /*Date fechaAct = new Date();
                                                 String fecha = fechaAct.toString();*/


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

                                                 } else if (estado.isEmpty()) {
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

                                                 } else {

                                                     if (newStatus.isEmpty()) {
                                                         newStatus = "";
                                                     }

                                                     if (cbE1.isChecked()) {
                                                         e1 = "1";
                                                     }

                                                     if (cbE2.isChecked()) {
                                                         e2 = "1";
                                                     }

                                                     if (cbE3.isChecked()) {
                                                         e3 = "1";
                                                     }

                                                     if (cbS1.isChecked()) {
                                                         s1 = "1";
                                                     }

                                                     if (cbS2.isChecked()) {
                                                         s2 = "1";
                                                     }

                                                     if (cbS3.isChecked()) {
                                                         s3 = "1";
                                                     }

                                                     //Store the status entered by user
                                                     //String hsp = currentUser.get("Hospital").toString();
                                                     System.out.println(hspOb);
                                                     // Building Parameters
                                                     new CreateNewProduct().execute();

                                                 }
                                             }
                                         }

        );

        mQrButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             IntentIntegrator integrator = new IntentIntegrator(UpdateStatusActivity.this);
                                             integrator.setCaptureLayout(R.layout.activity_capture_layout);
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

                qrId.setText("Cargando");
                String contents = intent.getStringExtra("SCAN_RESULT"); //this is the result
                qrResult.setText(contents);

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

                //Ej de URL exgerm.com/token/id/08012015/1/2
                //Ej de URL exgerm.com/bdb75f0f769444989a02c8da465c6819
                //Token: /08-01-2015/fecha /1/hospital /2/aparato
                String forId = path.substring(path.lastIndexOf('/') + 1);
                //String forAid = path.substring(pos2 + 1, pos2Fin);
                //String forAid2 = path.substring(pos2Fin + 1, pos2Fin2);
                //ParseUser currentUser = ParseUser.getCurrentUser();
                //String hsp = currentUser.get("Codigo").toString();

                //qrId.setText(forAid);
                qrToken.setText("" + forId);
                token = "" + forId;
                System.out.println(token);
                new GetProductDetails().execute();
                //runQuery();
                //qrResult.setText("" + forAid2);


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    private void runQuery() {
        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Series");
        query.whereEqualTo("UUID", qrToken.getText());
        query.whereEqualTo("Usado", true);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("error", "Aparato sin alta.");
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

                    qrId.setText("");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    String hsp = hspOb;
                    Log.d("id", "Retrieved the object.");
                    String mod = object.getString("Modelo");
                    String status = object.getString("Serie");
                    String status2 = object.getString("Hospital");
                    System.out.println("Aparato: " + mod + status);
                    System.out.println("Hospital: " + status2);
                    aparato = mod + status;

                    if (!aparato.equals("nullnull")) {
                        qrId.setText(aparato);
                    } else {
                        qrId.setText("");
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

                    if (hsp.equals(status2)) {
                        qrHsp.setText(status2);
                    } else {
                        qrHsp.setText("");
                        qrId.setText("");
                        //There was an error
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStatusActivity.this);
                        builder.setMessage("Este aparato no corresponde a este hospital");
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

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });*/
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
            params.add(new BasicNameValuePair("state", estado));
            params.add(new BasicNameValuePair("comment", newStatus));
            params.add(new BasicNameValuePair("users_id", LoginActivity.userId));
            params.add(new BasicNameValuePair("user_name", LoginActivity.userName));
            params.add(new BasicNameValuePair("lowBattery", e2));
            params.add(new BasicNameValuePair("changeBattery", s2));
            params.add(new BasicNameValuePair("lowLiquid", e3));
            params.add(new BasicNameValuePair("changeLiquid", s3));
            params.add(new BasicNameValuePair("physicalDamage", e1));
            params.add(new BasicNameValuePair("physicalRepair", s1));
            params.add(new BasicNameValuePair("hospitals_id", LoginActivity.hospitalSelectedId));
            params.add(new BasicNameValuePair("hospital_name", LoginActivity.hospitalSelected));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_report,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

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

                    String tokenT = token;

                    // Check for success tag
                    try {
                        // Building Parameters
                        List<NameValuePair> paramsGetDetails = new ArrayList<NameValuePair>();
                        paramsGetDetails.add(new BasicNameValuePair("token", tokenT));
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
                    qrId.setText(aparato);
                    idMachine.setText(mid);
                } else {
                    qrId.setText("");
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
                    qrId.setText("");
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
                qrId.setText("");
            }
        }
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



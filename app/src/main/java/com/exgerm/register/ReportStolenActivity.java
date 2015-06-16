package com.exgerm.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReportStolenActivity extends Activity {

    String hspOb;

    private static String url_report_stolen = LoginActivity.main_url + "create_stolen_report.php";

    private static String url_get_models = LoginActivity.main_url + "get_models.php";

    private ArrayList<Category> modelsList;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    protected Button send;
    protected EditText aparato;
    protected Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_stolen);

        //Initialize
        send = (Button) findViewById(R.id.enviar);
        spin = (Spinner) findViewById(R.id.modelo);
        aparato = (EditText) findViewById(R.id.serie);

        modelsList = new ArrayList<Category>();

        new GetCategories().execute();

        /*//Add items
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Modelos");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                int size = 0;
                size = parseObjects.size();
                String[] mod = new String[size+1];
                mod[0] = "Escoge";
                for (int i = 0; i < parseObjects.size(); i++) {
                    mod[i+1] = parseObjects.get(i).getString("Modelo");
                    System.out.println(mod[i+1]);
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ReportStolenActivity.this, android.R.layout.simple_spinner_item, mod);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                spin.setAdapter(spinnerArrayAdapter);
            }
        });*/

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String modeloRobado = spin.getSelectedItem().toString();
                final String serieRobado = aparato.getText().toString();
                    if (serieRobado.isEmpty() || serieRobado.length() < 7 || modeloRobado == null) {
                        //error
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReportStolenActivity.this);
                        builder.setMessage("Debes llenar todos los campos del aparato para reportarlo robado");
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

                    } else if (modeloRobado.equals("Escoge")){
                        //error
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReportStolenActivity.this);
                        builder.setMessage("Debes escoger un modelo");
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
                        new Stolen().execute();
                    }

                }

            }

        );
    }

    class Stolen extends AsyncTask<String, String, String> {

        final String modeloRobado = spin.getSelectedItem().toString();
        final String serieRobado = aparato.getText().toString();

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReportStolenActivity.this);
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
            params.add(new BasicNameValuePair("model", modeloRobado));
            params.add(new BasicNameValuePair("serie", serieRobado));
            params.add(new BasicNameValuePair("user", LoginActivity.userId));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_report_stolen,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

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
            Toast.makeText(ReportStolenActivity.this, "Reporte enviado", Toast.LENGTH_SHORT).show();
            Intent a = new Intent(ReportStolenActivity.this, HomepageActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            finish();
        }

    }

    private class GetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReportStolenActivity.this);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_stolen, menu);
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

package com.exgerm.register;

import android.app.AlertDialog;
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


public class ReportStolenActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();

    protected Boolean networkConnection = false;

    protected Button send;
    protected Button scan;
    protected TextView aparato;

    protected String target;
    protected String targetQr = "";

    protected String token;

    public String url_get_details = LoginActivity.main_url + "get_machine_details";
    public String url_delete_machine = LoginActivity.main_url + "delete_machine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_stolen);
        if(!isOnline()){
            Log.i("Internet status: ", "Not Available");
            networkConnection = false;
        } else {
            Log.i("Internet status: ", "Available");
            networkConnection = true;
        }

        //Initialize
        send = (Button) findViewById(R.id.btnSendDelete);
        scan = (Button) findViewById(R.id.btnScan);
        aparato = (TextView) findViewById(R.id.tvDeleteMachineId);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ReportStolenActivity.this);
                integrator.setCaptureActivity(CaptureLayout.class);
                integrator.setPrompt("Escanea un dosificador");
                integrator.initiateScan();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(targetQr != "" || token != "") {
                    if(networkConnection == true) {
                        new DeleteMachine().execute();
                    } else {
                        // Offline delete machine
                        LoginActivity.offlineDb.execSQL("INSERT INTO DoseliBajas " +
                                "(token, deleted_by) " +
                                "VALUES " +
                                "('"+ token + "', '" + LoginActivity.userId + "');");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReportStolenActivity.this);
                        builder.setTitle("Fuera de línea");
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
                    AlertDialog.Builder build = new AlertDialog.Builder(ReportStolenActivity.this);
                    build.setTitle("Falta aparato");
                    build.setMessage("Escanea un aparato para dar de baja");
                    build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    build.show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if(resultCode == RESULT_OK) {

                aparato.setText("Cargando");
                String contents = intent.getStringExtra("SCAN_RESULT");

                final URI uri = URI.create(contents);
                final String path = uri.getPath();

                int pos = -1;

                for(int i = 1; i <= 1; i++) {
                    pos = path.indexOf('/', pos + 1);
                }

                token = path.substring(path.lastIndexOf('/') + 1);

                if(networkConnection) {
                    new GetProductDetails().execute();
                } else {
                    aparato.setText("Fuera de línea");
                }
            }
        }
    }

    class GetProductDetails extends AsyncTask<Void, Void, Void> {

        int success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramsDelete = new ArrayList<>();
            paramsDelete.add(new BasicNameValuePair("token", token));
            paramsDelete.add(new BasicNameValuePair("current_hospitals_id", LoginActivity.hospitalSelectedId));

            JSONObject jsonDet = jsonParser.makeHttpRequest(url_get_details, "POST", paramsDelete);

            try {
                success = jsonDet.getInt("success");

                if (success == 1) {
                    JSONArray productObj = jsonDet.getJSONArray("product");

                    JSONObject product = productObj.getJSONObject(0);

                    target = product.getString("model") + product.getString("serial_number");
                    targetQr = product.getString("qrs_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            aparato.setText(target);

            if(success == 3) {
                AlertDialog.Builder build = new AlertDialog.Builder(ReportStolenActivity.this);
                build.setTitle("De baja");
                build.setMessage("El aparato ya ha sido dado de baja");
                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                build.show();
            }

        }
    }

    class DeleteMachine extends AsyncTask<Void, Void, Void> {
        int success;

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> paramsToDelete = new ArrayList<>();
            paramsToDelete.add(new BasicNameValuePair("qrs_id", targetQr));
            paramsToDelete.add(new BasicNameValuePair("deleted_by", LoginActivity.userId));

            JSONObject deleting = jsonParser.makeHttpRequest(url_delete_machine, "POST", paramsToDelete);

            try {
                success = deleting.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(success == 1) {
                AlertDialog.Builder build = new AlertDialog.Builder(ReportStolenActivity.this);
                build.setTitle("Hecho");
                build.setMessage("El aparato ha sido dado de baja");
                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                build.show();
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

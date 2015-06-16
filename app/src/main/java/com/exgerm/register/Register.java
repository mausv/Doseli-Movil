package com.exgerm.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class Register extends Activity {

    //Make protected variables to store info
    protected EditText mUsername;
    protected EditText mEmail;
    protected EditText mPassword;
    protected EditText mName;
    protected EditText mLastName;
    protected EditText mCurp;
    protected EditText mRfc;
    protected Button mRegisterButton;

    protected String username;
    protected String password;
    protected String name;
    protected String lastName;
    protected String curp;
    protected String rfc;
    protected String email;
    protected String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Find the buttons when the app starts
        mUsername = (EditText) findViewById(R.id.registerUsername);
        mEmail = (EditText) findViewById(R.id.registerEmail);
        mPassword = (EditText) findViewById(R.id.registerPassword);
        mRegisterButton = (Button) findViewById(R.id.registerButton);
        mName = (EditText) findViewById(R.id.name);
        mLastName = (EditText) findViewById(R.id.lastName);
        mCurp = (EditText) findViewById(R.id.curp);
        mRfc = (EditText) findViewById(R.id.rfc);

        //Listen to the button click
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve user input
                username = mUsername.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                name = mName.getText().toString().trim();
                lastName = mLastName.getText().toString().trim();
                curp = mCurp.getText().toString().trim();
                rfc = mRfc.getText().toString().trim();

                Toast.makeText(Register.this, "Por favor espere...", Toast.LENGTH_SHORT).show();

                /*if(!name.isEmpty() && !lastName.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    //Store user in parse
                    final ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("nombre", name);
                    user.put("apellido", lastName);
                    user.put("curp", curp);
                    user.put("rfc", rfc);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //User signed up successfully
                                Toast.makeText(Register.this, "Listo. Ingresa con tu cuenta.", Toast.LENGTH_SHORT).show();

                                ParseUser currentUser = ParseUser.getCurrentUser();

                                if (currentUser != null) {
                                    ParseUser.logOut();
                                }

                                //Take user home
                                Intent takeUserHome = new Intent(Register.this, LoginActivity.class);
                                startActivity(takeUserHome);
                            } else {
                                //There was an error
                                Toast.makeText(Register.this, "Hubo un error, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setMessage("Debes llenar todos los campos forzosos");
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
                }*/


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

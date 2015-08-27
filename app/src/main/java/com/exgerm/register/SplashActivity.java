package com.exgerm.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Mauricio on 8/27/2015.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

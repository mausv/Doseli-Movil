package com.exgerm.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

public class OfflineMissingHandsetsActivity extends AppCompatActivity {

    private ListView lvOfflineMissingHandsets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_missing_handsets);

        lvOfflineMissingHandsets = (ListView) findViewById(R.id.lvOfflineHandsetsMissing);

        OfflineHandsetsAdapter adapter = new OfflineHandsetsAdapter(this, LoginActivity.offlineMissingHandsets.getList());

        lvOfflineMissingHandsets.setAdapter(adapter);

    }

}

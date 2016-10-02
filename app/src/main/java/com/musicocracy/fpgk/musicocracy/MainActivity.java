package com.musicocracy.fpgk.musicocracy;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
    }

    public void gotoBroadcastSender(View view) {
        Intent intent = new Intent(this, BroadcastSenderActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void gotoBroadcastReceiver(View view) {
        Intent intent = new Intent(this, BroadcastReceiverActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void launchDbActivity(View view) {
        Intent databaseIntent = new Intent(MainActivity.this, DatabaseActivity.class);
        MainActivity.this.startActivity(databaseIntent);
    }

    public void launchBrowseActivity(View view) {
        Intent browseIntent = new Intent(MainActivity.this, Browse.class);
        MainActivity.this.startActivity(browseIntent);
    }
}

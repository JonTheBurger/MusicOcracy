package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.test_activity_main);
    }

    public void gotoBroadcastSender(View view) {
        Intent intent = new Intent(this, TestBroadcastSenderActivity.class);
        TestMainActivity.this.startActivity(intent);
    }

    public void gotoBroadcastReceiver(View view) {
        Intent intent = new Intent(this, TestBroadcastReceiverActivity.class);
        TestMainActivity.this.startActivity(intent);
    }

    public void launchDbActivity(View view) {
        Intent databaseIntent = new Intent(TestMainActivity.this, TestDatabaseActivity.class);
        TestMainActivity.this.startActivity(databaseIntent);
    }

    public void launchBrowseActivity(View view) {
        Intent browseIntent = new Intent(TestMainActivity.this, TestBrowseActivity.class);
        TestMainActivity.this.startActivity(browseIntent);
    }

    public void launchNetworkTestActivity(View view) {
        Intent intent = new Intent(TestMainActivity.this, TestNetworkTestActivity.class);
        TestMainActivity.this.startActivity(intent);
    }
}

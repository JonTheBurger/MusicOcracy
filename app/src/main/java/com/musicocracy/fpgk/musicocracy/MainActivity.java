package com.musicocracy.fpgk.musicocracy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.deleteDatabase("/data/data/com.musicocracy.fpgk.musicocracy/databases/MusicOcracy.sqlite");
    }

    public void launchDbActivity(View view) {
        Intent databaseIntent = new Intent(MainActivity.this, DatabaseActivity.class);
        MainActivity.this.startActivity(databaseIntent);
    }

    public void launchBrowseActivity(View view) {
        
    }
}

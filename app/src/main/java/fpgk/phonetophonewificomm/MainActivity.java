package fpgk.phonetophonewificomm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoBroadcastSender(View view) {
        Intent intent = new Intent(this, BroadcastSenderActivity.class);
        startActivity(intent);
    }

    public void gotoBroadcastReceiver(View view) {
        Intent intent = new Intent(this, BroadcastReceiverActivity.class);
        startActivity(intent);
    }
}

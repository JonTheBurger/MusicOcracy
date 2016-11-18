package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class AuthenticationActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "4becf88681f74bda9e38baac3bcf66d6";
    private static final String REDIRECT_URI = "social-music-app-sd://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_authenticate);
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                String token = response.getAccessToken();

                Bundle authData = new Bundle();
                authData.putString(getString(R.string.result_string), token);

                Log.d("Authentication", "Token: " + token);

                Intent data = new Intent();
                data.putExtras(authData);

                if (getParent() == null) {
                    setResult(RESULT_OK, data);
                } else {
                    getParent().setResult(RESULT_OK, data);
                }
            } else {
                //Auth flow returned returned an error
                Log.e("Authentication", "Auth flow was cancelled or returned an error.");
            }
            finish();
        }
    }
}

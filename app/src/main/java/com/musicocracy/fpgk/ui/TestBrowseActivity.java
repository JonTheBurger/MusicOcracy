package com.musicocracy.fpgk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

// Spotify Imports
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TestBrowseActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "4becf88681f74bda9e38baac3bcf66d6";
    private static final String REDIRECT_URI = "social-music-app-sd://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private static String token;

    SpotifyService spotify;

    private Player mPlayer;

    public void browse(View view) {
        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Log.d("TestBrowseActivity", "Get Album success");
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TestBrowseActivity", "Get Album failure");
                Log.d("Album failure", error.toString());
            }
        });

        TextView searchTrack = (TextView)findViewById(R.id.editText2);

        spotify.searchTracks(searchTrack.getText().toString(), new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tp, Response response) {
                Log.d("TestBrowseActivity", "Get TracksPager success");
                Log.d("TracksPager success", tp.tracks.items.get(1).uri);
                mPlayer.playUri(null, tp.tracks.items.get(0).uri, 0, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TestBrowseActivity", "Get TracksPager failure");
                Log.d("TracksPager failure", error.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_browse);

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
                SpotifyApi api = new SpotifyApi();

                token = response.getAccessToken();

                api.setAccessToken(token);

                spotify = api.getService();

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(TestBrowseActivity.this);
                        mPlayer.addNotificationCallback(TestBrowseActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("TestBrowseActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
        }
            else {
                //Auth flow returned returned an error
                Log.e("TestBrowseActivity", "Auth flow was cancelled or returned an error.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("TestBrowseActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("TestBrowseActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("TestBrowseActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("TestBrowseActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("TestBrowseActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("TestBrowseActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("TestBrowseActivity", "Received connection message: " + message);
    }
}

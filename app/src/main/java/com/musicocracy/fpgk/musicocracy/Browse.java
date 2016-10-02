package com.musicocracy.fpgk.musicocracy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;

// Spotify Imports
import com.musicocracy.fpgk.model.dal.PlayRequest;
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
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Browse extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "4becf88681f74bda9e38baac3bcf66d6";
    private static final String REDIRECT_URI = "social-music-app-sd://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    public void browse(View view) {
        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

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

                api.setAccessToken(response.getAccessToken());

                SpotifyService spotify = api.getService();

                spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new SpotifyCallback<Album>() {
                    @Override
                    public void success(Album album, Response response) {
                        Log.d("Browse", "Get Album success");
                        Log.d("Album success", album.name);
                    }

                    @Override
                    public void failure(SpotifyError error) {
                        Log.d("Browse", "Get Album failure");
                        Log.d("Album failure", error.toString());
                    }
                });

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(Browse.this);
                        mPlayer.addNotificationCallback(Browse.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("Browse", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
            else {
                //Auth flow returned returned an error
                Log.e("Browse", "Auth flow was cancelled or returned an error.");
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
        Log.d("Browse", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("Browse", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("Browse", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("Browse", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("Browse", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("Browse", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("Browse", "Received connection message: " + message);
    }
}

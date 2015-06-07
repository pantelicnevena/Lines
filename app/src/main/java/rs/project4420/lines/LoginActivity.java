package rs.project4420.lines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.example.games.basegameutils.BaseGameUtils;



public class LoginActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnTurnBasedMatchUpdateReceivedListener, OnInvitationReceivedListener{

    private static final String TAG = "LoginAct";
    private static int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
                Log.d(TAG, "google api connect()");
            }
        });
        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sign out clicked");
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    Log.d(TAG, "Games sign out");
                    mGoogleApiClient.disconnect();
                    Log.d(TAG, "api client sign out");
                    findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.second_act).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.second_act).setVisibility(View.VISIBLE);
        } else {
            mGoogleApiClient.connect();
            findViewById(R.id.second_act).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        findViewById(R.id.second_act).setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }
        Log.d(TAG, "connection failed");

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        mResolvingConnectionFailure = true;

        // Attempt to resolve the connection failure using BaseGameUtils.
        // The R.string.signin_other_error value should reference a generic
        // error string in your strings.xml file, such as "There was
        // an issue with sign in, please try again later."
        if (!BaseGameUtils.resolveConnectionFailure(this,
                mGoogleApiClient, connectionResult,
                RC_SIGN_IN, "error message")) {
            mResolvingConnectionFailure = false;
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        }
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        Log.d(TAG, "tbm received: " + turnBasedMatch.getParticipantIds().get(0));

    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {

    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.d(TAG, "recived from: " + invitation.getInviter().getDisplayName());
    }

    @Override
    public void onInvitationRemoved(String s) {

    }



}

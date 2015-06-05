package rs.project4420.lines;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.plus.model.people.Person;
import com.google.example.games.basegameutils.BaseGameUtils;



public class LoginActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    }

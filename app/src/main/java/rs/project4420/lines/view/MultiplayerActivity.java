package rs.project4420.lines.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

import rs.project4420.lines.R;


public class MultiplayerActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnInvitationReceivedListener{
    private static final String TAG = "SecondAct";
    private static final int RC_SIGN_IN = 9001;
    private static final int RQ_ROOM = 9002;
    private static final int RC_LOOK_AT_MATCHES = 9003;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        if (mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected())
                Log.d(TAG, "already connected");
            else
                Log.d(TAG, "notConnected");
        } else {
            Log.d(TAG, "building ApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
        }
        findViewById(R.id.get_room_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,1,1,false);
                startActivityForResult(intent, RQ_ROOM);
            }
        });
        findViewById(R.id.matches_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_LOOK_AT_MATCHES);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_ROOM){
            if(resultCode == Activity.RESULT_OK){
                //Lista pozvanih prijatelja
                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                Intent intent = new Intent(this, ThirdActivity.class);
                TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
                intent.putExtra("match", match);
                intent.putStringArrayListExtra("invitees", invitees);
                startActivity(intent);
            }
        }
        if (requestCode == RC_LOOK_AT_MATCHES) {

            // Returning from the 'Select Match' dialog
            if (resultCode != Activity.RESULT_OK) {
            // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                Log.d(TAG, "match = null");
                //TODO pravljenje poteza za mec iz inboxa
                Intent intent = new Intent(this, ThirdActivity.class);
                intent.putExtra("match", match);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "new connection");
        progress.dismiss();
    }

    @Override
    public void onConnectionSuspended(int i) {
        progress.dismiss();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }
        Log.d(TAG, "connection failed");
        progress.dismiss();

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
    public void onInvitationReceived(Invitation invitation) {
        Toast.makeText(this, "Invite: " + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Invite: " + invitation.getInviter().getDisplayName());
    }

    @Override
    public void onInvitationRemoved(String s) {

    }


    //TODO Never used getNextParticipantId
    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId(TurnBasedMatch mMatch) {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }
}
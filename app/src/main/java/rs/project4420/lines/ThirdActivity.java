package rs.project4420.lines;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;


public class ThirdActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>,
        OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener {

    private static final String TAG = "ThirdAct";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure;

    ArrayList<String> invitees;

    GameData gameData;
    TurnBasedMatch mMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();


        invitees = getIntent().getStringArrayListExtra("invitees");
        Log.d(TAG, invitees.toString());


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_third, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");
        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(invitees)
                .setAutoMatchCriteria(null)
                .build();

        // Create and start the mMatch.
        Games.TurnBasedMultiplayer
                .createMatch(mGoogleApiClient, tbmc)
                .setResultCallback(this);

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
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
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        Status status = result.getStatus();
        if(!status.isSuccess()){
            Log.d(TAG, "status error: (" + status.getStatusCode() + ")" + status.getStatusMessage());
        }

        TurnBasedMatch match = result.getMatch();

        // If this player is not the first player in this mMatch, continue.
        if (match.getData() == null) {
            initGame(match);
        }

        // Let the player take the first turn
        showTurnUI(match);

    }

    private void initGame(TurnBasedMatch match) {
        gameData = new GameData();
        gameData.data = "Nevena";
        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                gameData.persist(), myParticipantId ).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                Log.d(TAG, "treba update match!!!");
            }
        });
    }

    private void showTurnUI(TurnBasedMatch match) {
        byte[] data = match.getData();
        gameData = GameData.unpersist(data);

        // adapter.notifyDataSetChanged();
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.d(TAG, "recived from: " + invitation.getInviter().getDisplayName());
    }

    @Override
    public void onInvitationRemoved(String s) {

    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        Log.d(TAG, "tbm received: " + turnBasedMatch.getParticipantIds().get(0));
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {

    }
}

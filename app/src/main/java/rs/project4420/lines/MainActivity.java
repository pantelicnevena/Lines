package rs.project4420.lines;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity1";
    private static final int TOAST_DELAY = Toast.LENGTH_SHORT;;

    private static int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    // sign in flow, to know you should not attempt
    // to connect in onStart()
    GoogleApiClient mGoogleApiClient;  // initialized in onCreate
    private boolean mAutoStartSignInFlow = true;

    public TurnBasedMatch mMatch;
    public LinesTurn mTurnData;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;
    private AlertDialog mAlertDialog;

    // Local convenience pointers
    public TextView mDataView;
    public TextView mTurnTextView;

    Button button_start;
    Button button_quick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.start_match).setOnClickListener(this);
        findViewById(R.id.dots).setOnClickListener(this);

        // Create the Google Api Client with access to the Play Game services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mGoogleApiClient.connect();
            Log.d(TAG, "Konekcija");
        }
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): Disconnecting from Google APIs");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/


    @Override
    public void onConnected(Bundle bundle) {
        // show sign-out button, hide the sign-in button
        // (your code here: update UI, enable functionality that depends on sign in, etc)
        Log.d(TAG,"Connected");
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        findViewById(R.id.start_match).setVisibility(View.VISIBLE);
        findViewById(R.id.dots).setVisibility(View.VISIBLE);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow

            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        } else if(view.getId() == R.id.start_match){
            onStartMatchClicked();
        }
        else if(view.getId() == R.id.dots){
            otvoriTacke();
        }

        if (view.getId() == R.id.sign_out_button) {
            // user explicitly signed out, so turn off auto sign in
            mExplicitSignOut = true;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }


    }

    public void otvoriTacke(){
        Intent i = new Intent(getApplicationContext(), DotsActivity.class);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

    }

    public void onStartMatchClicked() {
        Intent intent = Games
                .TurnBasedMultiplayer
                .getSelectOpponentsIntent(mGoogleApiClient, 1, 1, true);
                // mGoogleApiCLent, min broj igraca, broj dodatnih igraca, automatch:true/false
        startActivityForResult(intent, RC_SELECT_PLAYERS);
        Log.d(TAG, "Selektujem igraca");
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        }
            /*else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

        }*/ else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            Log.d(TAG, ""+invitees);
            Log.d(TAG, ""+Games.Players.getCurrentPlayerId(mGoogleApiClient));

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria)
                    .build();

            // Start the match
            /*MatchInitiatedCallback mic = new MatchInitiatedCallback();
            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                    mic);*/

            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {

                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            // Check if the status code is not success.
//                            processResult(result);

                            Status status = result.getStatus();
                            if (status.isSuccess()) {
                                Log.d(TAG, "status: " +status.getStatusCode());
                                return;
                                //TODO vraca vrednost 0 za status.getStatusCode i null za status.getStatusMessage
                            } else{
                                Log.d(TAG, "NEMA STATUSA");
                            }

                            Log.d(TAG, "Result: "+result);

                            TurnBasedMatch match = result.getMatch();
                            mMatch = match;

                            // If this player is not the first player in this match, continue.
                            if (match.getData() != null) {
                                Log.d(TAG, "Turn UI");
                                //showTurnUI(match);
                                return;
                            }

                            // Otherwise, this is the first player. Initialize the game state.
                            //        initGame(match);

                            // Let the player take the first turn
                            Log.d(TAG, "Show turn");
                            //        showTurnUI(match);
                        }
                    }
            );

            Intent i = new Intent(getApplicationContext(), GameActivity.class);
            i.putExtra("player1", Games.Players.getCurrentPlayerId(mGoogleApiClient));
            i.putExtra("player2", invitees.toString());
            Log.d(TAG, "mGoogle " + mGoogleApiClient);
            //i.putExtra("matchID", mMatch.getMatchId());
            //String turnData = mDataView.getText().toString();
            startActivity(i);

//            showSpinner();
        }
    }


    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.

    public void startMatch(TurnBasedMatch match) {
        mTurnData = new LinesTurn();
        // Some basic turn data
        mTurnData.data = "First turn";

        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);


        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }


    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

        if (!isSignedIn) {
            /*findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);*/

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }


        //((TextView) findViewById(R.id.name_field)).setText(Games.Players.getCurrentPlayer(mGoogleApiClient).getDisplayName());
        //findViewById(R.id.login_layout).setVisibility(View.GONE);

        if (isDoingTurn) {
            /*findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameplay_layout).setVisibility(View.VISIBLE);*/
        } else {
            /*findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);*/
        }
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
        setViewVisibility();
        mDataView.setText(mTurnData.data);
        mTurnTextView.setText("Turn " + mTurnData.turnCounter);
    }




    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */


    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
//                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
//                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
//                showWarning("Waiting for auto-match...",
//                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = LinesTurn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
        }

        mTurnData = null;

        setViewVisibility();
    }

    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;
        Log.d(TAG, "This match is canceled. All other players will have their game ended.");
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            Log.d(TAG, "Ne postoji status kod"+checkStatusCode(match, result.getStatus().getStatusCode()));
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            Log.d(TAG, "Mec razlicit od null");
            updateMatch(match);
            return;
        }

        Log.d(TAG, "Zapocet mec");
        startMatch(match);
    }


    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            //askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        setViewVisibility();
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will be dealt with later.
                Toast.makeText(this, "Stored action for later. (Please remove this toast before release.)", TOAST_DELAY).show();
                // NOTE: This toast is for informative reasons only; please remove it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                Log.d(TAG, "STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER");
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                Log.d(TAG, "STATUS_MATCH_ERROR_ALREADY_REMATCHED");
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                Log.d(TAG, "STATUS_NETWORK_ERROR_OPERATION_FAILED");
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                Log.d(TAG, "STATUS_CLIENT_RECONNECT_REQUIRED");
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                Log.d(TAG, "STATUS_INTERNAL_ERROR");
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                Log.d(TAG, "STATUS_MATCH_ERROR_INACTIVE_MATCH");
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                Log.d(TAG, "STATUS_MATCH_ERROR_LOCALLY_MODIFIED");
                break;
            default:
                Log.d(TAG, "Unexpected status");
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }


        return false;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}

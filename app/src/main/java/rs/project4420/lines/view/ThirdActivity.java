package rs.project4420.lines.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import rs.project4420.lines.Adapter;
import rs.project4420.lines.R;
import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;
import rs.project4420.lines.classes.Matrix;
import rs.project4420.lines.classes.MatrixItem;
import rs.project4420.lines.classes.Polje;
import rs.project4420.lines.data.GameData;
import rs.project4420.lines.logic.LineSuccess;
import rs.project4420.lines.logic.GameLogic;
import rs.project4420.lines.solver.aStar;


public class ThirdActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener, ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>, AdapterView.OnItemClickListener {

    private static final String TAG = "ThirdAct";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure;
    private DotItem[][] matrix  = new DotItem[7][7];

    TurnBasedMatch mMatch;
    GameData gameData;
    private boolean isDoingTurn;
    ArrayList<String> invitees;
    private AsyncTask<String, Void, Bitmap> dit;

    int lastSelected = -1;
    private MatrixItem[][] matrixCopyItem;
    private Polje next;
    private boolean pronadjenCilj;

    private GridView table;
    private GridView gridView;
    private Adapter adapter;
    ImageView playerIcon1;
    ImageView playerIcon2;
    private View nextView;
    ValueAnimator animator;
    private TextView score;
    private TextView score2;
    private View scoreBar;
    Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        playerIcon1 = (ImageView) findViewById(R.id.player_image);
        playerIcon2 = (ImageView) findViewById(R.id.player_image_second);
        score = (TextView) findViewById(R.id.score);
        score2 = (TextView) findViewById(R.id.score_second);
        scoreBar = (View) findViewById(R.id.score_bar);
        table = (GridView) findViewById(R.id.table);
        gridView = (GridView)findViewById(R.id.table);
        nextView = (View) findViewById(R.id.next_dot_player1);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        gridView.setOnItemClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        mMatch = (TurnBasedMatch) getIntent().getExtras().get("match");
        next = GameLogic.returnNextColor(matrix);
        nextView.setBackgroundResource(R.drawable.next_dot_player1);
        GradientDrawable gd = (GradientDrawable) nextView.getBackground();
        gd.setColor(getResources().getColor(next.getDot().getColor()));

        if (mMatch != null){
            gameData =  new GameData();
            gameData = GameData.unpersist(mMatch.getData());
            Log.d(TAG, "Data: "+gameData.data+ ", turn: "+ gameData.turnCounter);

            matrix = gameData.matrix;
            adapter = new Adapter(this, matrix);
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
            gridView.setHorizontalSpacing(10);
            gridView.setVerticalSpacing(10);
            gridView.invalidate();
        }

        invitees = getIntent().getStringArrayListExtra("invitees");
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

        GameLogic.loadIcons(dit, mMatch, mGoogleApiClient, playerIcon1, playerIcon2);

        if (invitees == null) {
            Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
            Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
            return; }


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


    private void initGame(TurnBasedMatch match) {
        gameData = new GameData();
        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        gameData.data = "Nevena";
        gameData.matrix = matrix;
        gameData.score1 = 0;
        gameData.score2 = 0;

        matrix = GameLogic.setMatrixColors(matrix);
        adapter = new Adapter(this, matrix);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);

        // TODO: prikazi spiner
        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                gameData.persist(), myParticipantId ).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                //Log.d(TAG, "treba update match!!!");
                processResult(updateMatchResult);
            }
        });
    }

    private void showTurnUI(TurnBasedMatch match) {
        byte[] data = match.getData();
        if(data == null){
            return;
        }
        gameData = GameData.unpersist(data);
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
        if (turnBasedMatch == null) { return; }
        mMatch = turnBasedMatch;
        GameLogic.loadIcons(dit, mMatch, mGoogleApiClient, playerIcon1, playerIcon2);
        gridView.setEnabled(true);


        byte[] data = turnBasedMatch.getData();
        if (gameData == GameData.unpersist(data)) {
            Toast.makeText(this, "Prihvacen je invite", Toast.LENGTH_SHORT).show();
            return; }
        gameData = GameData.unpersist(data);
//        Log.d(TAG, "tbm received: " + turnBasedMatch.getParticipantIds().get(0));
        GameLogic.showScoreUpdate(mMatch, mGoogleApiClient, gameData, score, score2);

        matrix = gameData.matrix;
        adapter = new Adapter(this, matrix);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);
        gridView.invalidate();

        isDoingTurn = true;
        showTurnUI(turnBasedMatch);
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
    }

    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result){
        mMatch = result.getMatch();
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);

        // TODO: skloni spiner

        if(!result.getStatus().isSuccess()){
            // TODO: hendluj kodove
        }

        isDoingTurn = (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        Log.d(TAG, "IS DOING TURN: "+isDoingTurn);

        showTurnUI(mMatch);
    }



    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        Status status = result.getStatus();
        if(!status.isSuccess()){
            Log.d(TAG, "status error: (" + status.getStatusCode() + ")" + status.getStatusMessage());
        }
        TurnBasedMatch match = result.getMatch();
        if (match.getData() == null) {
            initGame(match);
        }
        // Let the player take the first turn
        showTurnUI(match);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //stopiraj prethodnu
        if(animator != null && animator.isRunning()){
            animator.end();
            view.clearAnimation();
            animator = null;
        }
        //kliknuto drugo obojeno polje
        if(lastSelected != position){
            if (matrix[position/7][position%7].getColor() == R.color.grey){
                //ako prethodno nije kliknuto na obojeno dugme
                if (lastSelected == -1) {
                    GameLogic.vibrate(vibe);
                } else {
                    pronadjenCilj = false;
                    matrixCopyItem = Matrix.napraviKopijuPolja(matrix);
                    List<MatrixItem> putanja = new ArrayList<>();
                    int xCilj = position/7;
                    int yCilj = position%7;

                    aStar astar = new aStar();
                    putanja = astar.aZvezda(matrixCopyItem, (lastSelected / 7), (lastSelected % 7), (xCilj), (yCilj));

                    if (putanja != null) {
                        final List<MatrixItem> finalPutanja = putanja;
                        int lastColor = matrix[lastSelected / 7][lastSelected % 7].getColor();
                        matrix[lastSelected / 7][lastSelected % 7].setColor(R.color.grey);
                        matrix[position / 7][position % 7].setColor(lastColor);

                        GameLogic.tranzicija(matrix, position, putanja, gridView);
                        lastSelected = -1;
                        matrix = LineSuccess.ponistiNizove(xCilj, yCilj, matrix, score, scoreBar);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                matrix = GameLogic.ubaciNoviDot(matrix, next, adapter, scoreBar, score);
                                next = GameLogic.returnNextColor(matrix);
                                nextView.setBackgroundResource(R.drawable.next_dot_player1);
                                GradientDrawable gd = (GradientDrawable) nextView.getBackground();
                                gd.setColor(getResources().getColor(next.getDot().getColor()));
                                nextView.invalidate();
                                adapter.notifyDataSetChanged();
                            }
                        }, 370);


                        // NEXT PARTICIPANT TURN
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String nextParticipant = GameLogic.getNextParticipantId(mMatch, mGoogleApiClient);
                                GameLogic.ParticipantTurn(mMatch, mGoogleApiClient, gameData, score, score2);

                                Games.TurnBasedMultiplayer
                                        .takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                                                gameData.persist(), nextParticipant).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                                    @Override
                                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                                        processResult(updateMatchResult);
                                    }
                                });
                                gridView.setEnabled(false);
                            }
                        }, 420);

                    }else { //ako ne moze da stigne do cilja
                        lastSelected = -1;
                        GameLogic.vibrate(vibe);
                    };

                    adapter.notifyDataSetChanged();
                }
            } else { //pokretanje animacije kliknutog dugmeta
                animator = ValueAnimator.ofFloat(0, (float) Math.PI);
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);

                final DotView dotView = ((DotView)view);
                final float origRadius = dotView.radius;

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        dotView.radius = origRadius - (float)(Math.abs(Math.sin(value)) * origRadius * .35f);
                        dotView.invalidate();
                    }
                });
                lastSelected = position;
                animator.start();
            }

        } else {  //kliknuto isto obojeno polje
            lastSelected = -1;
        }
    }
}

package rs.project4420.lines.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.List;
import java.util.Random;

import rs.project4420.lines.Connect4;
import rs.project4420.lines.R;
import rs.project4420.lines.logic.GameLogic;


public class LoginActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnTurnBasedMatchUpdateReceivedListener, OnInvitationReceivedListener{

    private static final String TAG = "LoginAct";
    private static int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    ProgressDialog progress;
    View kuglica;
    boolean check;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_connect4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Connect4.class);
                startActivity(i);
            }
        });

        progress = new ProgressDialog(this);
        kuglica = (View) findViewById(R.id.login_dot);
        check = true;
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.single_player_btn).setVisibility(View.GONE);
        findViewById(R.id.multi_player_btn).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.show();
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

        findViewById(R.id.single_player_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DotsActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.multi_player_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MultiplayerActivity.class);
                startActivity(intent);
            }
        });

        dotAnimation(kuglica);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
        findViewById(R.id.single_player_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.multi_player_btn).setVisibility(View.VISIBLE);
        //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        progress.dismiss();
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

    public void dotAnimation(final View dot){
        final List<Integer> boje = GameLogic.returnColors();
        final Random rnd = new Random();
        final GradientDrawable gd = (GradientDrawable) dot.getBackground();
        gd.setColor(getResources().getColor(boje.get(0)));

        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) Math.PI);
        animator.setDuration(1100);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        final float width = dot.getLayoutParams().width;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (i <=5) { i++; } else { i = 0; }
                float value = (float) animation.getAnimatedValue();
                if ((value<((Math.PI/2)-0.5)) || (value>((Math.PI/2)+0.5))) check = true;
                if (check){
                    if ((value>((Math.PI/2)-0.5)) && (value<((Math.PI/2)+0.5))) {
                        GradientDrawable gd = (GradientDrawable) dot.getBackground();
                        int boja = boje.get(i);
                        gd.setColor(getResources().getColor(boja));
                        check = false;
                    }
                }
                ViewGroup.LayoutParams params = dot.getLayoutParams();
                params.width = (int) (width - (float)(Math.abs(Math.sin(value)) * width * .5f));
                params.height = params.width;
                dot.setLayoutParams(params);
                dot.invalidate();
            }
        });
        animator.start();
    }

}

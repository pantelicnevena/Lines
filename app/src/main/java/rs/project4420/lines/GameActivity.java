package rs.project4420.lines;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.nio.charset.Charset;
import java.util.ArrayList;


public class GameActivity extends Activity {

    private static final String TAG = "GameActivity";
    public GoogleApiClient mGoogleApiClient;
    public TurnBasedMatch mMatch;
    public int turnCounter = 0;
    TextView tv1;
    String matchId;
    String mTurnData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tv1 = (TextView) findViewById(R.id.edit_text);

        String player1 = getIntent().getStringExtra("player1");
        String player2 = getIntent().getStringExtra("player2").substring(1, getIntent().getStringExtra("player2").length()-1);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();*/

        //matchId = getIntent().getStringExtra("matchID");
        Log.d(TAG, "\nmGoogleClient: "+mGoogleApiClient+"\nMatch:" + mMatch);

        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.button1:
                otvoriTacke();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
    }

    public void otvoriTacke(){
        Intent i = new Intent(getApplicationContext(), DotsActivity.class);
        startActivity(i);
    }



    /*public void posaljiClicked(){
        mTurnData = tv1.getText().toString();
        String nextParticipantId = getNextParticipantId();
        turnCounter++;
        String data = (String) tv1.getText();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, matchId,
                mTurnData.getBytes(Charset.forName("UTF-16")), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        //processResult(result);
                    }
                });

    }

    public String getNextParticipantId() {

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
    }*/

}

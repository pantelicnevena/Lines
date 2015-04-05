package rs.project4420.lines;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by nevena on 28.3.15..
 */
public class MatchInitiatedCallback implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {
    private static final String TAG = "MatchInitiatedCallback";
    private TurnBasedMatch mMatch;

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        // Check if the status code is not success.
        Status status = result.getStatus();
        if (status.isSuccess()) {
            Log.d(TAG, ""+status.getStatusMessage());
            //return;
//            TODO vraca vrednost 0 za status.getStatusCode i null za status.getStatusMessage
        }
        Log.d(TAG, "Result: "+result);

        TurnBasedMatch match = result.getMatch();
        mMatch = match;
        setmMatch(mMatch);

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

    public TurnBasedMatch getmMatch() {
        return mMatch;
    }

    public void setmMatch(TurnBasedMatch mMatch) {
        this.mMatch = mMatch;
    }
}

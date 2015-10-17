package rs.project4420.lines.connect4;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import rs.project4420.lines.R;

public class Connect4Activity extends Activity
        implements AdapterView.OnItemClickListener,
                    GameListener{


    private static final String TAG = Connect4Activity.class.getSimpleName();

    private Game game;

    private GridView tableView;
    private Connect4Adapter adapter;
    private Vibrator vibrator;
    private TextView statusText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect4);

        int player = getIntent().getIntExtra(Constants.PLAYER_EXTRA, 1);
        Log.d(TAG, "player: " + player);

        game = new Game();
        game.addGameListener(this);

        adapter = new Connect4Adapter(this, game);

        tableView = (GridView)findViewById(R.id.table);
        tableView.setAdapter(adapter);
        tableView.setHorizontalSpacing(10);
        tableView.setVerticalSpacing(10);
        tableView.setOnItemClickListener(this);

        statusText = (TextView)findViewById(R.id.status_text);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(player == Constants.PLAYER_2) {
            tableView.setEnabled(false);
            game.nextComputer();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        tableView.setEnabled(false);
        if(game.next(i % 7)) {
            if(game.checkFinish()){
                String msg = "Player " + game.gameStatus + " won.";
                statusText.setText(msg);
            } else {
                game.nextComputer();
            }
        } else {
            vibrator.vibrate(50);
            tableView.setEnabled(true);
        }
    }

    @Override
    public void gameDataChanged(CoinItem[][] data) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void computerThinkig() {
        statusText.setText("Computer Thinking");
    }

    @Override
    public void computerPlayed() {
        statusText.setText("");
        if(game.checkFinish()){
            String msg = "Player " + game.gameStatus + " won.";
            statusText.setText(msg);
        } else {
            tableView.setEnabled(true);
        }
    }
}

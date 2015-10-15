package rs.project4420.lines;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class Connect4 extends ActionBarActivity implements AdapterView.OnItemClickListener {


    private static final String TAG = Connect4.class.getSimpleName();
    private static final int STATUS_DRAW = 0;
    private static final int STATUS_P1_WIN = 1;
    private static final int STATUS_P2_WIN = 2;


    private GridView tableView;
    private Connect4Adapter adapter;
    private int currentPlayer;
    private Vibrator vibrator;
    private int status = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect4);

        //Hide Toolbar
        getSupportActionBar().hide();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        adapter = new Connect4Adapter(this);

        tableView = (GridView)findViewById(R.id.table);
        tableView.setAdapter(adapter);
        tableView.setHorizontalSpacing(10);
        tableView.setVerticalSpacing(10);
        tableView.setOnItemClickListener(this);

        currentPlayer = CoinItem.COIN_OWNER_PL1;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapter.addToColumn(i%7, currentPlayer)) {
            if(checkFinish()){
                String msg = "Player " + status + " won.";
                ((TextView)findViewById(R.id.status_text)).setText(msg);
            }
            currentPlayer = (currentPlayer % 2)+1;
        } else {
            vibrator.vibrate(50);
        }
    }

    private boolean checkFinish() {

        CoinItem[][] data = adapter.getItems();

        int m = 0;
        for (int i = 0; i < 7; i++) {
            if(data[0][i].getCoinOwner() != CoinItem.COIN_OWNER_GRID){
                m++;
            }
        }
        if(m==7){
            this.status = STATUS_DRAW;
            return true;
        }


        // check path -
        for (int i = 0; i < 6; i++) {
            int max = 1;
            for (int j = 1; j < 7; j++) {
                if(data[i][j].getCoinOwner() != CoinItem.COIN_OWNER_GRID &&
                        data[i][j-1].getCoinOwner() == data[i][j].getCoinOwner()){
                    max++;
                    if(max >= 4){
                        if (currentPlayer == CoinItem.COIN_OWNER_PL1) {
                            this.status = STATUS_P1_WIN;

                        } else {
                            this.status = STATUS_P2_WIN;
                        }
                        return true;
                    }
                } else {
                    max = 1;
                }
            }
        }

        // check path -
        for (int j = 0; j < 7; j++) {
            int max = 1;
            for (int i = 1; i < 6; i++) {
                if(data[i][j].getCoinOwner() != CoinItem.COIN_OWNER_GRID &&
                        data[i-1][j].getCoinOwner() == data[i][j].getCoinOwner()){
                    max++;
                    if(max >= 4){
                        if (currentPlayer == CoinItem.COIN_OWNER_PL1) {
                            this.status = STATUS_P1_WIN;

                        } else {
                            this.status = STATUS_P2_WIN;
                        }
                        return true;
                    }
                } else {
                    max = 1;
                }
            }
        }

        // check path /
        for(int n=0;n<(6+7);n++){
            int max = 1;

            int x = 0;
            int y = n;

            while (x <= 7 && y>=0){
                if(x>=6 ||x<0 ||y<0 ||y >= 7) {
                    x++;
                    y--;

                    continue;
                }
                if(data[x][y].getCoinOwner() != CoinItem.COIN_OWNER_GRID && x>0 && y< 7-1 && data[x-1][y+1].getCoinOwner()==data[x][y].getCoinOwner()){
                    max++;
                    if(max >=4){
                        if(data[x][y].getCoinOwner() == CoinItem.COIN_OWNER_PL1) {
                            this.status = STATUS_P1_WIN;
                        } else {
                            this.status = STATUS_P2_WIN;
                        }
                        return true;
                    }
                }else{
                    max = 1;
                }

                x++;
                y--;
            }
        }

        //check path \
        for(int n=0;n<(6+7);n++){
            int max = 1;

            int x = 6 - 1;
            int y = n;

            while (x <= 7 && y>=0){
                if(x>=6 ||x<0 ||y<0 ||y >= 7) {
                    x--;
                    y--;

                    continue;
                }
                Log.d(TAG, "("+x+", "+y+")");
                if(data[x][y].getCoinOwner() != CoinItem.COIN_OWNER_GRID && x>0 && y>0 && data[x-1][y-1].getCoinOwner()==data[x][y].getCoinOwner()){
                    max++;
                    if(max >=4) {
                        if(data[x][y].getCoinOwner() == CoinItem.COIN_OWNER_PL1) {
                            this.status = STATUS_P1_WIN;
                        } else {
                            this.status = STATUS_P2_WIN;
                        }
                        return true;
                    }
                }else{
                    max = 1;
                }

                x--;
                y--;
            }
        }


        return false;

    }
}

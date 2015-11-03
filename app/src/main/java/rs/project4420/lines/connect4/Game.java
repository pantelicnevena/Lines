package rs.project4420.lines.connect4;

import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class Game {
    private static final String TAG = Game.class.getSimpleName();


    private final Set<GameListener> gameListeners = new HashSet<>();



    private CoinItem[][] data;
    private Connect4AI ai;

    public int turn;

    public int gameStatus = -1;

    public Game() {
        this.turn = 1;
        data = new CoinItem[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                data[i][j] = new CoinItem();
            }
        }
        ai = new Connect4AI();
    }

    public CoinItem[][] getData() {
        return data;
    }

    public void setData(CoinItem[][] data) {
        this.data = data;
        broadcastDataChanged();
    }

    public void addGameListener(GameListener listener){
        gameListeners.add(listener);
    }

    public void removeGameListener(GameListener listener){
        gameListeners.remove(listener);
    }

    public boolean next(int col){
        // check if valid move
        for (int i = 5; i >= 0; i--) {
            if(data[i][col].getCoinOwner() == Constants.COIN_OWNER_GRID){

                data[i][col].setCoinOwner(turn);
                broadcastDataChanged();
                this.turn = -1 * this.turn;

                logData();
                return true;
            }
        }
        return false;
    }

    public void nextComputer(){
        broadcastComputerThinking();

        final Game self = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                int ret = ai.next(data, self.turn * (-1));
                for (int i = 5; i >= 0; i--) {
                    if (data[i][ret].getCoinOwner() == Constants.COIN_OWNER_GRID) {
                        data[i][ret].setCoinOwner(turn);
                        broadcastDataChanged();
                        self.turn = -1 * self.turn;
                        break;
                    }
                }

                broadcastComputerPlayed();
            }
        }, 2000);

    }

    private void broadcastDataChanged(){
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.gameDataChanged(data);
        }
    }

    private void broadcastComputerThinking() {
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.computerThinkig();
        }
    }

    private void broadcastComputerPlayed() {
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.computerPlayed();
        }
    }



    public boolean checkFinish() {

        // check if full
        int m = 0;
        for (int i = 0; i < 7; i++) {
            if(data[0][i].getCoinOwner() != Constants.COIN_OWNER_GRID){
                m++;
            }
        }
        if(m==7){
            this.gameStatus = Constants.STATUS_DRAW;
            return true;
        }


        // check path -
        for (int i = 0; i < 6; i++) {
            int max = 1;
            for (int j = 1; j < 7; j++) {
                if(data[i][j].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        data[i][j-1].getCoinOwner() == data[i][j].getCoinOwner()){
                    max++;
                    if(max >= 4){
                        processStatus(data[i][j].getCoinOwner());
                        return true;
                    }
                } else {
                    max = 1;
                }
            }
        }

        // check path |
        for (int j = 0; j < 7; j++) {
            int max = 1;
            for (int i = 1; i < 6; i++) {
                if(data[i][j].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        data[i-1][j].getCoinOwner() == data[i][j].getCoinOwner()){
                    max++;
                    if(max >= 4){
                        processStatus(data[i][j].getCoinOwner());
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
                if (data[x][y].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        x>0 && y< 7-1 &&
                        data[x-1][y+1].getCoinOwner()==data[x][y].getCoinOwner() ){

                    max++;
                    if(max >=4){
                        processStatus(data[x][y].getCoinOwner());
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
                if (data[x][y].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        x>0 && y>0 &&
                        data[x-1][y-1].getCoinOwner()==data[x][y].getCoinOwner() ){

                    max++;
                    if(max >=4) {
                        processStatus(data[x][y].getCoinOwner());
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

    private void processStatus(int coinOwner) {
        this.gameStatus = (coinOwner == Constants.PLAYER_1) ? Constants.STATUS_P1_WIN : Constants.STATUS_P2_WIN;
    }

    private void logData(){
        for (int x = 0; x < 6; x++) {
            String s = "";
            for (int y = 0; y < 7; y++) {
                s+= " " + data[x][y].getCoinOwner();
            }
            Log.d(TAG, s);
        }
        Log.d(TAG, "-------");
    }

}

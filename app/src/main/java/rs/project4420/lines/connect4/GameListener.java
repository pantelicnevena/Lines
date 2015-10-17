package rs.project4420.lines.connect4;

/**
 * Created by enco on 16.10.15..
 */
public interface GameListener {
    void gameDataChanged(CoinItem[][] data);
    void computerThinkig();
    void computerPlayed();
}

package rs.project4420.lines.connect4;

public interface GameListener {
    void gameDataChanged(CoinItem[][] data);
    void computerThinkig();
    void computerPlayed();
}

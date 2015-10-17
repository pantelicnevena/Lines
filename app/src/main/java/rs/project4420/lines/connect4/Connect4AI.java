package rs.project4420.lines.connect4;

import java.util.Random;

/**
 * Created by enco on 17.10.15..
 */
public class Connect4AI {
    public int next(CoinItem[][] data) {
        Random rand = new Random();
        int r;
        do {
            r = rand.nextInt(7);
        }
        while (data[0][r].getCoinOwner() != Constants.COIN_OWNER_GRID);

        return r;

    }
}

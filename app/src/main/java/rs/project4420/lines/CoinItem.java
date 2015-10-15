package rs.project4420.lines;

import android.graphics.Color;

/**
 * Created by enco on 15.10.15..
 */
public class CoinItem {

    public static final int COIN_COLOR_GRID = R.color.grey;
    public static final int COIN_COLOR_PL1 = R.color.red;
    public static final int COIN_COLOR_PL2 = R.color.yellow;

    public static final int COIN_OWNER_GRID = 0;
    public static final int COIN_OWNER_PL1 = 1;
    public static final int COIN_OWNER_PL2 = 2;

    private int color;
    private int coinOwner;

    public CoinItem() {
        color = COIN_COLOR_GRID;
        coinOwner = COIN_OWNER_GRID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCoinOwner() {
        return coinOwner;
    }

    public void setCoinOwner(int coinOwner) {
        this.coinOwner = coinOwner;
        this.color = (coinOwner == COIN_OWNER_PL1) ? COIN_COLOR_PL1 : COIN_COLOR_PL2;
    }
}

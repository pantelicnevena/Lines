package rs.project4420.lines.connect4;

public class CoinItem {


    private int color;
    private int coinOwner;

    public CoinItem() {
        color = Constants.COIN_COLOR_GRID;
        coinOwner = Constants.COIN_OWNER_GRID;
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
        this.color = (coinOwner == Constants.PLAYER_1) ? Constants.COIN_COLOR_PL1 : Constants.COIN_COLOR_PL2;
    }
}

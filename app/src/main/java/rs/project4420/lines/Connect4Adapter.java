package rs.project4420.lines;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;

/**
 * Created by enco on 15.10.15..
 */
public class Connect4Adapter extends BaseAdapter {

    private static final String TAG = "Adapter";
    private CoinItem[][] mItems;
    private Context mContext;

    public Connect4Adapter(Context context) {
        mContext = context;
        mItems = new CoinItem[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                mItems[i][j] = new CoinItem();
            }
        }
    }

    @Override
    public int getCount() {
        return 7*6;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position/7][position%7];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dot_item, parent, false);
        }
        ((DotView) convertView).setColor(mItems[position / 7][position % 7].getColor());
        return convertView;
    }

    public boolean addToColumn(int col, int coinOwner){
        for (int i = 5; i >= 0; i--) {
            if(mItems[i][col].getCoinOwner() == CoinItem.COIN_OWNER_GRID){
                mItems[i][col].setCoinOwner(coinOwner);
                this.notifyDataSetChanged();

                for (int x = 0; x < 6; x++) {
                    String s = "";
                    for (int y = 0; y < 7; y++) {
                        s+= " " + mItems[x][y].getCoinOwner();
                    }
                    Log.d(TAG, s);
                }
                Log.d(TAG, "-------");
                return true;
            }
        }
        return false;
    }

    public CoinItem[][] getItems() {
        return mItems;
    }
}


package rs.project4420.lines;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

/**
 * Created by nevena on 19.4.15..
 */
public class Adapter extends BaseAdapter {

    private static final String TAG = "Adapter";
    private DotItem[][] mItems;
    private Context mContext;

    public Adapter(Context context, DotItem[][] items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return 36;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position/6][position%6];
    }

    @Override
    public long getItemId(int position) {
        return 0;//mItems[position/6][position%6].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dot_item, parent, false);
        }
        ((DotView)convertView).setColor(mItems[position/6][position%6].getColor());
        return convertView;
    }


}

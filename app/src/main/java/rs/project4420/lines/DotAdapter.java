package rs.project4420.lines;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by nevena on 2.4.15..
 */
public final class DotAdapter extends BaseAdapter{

    private static final String TAG = "DotAdapter";
    private final List<Button> mItem = new ArrayList<>();
    private final LayoutInflater mInflater;

    public DotAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        List<Integer> colors = new ArrayList<>();
        colors.add(R.color.blue);
        colors.add(R.color.red);
        colors.add(R.color.purple);
        colors.add(R.color.yellow);
        colors.add(R.color.orange);
        colors.add(R.color.light_blue);
        colors.add(R.color.green);

        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Item itm = new Item(context, colors.get(rnd.nextInt(7)));
                mItem.add(itm);
            }
        }
    }

    @Override
    public int getCount() {
        return mItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItem.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.button, v.findViewById(R.id.button));
        }

        picture = (ImageView) v.getTag(R.id.button);
        Item button = (Item) getItem(i);
        picture.setImageResource(button.drawableId);

        return v;
    }


    private static class Item extends Button{
        public final int drawableId;


        public Item(Context context, int drawableId) {
            super(context);
            this.drawableId = drawableId;
        }
    }

}

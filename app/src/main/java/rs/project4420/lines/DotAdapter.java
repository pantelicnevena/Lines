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


        /*        Button btn = new Button(context);
                btn.setText("aaaaa");
                btn.setId(R.id.button);
                mItem.add(btn);*/
/*
        mItem.add(new Item("Red", R.color.red));
        mItem.add(new Item("Blue", R.color.blue));
        mItem.add(new Item("Purple", R.color.purple));
        mItem.add(new Item("Light blue", R.color.light_blue));
        mItem.add(new Item("Red", R.color.red));
        mItem.add(new Item("Blue", R.color.blue));
        mItem.add(new Item("Purple", R.color.purple));
        mItem.add(new Item("Light blue", R.color.light_blue));*/

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
        Log.d(TAG, ""+mItem.get(i).getId());
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
        /*picture = (ImageView) v.getTag(R.id.button);
        Item item = (Item) getItem(i);
        picture.setImageResource(item.drawableId);*/

        return v;
    }


    private static class Item extends Button{
        public final int drawableId;


        public Item(Context context, int drawableId) {
            super(context);
            this.drawableId = drawableId;
            setText("jsakdla");
        }
    }
    /*
    private static class Item{
        public final String name;
        public final int drawableId;

        Item(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }*/
}

package rs.project4420.lines;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

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
    Context con;
    Button btn;
    Button selectedItem = null;
    ValueAnimator valAnim;
    ValueAnimator va;

    public DotAdapter(Context context) {
        con = context;
        mInflater = LayoutInflater.from(context);

        //Lista boja za random generisanje pocetnog stanja
        List<Integer> colors = new ArrayList<>();
        colors.add(R.color.blue);
        colors.add(R.color.red);
        colors.add(R.color.purple);
        colors.add(R.color.yellow);
        colors.add(R.color.orange);
        colors.add(R.color.light_blue);
        colors.add(R.color.green);
        colors.add(R.color.grey);

        Random rnd = new Random();
        List<Integer> pozicija = new ArrayList();

        //Niz polja
        for (int i = 0; i < 36; i++) {
            Item item = new Item(context, colors.get(7), colors.get(7));
            mItem.add(item);
        }
        //Random odredjivanje pozicije obojenih polja u listi
        //TODO obezbediti da se random brojevi ne ponavljaju
        for (int i = 0; i <12 ; i++) {
            pozicija.add(rnd.nextInt(36));
        }
        //Dodavanje obojenih polja na random pozicije u listu obojenih polja
        for (int i = 0; i < pozicija.size(); i++) {
            int res = colors.get(rnd.nextInt(7));
            Item itm = new Item(context, res, res);
            mItem.set(pozicija.get(i), itm);
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
    public View getView(int i, final View view, ViewGroup viewGroup) {
        View v = view;
        final ImageView button;
        Item itm;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.button, v.findViewById(R.id.button));
            v.setTag(R.id.item, v.findViewById(R.id.item));
        }

        btn = (Button) v.getTag(R.id.item);
        itm = (Item) getItem(i);

        ViewGroup.LayoutParams lp = btn.getLayoutParams();
        lp.width = 109;
        lp.height = 109;
        btn.setLayoutParams(lp);
        final int height = btn.getLayoutParams().height;

        btn.getBackground().setColorFilter(con.getResources().getColor(itm.drawableId), PorterDuff.Mode.MULTIPLY);
        btn.setDrawingCacheBackgroundColor(itm.drawableId);

        valAnim = ValueAnimator.ofFloat(0, (float)Math.PI);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Button b = (Button) v;

                //Ako je selektovano obojeno polje
                if (b.getDrawingCacheBackgroundColor() != 2131230754){
                    if(selectedItem == null) {
                        selectedItem = b;
                        int color = b.getDrawingCacheBackgroundColor();
                        Log.d(TAG, ""+color);

                        va = ValueAnimator.ofFloat(0, (float) Math.PI);
                        va.setDuration(2000);
                        va.setRepeatCount(ValueAnimator.INFINITE);
                        va.setInterpolator(new LinearInterpolator());

                        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();

                                int pad = (int) Math.abs(0.42 * height * Math.sin((double) value * 2)) / 2;
                                ViewGroup.LayoutParams params = v.getLayoutParams();
                                params.width = 109 - pad * 2;
                                params.height = 109 - pad * 2;
                                FrameLayout fl = (FrameLayout) v.getParent();
                                fl.setPadding(pad, pad, pad, pad);
                                v.setLayoutParams(params);
                            }
                        });
                        va.start();
                        valAnim = va;
                    } else{
                        if (selectedItem == (Button) v){
                            va.end();
                            selectedItem.clearAnimation();
                            v.clearAnimation();
                            selectedItem = null;
                        } else{
                            va.end();
                            v.clearAnimation();
                            selectedItem = (Button) v;
                            va = ValueAnimator.ofFloat(0, (float) Math.PI);
                            va.setDuration(2000);
                            va.setRepeatCount(ValueAnimator.INFINITE);
                            va.setInterpolator(new LinearInterpolator());
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float value = (float) animation.getAnimatedValue();
                                    int pad = (int) Math.abs(0.42 * height * Math.sin((double) value * 2)) / 2;
                                    ViewGroup.LayoutParams params = v.getLayoutParams();
                                    params.width = 109 - pad * 2;
                                    params.height = 109 - pad * 2;
                                    FrameLayout fl = (FrameLayout) v.getParent();
                                    fl.setPadding(pad, pad, pad, pad);
                                    v.setLayoutParams(params);
                                }
                            });
                            va.start();
                            valAnim = va;
                        }
                    }
                }
                //Ako je selektovano sivo polje
                else{
                    //Ako je prethodno selektovano obojeno polje za pomeranje
                    if (selectedItem != null) {
                        final int color1 = selectedItem.getDrawingCacheBackgroundColor();
                        final int color2 = v.getDrawingCacheBackgroundColor();
                        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), color1, color2);
                        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                v.getBackground().setColorFilter(con.getResources().getColor(color1), PorterDuff.Mode.MULTIPLY);
                                v.setDrawingCacheBackgroundColor(color1);
                                selectedItem.getBackground().setColorFilter(con.getResources().getColor(color2), PorterDuff.Mode.MULTIPLY);
                                selectedItem.setDrawingCacheBackgroundColor(color2);
                                va.end();
                                v.clearAnimation();
                            }
                        });
                        colorAnimator.start();
                    }
                    //Ako nije prethodno selektovano obojeno polje za pomeranje
                    else{
                        Toast.makeText(con, "Izaberite polje za pomeranje", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        //button = (ImageView) v.getTag(R.id.button);
        //button.setImageResource(itm.drawableId);

        return v;
    }




    public static class Item extends Button{
        public int drawableId;
        public int color;

        public Item(Context context, int drawableId, int color) {
            super(context);
            this.drawableId = drawableId;
            this.setBackgroundColor(color);
        }

        @Override
        public void setHeight(int pixels) {
            super.setHeight(pixels);
        }

        @Override
        public void setBackgroundColor(int color) {
            super.setBackgroundColor(color);
        }

    }

}

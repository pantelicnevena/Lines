package rs.project4420.lines;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class DotsActivity extends ActionBarActivity {

    private static final String TAG = "DotsActivity";

    Random rnd;
    GridView table;
    View selectedView;
    //HashMap<View, Boolean> items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        selectedView = null;
        super.onCreate(savedInstanceState);
        rnd = new Random();

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);
        List<rs.project4420.lines.DotView> list = new ArrayList<>();

        final GridView gridView = (GridView)findViewById(R.id.table);
        gridView.setAdapter(new DotAdapter(this));

        /*items = new HashMap<>();
        for (int i = 0; i < 35; i++) {
            items.put((View) gridView.getItemAtPosition(i), false);
            Log.d(TAG, items.get(i)+"");
        }*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final ValueAnimator va =  ValueAnimator.ofFloat(0, (float)Math.PI);
                if (selectedView == null) {
                    selectedView = view;

                    va.setDuration(2000);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            //Log.d(TAG, "" + value);
                            //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                            //layoutParams.width = (int) Math.abs(113.22 * Math.cos((double) value * 5));
                            //view.setLayoutParams(layoutParams);
                            int height = view.getHeight();
                            int pad = (int) Math.abs(0.42 * height * Math.sin((double) value * 2));
                            view.setPadding(pad, pad, pad, pad);
                        }
                    });
                    va.setInterpolator(new LinearInterpolator());
                    va.start();
                    Log.d(TAG, "Startovana");
                }
                else {
                    if(selectedView == view){
                        view.clearAnimation();
                        selectedView = null;
                        Log.d(TAG, "ISTI KLIKNUT");
                    }
                    else {
                        view.clearAnimation();
                        selectedView = view;
                        //va.start();
                        Log.d(TAG, "DRUGI KLIKNUT");
                    }
                }
            }
        });

    }

    public class DotView extends ImageView {

        private float mRadius = 20;
        private PointF mPosition;
        private Paint mPaint;

        private int color;

        public DotView(Context context) {
            super(context);
            init(context, null, 0);
        }

        public DotView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs, 0);
        }

        public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs, defStyleAttr);
        }

        private void init(Context context, AttributeSet attrs, int defStyleAttr){
            mPaint = new Paint();
            mPosition = new PointF(10,10);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
        /*
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            mPaint.setColor(color);
            canvas.drawCircle(mPosition.x, mPosition.y, mRadius, mPaint);
            canvas.restore();
        }
*/
        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

    }


    public class MyView extends View {
        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            double m = x/8;
            Log.d(TAG, "onDraw");
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            // Use Color.parseColor to define HTML colors
            List<Integer> colors = new ArrayList<>();
            colors.add(R.color.blue);
            colors.add(R.color.red);
            colors.add(R.color.purple);
            colors.add(R.color.yellow);
            colors.add(R.color.orange);
            colors.add(R.color.light_blue);
            colors.add(R.color.green);
            for (int i=0; i<6; i++) {
                for (int j=0; j<6; j++) {
                    //paint.setColor(Color.YELLOW);
                    paint.setColor(getResources().getColor(colors.get(rnd.nextInt(7))));
                    canvas.drawCircle((float)(m+m/2+i*m), (float)(((y-6*m))/2+j*m) , 20, paint);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package rs.project4420.lines;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DotsActivity extends ActionBarActivity {

    private static final String TAG = "DotsActivity";

    Random rnd;
    GridView table;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rnd = new Random();

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);
        List<rs.project4420.lines.DotView> list = new ArrayList<>();



        GridView gridView = (GridView)findViewById(R.id.table);
        gridView.setAdapter(new DotAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, ""+position);
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

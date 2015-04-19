package rs.project4420.lines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by nevena on 2.4.15..
 */
public class DotView extends View {



    private static final String TAG = "DotView";
    Paint paint;
    float radius;
    private float cx;
    private float cy;


    public DotView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(cx, cy, radius *.35f, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        radius = getMeasuredWidth();
        cx = getMeasuredHeight()/2.0f;
        cy = getMeasuredHeight()/2.0f;
    }

    public void setColor(int color) {
        paint.setColor(getResources().getColor(color));
    }
}

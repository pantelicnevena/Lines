package rs.project4420.lines;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by nevena on 2.4.15..
 */
public class DotView extends ImageView {

    private float mRadius = 20;
    private PointF mPosition;
    private Paint mPaint;

    private int color;

    public DotView(Context context) {
        super(context);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

package rs.project4420.lines;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by nevena on 19.4.15..
 */
public class DotButton implements View.OnClickListener {

    private static final String TAG = "DotButton";
    private Context mContext;
    private Button mButton;
    private FrameLayout frameLayout;
    private String mText;
    private int mColor;

    public DotButton(Context context) {
        mContext = context;
        mButton = new Button(mContext);
        //mButton.setBackgroundColor(Color.RED);
        mButton.setOnClickListener(this);
        frameLayout = new FrameLayout(mContext);
        frameLayout.addView(mButton);
    }

    public DotButton(Context context, Button button) {
        mButton = button;
        mContext = context;
    }

    int i = 0;
    @Override
    public void onClick(View v) {
        Log.d(TAG, "button: " + mText);
        frameLayout.setPadding(10*i,10*i,10*i,10*i);
        i++;
    }

    public void setmText(String text) {
        mText = text;
        mButton.setText(text);
    }

    public String getmText() {
        return mText;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int color) {
        this.mColor = color;
        mButton.setBackgroundColor(color);
    }

    public Button getmButton() {
        return mButton;
    }

    public void setmButton(Button mButton) {
        this.mButton = mButton;
    }

    public ViewGroup getViewGroup(){
        return frameLayout;
    }

}

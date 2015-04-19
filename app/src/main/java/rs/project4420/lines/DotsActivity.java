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
import android.widget.FrameLayout;
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

    List<DotButton> mButtons;
    List<DotView> dots;

    DotItem[][] matrix = new DotItem[6][6];


    ValueAnimator animator;
    int lastSelected = -1;

    public DotsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                matrix[i][j] = new DotItem(R.color.grey);
            }
        }


        GridView gridView = (GridView)findViewById(R.id.table);
        Adapter adapter = new Adapter(this, matrix);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);
        Log.d(TAG, "activity: " + gridView.getItemAtPosition(0) );

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position:" + position);

                //stopiraj prethodnu
                if(animator != null && animator.isRunning()){
                    animator.end();
                    view.clearAnimation();
                    animator = null;
                }

                if(lastSelected != position){
                    lastSelected = position;

                    animator = ValueAnimator.ofFloat(0, (float) Math.PI);
                    animator.setDuration(1000);
                    animator.setRepeatCount(ValueAnimator.INFINITE);

                    final DotView dotView = ((DotView)view);
                    final float origRadius = dotView.radius;

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            dotView.radius = origRadius - (float)(Math.abs(Math.sin(value)) * origRadius * .35f);
                            dotView.invalidate();
                        }
                    });
                    animator.start();
                } else {
                    lastSelected = -1;
                }
            }
        });

/*
        Matrica matrica = new Matrica(gridView);
        matrica.kreirajMatricu();
        matrica.stampajMatricu();
*/

    }

}

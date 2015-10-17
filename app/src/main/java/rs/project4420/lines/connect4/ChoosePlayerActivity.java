package rs.project4420.lines.connect4;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.Random;

import rs.project4420.lines.R;
import rs.project4420.lines.logic.GameLogic;

public class ChoosePlayerActivity extends Activity {

    int player = 0;
    Button playBtn;
    ValueAnimator animator;
    View currentDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);

        playBtn = ((Button)findViewById(R.id.play_btn));
        playBtn.setEnabled(false);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Connect4Activity.class);
                i.putExtra(Constants.PLAYER_EXTRA, player);
                startActivity(i);
            }
        });



        final View redDot = findViewById(R.id.dot_red);
        final View yellowDot = findViewById(R.id.dot_yellow);

        redDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                player = Constants.PLAYER_1;
                dotAnimation(redDot);
            }
        });

        yellowDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                player = Constants.PLAYER_2;
                dotAnimation(yellowDot);
            }
        });


    }

    public void dotAnimation(final View dot){
        animator = ValueAnimator.ofFloat(0, (float) Math.PI);
        animator.setDuration(1100);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        final float width = dot.getLayoutParams().width;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dot.getLayoutParams();
                params.width = (int) (width - (float)(Math.abs(Math.sin(value)) * width * .5f));
                params.height = params.width;
                dot.setLayoutParams(params);
                dot.invalidate();
            }
        });
        animator.start();
    }

}

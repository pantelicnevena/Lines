package rs.project4420.lines;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.*;

import rs.project4420.lines.solver.aStar;


public class DotsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "DotsActivity";
    Adapter adapter;
    Random rnd;
    GridView table;
    GridView gridView;

    List<Integer> colors;

    DotItem[][] matrix = new DotItem[6][6];
    int[][] matrixCopy;
    MatrixItem[][] matrixCopyItem;
    boolean pronadjenCilj;
    List<MatrixItem> putanja;
    List<MatrixItem> put;

    ValueAnimator animator;
    int lastSelected = -1;

    public DotsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);

        Random rnd = new Random();
        List<Integer> pozicija = new ArrayList();
        colors = new ArrayList<>();
        colors.add(R.color.blue);
        colors.add(R.color.red);
        colors.add(R.color.purple);
        colors.add(R.color.yellow);
        colors.add(R.color.orange);
        colors.add(R.color.light_blue);
        colors.add(R.color.green);
        colors.add(R.color.grey);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                matrix[i][j] = new DotItem(R.color.grey);
            }
        }
        for (int i = 0; i < 15; i++) {
            matrix[rnd.nextInt(6)][rnd.nextInt(6)].setColor(colors.get(rnd.nextInt(7)));
        }
        //TODO ne sme da dodje do ponavljanja pozicija na koja se ubacuju obojena polja

        gridView = (GridView)findViewById(R.id.table);
        adapter = new Adapter(this, matrix);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position:" + position);

        //stopiraj prethodnu
        if(animator != null && animator.isRunning()){
            animator.end();
            view.clearAnimation();
            animator = null;
        }
        //kliknuto drugo obojeno polje
        if(lastSelected != position){
            if (matrix[position/6][position%6].getColor() == R.color.grey || matrix[position/6][position%6].getColor() == R.color.grey_dark){
                if (lastSelected == -1) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(40);
                } else {
                    pronadjenCilj = false;
                    matrixCopy = napraviKopiju(matrix);
                    matrixCopyItem = napraviKopijuPolja(matrix);
                    List<MatrixItem> putanja = new ArrayList<>();

                    aStar astar = new aStar();
                    putanja = astar.aZvezda(matrixCopyItem, (lastSelected / 6), (lastSelected % 6), (position / 6), (position % 6));

                    if (putanja.size() > 0) {
                        final List<MatrixItem> finalPutanja = putanja;

                        int lastColor = matrix[lastSelected / 6][lastSelected % 6].getColor();
                        matrix[lastSelected / 6][lastSelected % 6].setColor(R.color.grey);
                        matrix[position / 6][position % 6].setColor(lastColor);

                    }else {
                        lastSelected = -1;
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(40);
                        Log.d(TAG, "NE MOZE DA SE DODJE DO CILJA!!!!!!!!!!!!!!!");
                    };

                    adapter.notifyDataSetChanged();
                }
            } else{
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
                lastSelected = position;
                animator.start();
            }

        } else {  //kliknuto isto obojeno polje
            lastSelected = -1;
        }
    }

    public void stampajMatricu (DotItem[][] matrix){
        for (int i = 0; i < 6; i++) {
            List lista = new ArrayList();
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j].getColor() == R.color.grey)
                    lista.add(0);
                else lista.add(1);
            }
            Log.d(TAG, i + ": " + lista);
        }
    }

    public int[][] napraviKopiju (DotItem[][] matrix){
        int[][] kopija = new int[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j].getColor() == R.color.grey)
                    kopija[i][j] = 0;
                else kopija[i][j] = 1;
            }
        }
        stampajKopiju(kopija);
        return kopija;
    };

    public MatrixItem[][] napraviKopijuPolja (DotItem[][] matrix){
        MatrixItem[][] kopija = new MatrixItem[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j].getColor() == R.color.grey)
                    kopija[i][j] = new MatrixItem(i, j, 0);
                else kopija[i][j] = new MatrixItem(i, j, -1);
            }
        }
        stampajKopijuPolja(kopija);
        return kopija;
    };

    public void stampajKopiju(int[][] kopija){
        for (int j = 0; j < 6; j++) {
            List lista = new ArrayList();
            for (int k = 0; k < 6; k++) {
                lista.add(kopija[j][k]);
            }
            Log.d(TAG, j + ": " + lista);
        }
    };

    public void stampajKopijuPolja(MatrixItem[][] kopija){
        for (int j = 0; j < 6; j++) {
            List lista = new ArrayList();
            for (int k = 0; k < 6; k++) {
                lista.add(kopija[j][k].getValue());
            }
            Log.d(TAG, j + ": " + lista);
        }
    };

    public List<Polje> vratiListuPraznihPolja(DotItem[][] matrix){
        List<Polje> praznaPolja = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j].getColor() == R.color.grey) praznaPolja.add(new Polje(i,j));
            }
        }
        return praznaPolja;
    };



    public boolean proveraPolja(int[][] kopija, int n, int m, int xCilj, int yCilj){
        if ((n+1 <= 5) && (n+1 >= 0) && (m <= 5) && (m >= 0) && (kopija[n+1][m] == 0)){
            if (n+1 == xCilj && m == yCilj) {
                pronadjenCilj = true;
            } else{
                if (pronadjenCilj == false) {
                    kopija[n + 1][m] = 2;
                    proveraPolja(kopija, n + 1, m, xCilj, yCilj);
                    //Log.d(TAG, "DOLE");
                }
            }
        }
        if ((n-1 <= 5) && (n-1 >= 0) && (m <= 5) && (m >= 0) && (kopija[n-1][m] == 0 )){
            if (n-1 == xCilj && m == yCilj) {
                pronadjenCilj = true;
            } else{
                if (pronadjenCilj == false) {
                    kopija[n - 1][m] = 2;
                    proveraPolja(kopija, n - 1, m, xCilj, yCilj);
                    //Log.d(TAG, "GORE");
                }
            }
        }
        if ((n <= 5) && (n >= 0) && (m+1 <= 5) && (m+1 >= 0) && (kopija[n][m+1] == 0 )){
            if (n == xCilj && m+1 == yCilj) {
                pronadjenCilj = true;
            } else {
                if (pronadjenCilj == false) {
                    kopija[n][m + 1] = 2;
                    proveraPolja(kopija, n, m + 1, xCilj, yCilj);
                    //Log.d(TAG, "DESNO");
                }
            }
        }
        if ((n <= 5) && (n >= 0) && (m-1 <= 5) && (m-1 >= 0) && (kopija[n][m-1] == 0 )){
            if (n == xCilj && m-1 == yCilj) {
                pronadjenCilj = true;
            } else {
                if (pronadjenCilj == false) {
                    kopija[n][m - 1] = 2;
                    proveraPolja(kopija, n, m - 1, xCilj, yCilj);
                    //Log.d(TAG, "LEVO");
                }
            }
        }
        //stampajKopiju(kopija);
        return pronadjenCilj;
    }
}

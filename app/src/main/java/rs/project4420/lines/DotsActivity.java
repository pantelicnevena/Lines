package rs.project4420.lines;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
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
    List<ValueAnimator> listaVA;

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
            if (matrix[position/6][position%6].getColor() == R.color.grey){
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

                    if (putanja != null) {
                        final List<MatrixItem> finalPutanja = putanja;

                        int lastColor = matrix[lastSelected / 6][lastSelected % 6].getColor();
                        matrix[lastSelected / 6][lastSelected % 6].setColor(R.color.grey);
                        matrix[position / 6][position % 6].setColor(lastColor);

                        //animacija puta
                        listaVA = new ArrayList<>();
                        for (int i = 0; i <putanja.size() ; i++) {
                            ValueAnimator va = ValueAnimator.ofFloat(0, (float) Math.PI);
                            listaVA.add(va);
                            va.setDuration(2000);
                            va.setRepeatCount(20);
                            final DotView tackica = (DotView) gridView.getChildAt(putanja.get(i).getxTrenutno()*6+putanja.get(i).getyTrenutno());

                            final float origRadius = tackica.radius;
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float value = (float) animation.getAnimatedValue();
                                    tackica.setColorInt((int) (Math.abs(16007990/Math.sin(value))));
                                    tackica.invalidate();
                                }
                            });
                            va.start();
                        }

                        //zaustavljanje animacije puta
                        final List<MatrixItem> finalPutanja1 = putanja;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < finalPutanja1.size() ; i++) {
                                    DotView tackica = (DotView) gridView.getChildAt(finalPutanja1.get(i).getxTrenutno()*6+ finalPutanja1.get(i).getyTrenutno());
                                    listaVA.get(i).end();
                                    tackica.clearAnimation();
                                    tackica.setColor(R.color.grey);
                                    tackica.invalidate();
                                }
                            }
                        }, 250);

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

}

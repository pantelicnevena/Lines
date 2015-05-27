package rs.project4420.lines;

import android.animation.ValueAnimator;
import android.app.ListActivity;
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
                //ako prethodno nije kliknuto na obojeno dugme
                if (lastSelected == -1) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(40);
                } else {
                    pronadjenCilj = false;
                    matrixCopy = napraviKopiju(matrix);
                    matrixCopyItem = napraviKopijuPolja(matrix);
                    List<MatrixItem> putanja = new ArrayList<>();
                    int xCilj = position/6;
                    int yCilj = position%6;

                    aStar astar = new aStar();
                    putanja = astar.aZvezda(matrixCopyItem, (lastSelected / 6), (lastSelected % 6), (xCilj), (yCilj));

                    if (putanja != null) {
                        final List<MatrixItem> finalPutanja = putanja;
                        int lastColor = matrix[lastSelected / 6][lastSelected % 6].getColor();
                        matrix[lastSelected / 6][lastSelected % 6].setColor(R.color.grey);
                        matrix[position / 6][position % 6].setColor(lastColor);

                        tranzicija(matrix, position, putanja, gridView);
                        lastSelected = -1;

                        //TODO provera 4 iste kuglice?
                        //TODO provera 4 iste kuglice kada se pojavi nova

                        ponistiNizGore(xCilj, yCilj);
                        ponistiNizLevo(xCilj, yCilj);
                        ponistiNizDijagonalnoGlavna(xCilj, yCilj);
                        ponistiNizDijagonalnoSporedna(xCilj, yCilj);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vratiNoviDot(matrix);
                                adapter.notifyDataSetChanged();
                            }
                        }, 370);

                    }else { //ako ne moze da stigne do cilja
                        lastSelected = -1;
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(40);
                        Log.d(TAG, "NE MOZE DA SE DODJE DO CILJA!!!!!!!!!!!!!!!");
                    };

                    adapter.notifyDataSetChanged();
                }
            } else { //pokretanje animacije kliknutog dugmeta
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

    private DotItem[][] vratiNoviDot(final DotItem[][] matrix) {
        rnd = new Random();
        List<Polje> praznaPolja = vratiListuPraznihPolja(matrix);

        int praznoPolje = rnd.nextInt(praznaPolja.size());
        int boja = colors.get(rnd.nextInt(7));

        final Polje p = praznaPolja.get(praznoPolje);
        matrix[p.getN()][p.getM()].setColor(boja);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ponistiNizGore(p.getN(), p.getM());
                ponistiNizLevo(p.getN(),p.getM());
                adapter.notifyDataSetChanged();
            }
        }, 300);

        Log.d(TAG, "Pozicija:" + p.getN() + "" + p.getM() + ", boja: " + matrix[p.getN()][p.getM()].getColor());

        return matrix;
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
        Log.d(TAG, "Lista praznih polja: "+praznaPolja);
        return praznaPolja;
    };

    public void tranzicija(final DotItem[][]matrix, int position, List<MatrixItem> put, GridView gv){
        //animacija puta
        final List<ValueAnimator> animList = new ArrayList<>();
        for (int i = 0; i <put.size() ; i++) {

            ValueAnimator va = ValueAnimator.ofFloat(0, 1);
            animList.add(va);
            va.setDuration(300);
            va.setRepeatCount(2);
            final DotView tackica = (DotView) gv.getChildAt(put.get(i).getxTrenutno()*6+put.get(i).getyTrenutno());
            final int color = matrix[position/6][position%6].getColor();
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedFraction();
                    if (value<=1 && value>0.8) tackica.setColor(R.color.grey);
                    if (value<=0.8 && value>=0.2) tackica.setColor(color);
                    if (value<=0 && value>0.2) tackica.setColor(R.color.grey);
                    tackica.invalidate();
                }
            });
            va.start();

            //zaustavljanje animacije puta
            final List<MatrixItem> finalPutanja1 = put;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < finalPutanja1.size() ; i++) {
                        DotView tackica = (DotView) gridView.getChildAt(finalPutanja1.get(i).getxTrenutno()*6+ finalPutanja1.get(i).getyTrenutno());
                        animList.get(i).end();
                        tackica.clearAnimation();
                        tackica.setColor(R.color.grey);
                        tackica.invalidate();

                    }
                }
            }, 350); //pauza 350ms
        }
    }

    public void ponistiNizGore(int xCilj, int yCilj){
        List<Polje> kuglice = null;
        if (xCilj+1 != 0){ //moze gore
            kuglice = new ArrayList<>();
            int xGore = xCilj + 1;
            int yGore = yCilj;
            int xDole = xCilj - 1;
            int yDole = yCilj;
            kuglice.add(new Polje(xCilj, yCilj));

            while (kuglice.size()<=5){
                Log.d(TAG, "Gore: "+xGore+""+yGore+", dole: "+xDole+""+yDole);
                if (xGore<=5 && matrix[xCilj][yCilj].getColor() == matrix[xGore][yGore].getColor()){
                    kuglice.add(new Polje(xGore, yGore));
                    Log.d(TAG, "Gore: "+xGore+""+yGore);
                    xGore++;
                } else {
                    if (xDole >=0 && matrix[xCilj][yCilj].getColor() == matrix[xDole][yDole].getColor()) {
                        kuglice.add(new Polje(xDole, yDole));
                        Log.d(TAG, "Dole: "+xDole+""+yDole);
                        xDole--;
                    } else break;
                }
            }
            if (kuglice.size() >= 4) {
                for (int i = 0; i < kuglice.size(); i++) {
                    matrix[kuglice.get(i).getN()][kuglice.get(i).getM()].setColor(R.color.grey);
                };
            }
            else kuglice = null;
        }
    }

    public void ponistiNizLevo(int xCilj, int yCilj){
        List<Polje> kuglice = null;
        if (yCilj+1 != 0){ //moze dole
            kuglice = new ArrayList<>();
            int xDesno = xCilj;
            int yDesno = yCilj + 1;
            int xLevo = xCilj;
            int yLevo = yCilj - 1;
            kuglice.add(new Polje(xCilj, yCilj));

            while (kuglice.size()<=5){
                Log.d(TAG, "Kuglice: "+kuglice);
                Log.d(TAG, "Desno: "+xDesno+""+yDesno+", levo: "+xLevo+""+yLevo);
                if (yDesno<=5 && matrix[xCilj][yCilj].getColor() == matrix[xDesno][yDesno].getColor()){
                    kuglice.add(new Polje(xDesno, yDesno));
                    Log.d(TAG, "Desno!");
                    yDesno++;
                } else {
                    if (yLevo >=0 && matrix[xCilj][yCilj].getColor() == matrix[xLevo][yLevo].getColor()) {
                        kuglice.add(new Polje(xLevo, yLevo));
                        yLevo--;
                        Log.d(TAG, "Levo!");
                    } else break;
                }
            }
            Log.d(TAG, ""+kuglice);
            if (kuglice.size() >= 4) {
                for (int i = 0; i < kuglice.size(); i++) {
                    matrix[kuglice.get(i).getN()][kuglice.get(i).getM()].setColor(R.color.grey);
                };
            }
        }
    }

    public void ponistiNizDijagonalnoGlavna(int xCilj, int yCilj){
        List<Polje> kuglice = null;
        if (yCilj+1 != 0){ //moze dole
            kuglice = new ArrayList<>();
            int xGoreDesno = xCilj - 1;
            int yGoreDesno = yCilj + 1;
            int xDoleLevo = xCilj + 1;
            int yDoleLevo = yCilj - 1;
            kuglice.add(new Polje(xCilj, yCilj));

            while (kuglice.size()<=5){
                Log.d(TAG, "Kuglice: "+kuglice);
                Log.d(TAG, "Desno: "+xGoreDesno+""+yGoreDesno+", levo: "+xDoleLevo+""+yDoleLevo);
                if (yGoreDesno<=5 && xGoreDesno >=0 && matrix[xCilj][yCilj].getColor() == matrix[xGoreDesno][yGoreDesno].getColor()){
                    kuglice.add(new Polje(xGoreDesno, yGoreDesno));
                    Log.d(TAG, "Desno!");
                    xGoreDesno--;
                    yGoreDesno++;
                } else {
                    if (yDoleLevo >=0 && xDoleLevo <=5 && matrix[xCilj][yCilj].getColor() == matrix[xDoleLevo][yDoleLevo].getColor()) {
                        kuglice.add(new Polje(xDoleLevo, yDoleLevo));
                        xDoleLevo++;
                        yDoleLevo--;
                        Log.d(TAG, "Levo!");
                    } else break;
                }
            }
            Log.d(TAG, ""+kuglice);
            if (kuglice.size() >= 4) {
                for (int i = 0; i < kuglice.size(); i++) {
                    matrix[kuglice.get(i).getN()][kuglice.get(i).getM()].setColor(R.color.grey);
                };
            }
        }
    }


    public void ponistiNizDijagonalnoSporedna(int xCilj, int yCilj){
        List<Polje> kuglice = null;
        if (yCilj+1 != 0){ //moze dole
            kuglice = new ArrayList<>();
            int xGoreLevo = xCilj - 1;
            int yGoreLevo = yCilj - 1;
            int xDoleDesno = xCilj + 1;
            int yDoleDesno = yCilj + 1;
            kuglice.add(new Polje(xCilj, yCilj));

            while (kuglice.size()<=5){
                Log.d(TAG, "Kuglice: "+kuglice);
                Log.d(TAG, "Desno: "+xDoleDesno+""+yDoleDesno+", levo: "+xGoreLevo+""+yGoreLevo);
                if (xDoleDesno<=5 && yDoleDesno <=5 && matrix[xCilj][yCilj].getColor() == matrix[xDoleDesno][yDoleDesno].getColor()){
                    kuglice.add(new Polje(xDoleDesno, yDoleDesno));
                    Log.d(TAG, "Desno!");
                    xDoleDesno++;
                    yDoleDesno++;
                } else {
                    if (xGoreLevo >=0 && yGoreLevo >=0 && matrix[xCilj][yCilj].getColor() == matrix[xGoreLevo][yGoreLevo].getColor()) {
                        kuglice.add(new Polje(xGoreLevo, yGoreLevo));
                        xGoreLevo--;
                        yGoreLevo--;
                        Log.d(TAG, "Levo!");
                    } else break;
                }
            }
            Log.d(TAG, ""+kuglice);
            if (kuglice.size() >= 4) {
                for (int i = 0; i < kuglice.size(); i++) {
                    matrix[kuglice.get(i).getN()][kuglice.get(i).getM()].setColor(R.color.grey);
                };
            }
        }
    }
}

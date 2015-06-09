package rs.project4420.lines.classes;

import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import rs.project4420.lines.classes.Polje;

/**
 * Created by nevena on 18.4.15..
 */
public class Matrica {
    private static final String TAG = "Matrica";
    int[][] polja = new int[6][6];
    boolean pronadjenCilj = false;
    List<Polje> zid = new ArrayList<>();
    GridView gridView;

    public Matrica(GridView gridView) {
        this.gridView = gridView;
    }

    public int[][] kreirajMatricu(){
        for (int j = 0; j < 36; j++) {
            Button polje = (Button) gridView.getItemAtPosition(j);
            int n = j/6;
            int m = j%6;
            if (polje.getDrawingCacheBackgroundColor() == 2131230754){
                polja[n][m] = 0;
            } else {
                polja[n][m] = 1;
                zid.add(new Polje(n, m));
            }
        }
        napraviKopiju();
        return polja;
    }

    public void stampajMatricu(){
        for (int j = 0; j < 6; j++) {
            List lista = new ArrayList();
            for (int k = 0; k < 6; k++) {
                lista.add(polja[j][k]);
            }
//            Log.d(TAG, j + ": " + lista);
        }
    }

    public void stampajMatricu(int[][] matrica){
        for (int j = 0; j < 6; j++) {
            List lista = new ArrayList();
            for (int k = 0; k < 6; k++) {
                lista.add(matrica[j][k]);
            }
//            Log.d(TAG, j + ": " + lista);
        }
    }

    public void napraviKopiju(){
        int [][] kopija = polja;
        //Log.d(TAG, "Kopija napravljena!");
    }

    public int[][] izmeniMatricu(Polje start, Polje cilj){
        polja[start.getN()][start.getM()] = 0;
        polja[cilj.getN()][cilj.getM()] = 1;
        stampajMatricu(polja);
        napraviKopiju();
        return polja;
    }
}

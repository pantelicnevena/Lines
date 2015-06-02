package rs.project4420.lines;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nevena on 2.6.15..
 */
public class LineSuccess {

    private static final String TAG = "LineSucc";

    public static DotItem[][] ponistiNizGore(int xCilj, int yCilj, DotItem[][] matrix){
        List<Polje> kuglice = null;
        if (xCilj+1 != 0){ //moze gore
            kuglice = new ArrayList<>();
            int xGore = xCilj + 1;
            int yGore = yCilj;
            int xDole = xCilj - 1;
            int yDole = yCilj;
            kuglice.add(new Polje(xCilj, yCilj));

            while (kuglice.size()<=5){
                Log.d(TAG, "Gore: " + xGore + "" + yGore + ", dole: " + xDole + "" + yDole);
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
        return matrix;
    }

    public static DotItem[][] ponistiNizLevo(int xCilj, int yCilj, DotItem[][] matrix){
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
        return matrix;
    }

    public static DotItem[][] ponistiNizDijagonalnoGlavna(int xCilj, int yCilj, DotItem[][] matrix){
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
        return matrix;
    }


    public static DotItem[][] ponistiNizDijagonalnoSporedna(int xCilj, int yCilj, DotItem[][] matrix){
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
        return matrix;
    }
}

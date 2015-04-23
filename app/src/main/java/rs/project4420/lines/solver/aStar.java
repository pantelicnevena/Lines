package rs.project4420.lines.solver;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rs.project4420.lines.MatrixItem;

/**
 * Created by nevena on 23.4.15..
 */
public class aStar {

    private static final String TAG = "aStar";
    List<MatrixItem> put;

    public aStar() {
    }

    public List<MatrixItem> aZvezda(MatrixItem[][] kopija, int n, int m, int xCilj, int yCilj){
        List<MatrixItem> zatvorenaLista = new ArrayList<>();
        List<MatrixItem> otvorenaLista = new ArrayList<>();
        boolean kraj = false;
        MatrixItem[][] kopijaKopije = kopija.clone();

        kopija[n][m].setKoraci(Math.abs(n-xCilj)+Math.abs(n-yCilj));
        kopija[n][m].setPomeraj(0);
        zatvorenaLista.add(kopija[n][m]);

        MatrixItem gore = vratiGore(kopija, n, m, xCilj, yCilj);
        MatrixItem dole = vratiDole(kopija, n, m, xCilj, yCilj);
        MatrixItem levo = vratiLevo(kopija, n, m, xCilj, yCilj);
        MatrixItem desno = vratiDesno(kopija, n, m, xCilj, yCilj);

        if (gore.getKoraci() != -1) otvorenaLista.add(gore);
        if (dole.getKoraci() != -1) otvorenaLista.add(dole);
        if (levo.getKoraci() != -1) otvorenaLista.add(levo);
        if (desno.getKoraci() != -1) otvorenaLista.add(desno);

        MatrixItem next = new MatrixItem();

        while (otvorenaLista.size()>0){
            int min = 100;
            for (int i = 0; i < otvorenaLista.size(); i++) {
                if (otvorenaLista.get(i).getKoraci() < min) {
                    min = otvorenaLista.get(i).getKoraci();
                    next = otvorenaLista.get(i);
                }
            }

            zatvorenaLista.add(next);
            Log.d(TAG, next + " " + zatvorenaLista);
            Log.d(TAG,  ""+otvorenaLista);

            for (int i = 0; i < otvorenaLista.size(); i++) {
                if (otvorenaLista.get(i) == next) otvorenaLista.remove(i);
            }

            if (zatvorenaLista.get(zatvorenaLista.size()-1).getxTrenutno() == xCilj &&
                    zatvorenaLista.get(zatvorenaLista.size()-1).getyTrenutno() == yCilj) {
                Log.d(TAG, "CIIIILJ");
                kraj = true;
                break;
            }


            MatrixItem up = new MatrixItem();
            boolean proveraUp = true;
            for (int i = 0; i <zatvorenaLista.size() ; i++) {
                if (zatvorenaLista.get(i).getxTrenutno() == next.getxTrenutno() - 1
                        && zatvorenaLista.get(i).getyTrenutno() == next.getyTrenutno())
                    proveraUp = false;
            }
            for (int j = 0; j < otvorenaLista.size(); j++) {
                if (otvorenaLista.get(j).getxTrenutno() == next.getxTrenutno()-1 &&
                        otvorenaLista.get(j).getyTrenutno() == next.getyTrenutno())
                    proveraUp = false;
            }
            if (proveraUp) up = vratiGore(kopija, next.getxTrenutno(), next.getyTrenutno(), xCilj, yCilj);
            if (up.getKoraci() != -1) otvorenaLista.add(up);



            MatrixItem down = new MatrixItem();
            boolean proveraDown = true;
            for (int i = 0; i <zatvorenaLista.size() ; i++) {
                if (zatvorenaLista.get(i).getxTrenutno() == next.getxTrenutno()+1
                        && zatvorenaLista.get(i).getyTrenutno() == next.getyTrenutno()) proveraDown = false;
            }
            for (int j = 0; j < otvorenaLista.size(); j++) {
                if (otvorenaLista.get(j).getxTrenutno() == next.getxTrenutno() + 1 &&
                        otvorenaLista.get(j).getyTrenutno() == next.getyTrenutno())
                    proveraDown = false;
            }
            if (proveraDown) down = vratiDole(kopija, next.getxTrenutno(), next.getyTrenutno(), xCilj, yCilj);
            if (down.getKoraci() != -1) otvorenaLista.add(down);


            MatrixItem left = new MatrixItem();
            boolean proveraLeft = true;
            for (int i = 0; i <zatvorenaLista.size() ; i++) {
                if (zatvorenaLista.get(i).getxTrenutno() == next.getxTrenutno()
                        && zatvorenaLista.get(i).getyTrenutno() == next.getyTrenutno() - 1)
                    proveraLeft = false;
            }
            for (int j = 0; j < otvorenaLista.size(); j++) {
                if (otvorenaLista.get(j).getxTrenutno() == next.getxTrenutno() &&
                        otvorenaLista.get(j).getyTrenutno() == next.getyTrenutno()-1) proveraLeft = false;
            }
            if (proveraLeft) left = vratiLevo(kopija, next.getxTrenutno(), next.getyTrenutno(), xCilj, yCilj);
            if (left.getKoraci() != -1) otvorenaLista.add(left);


            MatrixItem right = new MatrixItem();
            boolean proveraRight = true;
            for (int i = 0; i <zatvorenaLista.size() ; i++) {
                if (zatvorenaLista.get(i).getxTrenutno() == next.getxTrenutno()
                        && zatvorenaLista.get(i).getyTrenutno() == next.getyTrenutno() + 1)
                    proveraRight = false;
            }
            for (int j = 0; j < otvorenaLista.size(); j++) {
                if (otvorenaLista.get(j).getxTrenutno() == next.getxTrenutno() &&
                        otvorenaLista.get(j).getyTrenutno() == next.getyTrenutno()+1) proveraRight = false;
            }
            if (proveraRight) right = vratiDesno(kopija, next.getxTrenutno(), next.getyTrenutno(), xCilj, yCilj);
            if (right.getKoraci() != -1) otvorenaLista.add(right);


            Log.d(TAG, "Next: "+next+", gore: "+up+", dole: "+down+", levo: "+left+", desno: "+right );

        };

        put = vratiPutanju(zatvorenaLista);

        //Log.d(TAG, ""+zatvorenaLista);
        //Log.d(TAG, ""+otvorenaLista);
        Log.d(TAG, "Putanja: "+put);

        if (!kraj) put = null;
        return put;
    }

    public List<MatrixItem> vratiPutanju(List<MatrixItem> zatvorenaLista){
        List<MatrixItem> put = new ArrayList<>();
        put.add(zatvorenaLista.get(zatvorenaLista.size()-1));
        int index = zatvorenaLista.size()-1;

        while (zatvorenaLista.get(index).getxPrethodno() != -1 && zatvorenaLista.get(index).getyPrethodno() != -1){
            for (int i = 0; i < zatvorenaLista.size(); i++) {
                if (zatvorenaLista.get(i).getxTrenutno() == put.get(put.size()-1).getxPrethodno()
                        && zatvorenaLista.get(i).getyTrenutno() == put.get(put.size()-1).getyPrethodno())
                    put.add(zatvorenaLista.get(i));
            }
            index--;
        }

        List<MatrixItem> naopakPut = new ArrayList<>();
        for (int i = put.size()-2; i > 0 ; i--) {
            naopakPut.add(put.get(i));
        }
        Log.d(TAG, ""+naopakPut);
        return naopakPut;
    }

    public MatrixItem vratiGore (MatrixItem[][] kopija, int n, int m, int xCilj, int yCilj){
        MatrixItem gore = new MatrixItem();
        if (n-1>=0 && n-1<=5 && m>=0 && m<=5 && kopija[n-1][m].getValue() != -1) {
            gore = kopija[n-1][m];
            int man = Math.abs(n-1 - xCilj) + Math.abs(m - yCilj);
            gore.setKoraci(kopija[n][m].getPomeraj()+1+man);
            gore.setPomeraj(kopija[n][m].getPomeraj()+1);
            gore.setxPrethodno(kopija[n][m].getxTrenutno());
            gore.setyPrethodno(kopija[n][m].getyTrenutno());
        }
        return gore;
    };

    public MatrixItem vratiDole (MatrixItem[][] kopija, int n, int m, int xCilj, int yCilj){
        MatrixItem dole = new MatrixItem();
        if (n+1>=0 && n+1<=5 && m>=0 && m<=5 && kopija[n+1][m].getValue() != -1) {
            dole = kopija[n+1][m];
            int man = Math.abs(n+1 - xCilj) + Math.abs(m - yCilj);
            dole.setKoraci(kopija[n][m].getPomeraj()+1+man);
            dole.setPomeraj(kopija[n][m].getPomeraj()+1);
            dole.setxPrethodno(kopija[n][m].getxTrenutno());
            dole.setyPrethodno(kopija[n][m].getyTrenutno());
        }
        return dole;
    };

    public MatrixItem vratiLevo (MatrixItem[][] kopija, int n, int m, int xCilj, int yCilj){
        MatrixItem levo = new MatrixItem();
        if (n>=0 && n<=5 && m-1>=0 && m-1<=5 && kopija[n][m-1].getValue() != -1) {
            levo = kopija[n][m-1];
            int man = Math.abs(n - xCilj) + Math.abs(m-1 - yCilj);
            levo.setKoraci(kopija[n][m].getPomeraj()+1+man);
            levo.setPomeraj(kopija[n][m].getPomeraj()+1);
            levo.setxPrethodno(kopija[n][m].getxTrenutno());
            levo.setyPrethodno(kopija[n][m].getyTrenutno());
        }
        return levo;
    };

    public MatrixItem vratiDesno (MatrixItem[][] kopija, int n, int m, int xCilj, int yCilj){
        MatrixItem desno = new MatrixItem();
        if (n>=0 && n<=5 && m+1>=0 && m+1<=5 && kopija[n][m+1].getValue() != -1) {
            desno =kopija[n][m+1];
            int man = Math.abs(n - xCilj) + Math.abs(m+1 - yCilj);
            desno.setKoraci(kopija[n][m].getPomeraj()+1+man);
            desno.setPomeraj(kopija[n][m].getPomeraj()+1);
            desno.setxPrethodno(kopija[n][m].getxTrenutno());
            desno.setyPrethodno(kopija[n][m].getyTrenutno());
        }
        return desno;
    };
}

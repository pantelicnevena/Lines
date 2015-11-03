package rs.project4420.lines.connect4;

import android.util.Log;

/**
 * Created by nevena on 30.10.15..
 */
public class NodeScoring {

    private static final String TAG = NodeScoring.class.getSimpleName();

    public static int vratiSumu(CoinItem[][] data, int kolona, int turn, int znak) {
        int sum = 0;
        sum += proveriPoziciju(data, kolona, turn);
        int pretraga = pretraga(data, kolona, turn, znak, Constants.PLAYER_1); //zatvaranje drugog igraca
        if (Math.abs(pretraga) == (int)Double.POSITIVE_INFINITY) return pretraga;
        else sum += pretraga;
        sum += pretraga(data, kolona, -turn, znak, Constants.PLAYER_2); //nastavljanje niza
        return sum;
    }

    public static int proveriPoziciju(CoinItem[][] data, int kolona, int turn) {
        int suma = 0;
        int red = -1;
        for (int i = 5; i >= 0; i--) {
            if (data[i][kolona].getCoinOwner() == turn) {
                red = i;
            }
        }

        if (kolona == 0 || kolona == 6) { if (red == 0 || red == 5) suma=3; if (red == 1 || red == 4) suma=4; if (red == 2 || red ==3) suma=5; }
        if (kolona == 1 || kolona == 5) { if (red == 0 || red == 5) suma=4; if (red == 1 || red == 4) suma=6; if (red == 2 || red ==3) suma=8; }
        if (kolona == 2 || kolona == 4) { if (red == 0 || red == 5) suma=5; if (red == 1 || red == 4) suma=8; if (red == 2 || red ==3) suma=11;}
        if (kolona == 3){ if (red == 0 || red == 5) suma =7; if (red == 1 || red == 4) suma =10; if (red == 2 || red == 3) suma =13; }

        return suma;
    }

    public static int pretraga(CoinItem[][] data, int kolona, int turn, int znak, int player) { //turn je suigrac
        int suma = 0;
        int red = -1;
        for (int i = 5; i >= 0; i--) {
            if (data[i][kolona].getCoinOwner() == turn * player) {
                red = i;
            }
        }

        //Log.d(TAG, "Kolona: "+kolona+", red: "+red);
        if (red>=0 && red<=5) {
            /*int levo = pretragaLevoDesno(data, kolona, red, turn, znak, 1, player); //desno
            if (levo == (int) ((int)znak*Double.POSITIVE_INFINITY)) return levo; else suma += levo;
            int desno = pretragaLevoDesno(data, kolona, red, turn, znak, -1, player); //levo
            if (desno == (int) ((int)znak*Double.POSITIVE_INFINITY)) return desno; else suma += desno;*/
            suma += pretragaLD(data, kolona, red, turn, player);
            int dole = pretragaDole(data, kolona, red, turn, znak, player);  //dole
            if (dole == (int) ((int)znak*Double.POSITIVE_INFINITY)) return dole; else suma += dole;
            int goreDesno = pretragaGDesnoDLevo(data, kolona, red, turn, znak, 1, player);
            if (goreDesno == (int) ((int)znak*Double.POSITIVE_INFINITY)) return goreDesno; else suma += goreDesno;
            int doleLevo = pretragaGDesnoDLevo(data, kolona, red, turn, znak, -1, player);
            if (doleLevo == (int) ((int)znak*Double.POSITIVE_INFINITY)) return doleLevo; else suma += doleLevo;
            int goreLevo = pretragaGLevoDDesno(data, kolona, red, turn, znak, 1, player);
            if (goreLevo == (int) ((int)znak*Double.POSITIVE_INFINITY)) return goreLevo; else suma += goreLevo;
            int doleDesno = pretragaGLevoDDesno(data, kolona, red, turn, znak, -1, player);
            if (doleDesno == (int) ((int)znak*Double.POSITIVE_INFINITY)) return doleDesno; else suma += doleDesno;
        }
        //Log.d(TAG, "Ukupna suma: " + suma);
        return suma;
    }

    public static int pretragaLD(CoinItem[][] data, int kolona, int red, int turn, int player){
        int sum = 0;
        int niz = 0;
        boolean zatvorenDesno = false;
        boolean zatvorenLevo = false;
        if (player == Constants.PLAYER_2){ //ako proveravam svoje
            //desno
            if (kolona+1<=6 && data[red][kolona+1].getCoinOwner() == turn){  //ako je desno moja
                niz++;
                int brojac = 2;
                while (kolona+brojac <= 6){
                    if (data[red][kolona+brojac].getCoinOwner() == turn) niz++;
                    else if (kolona-brojac<0 || data[red][kolona-brojac].getCoinOwner() == -turn) zatvorenDesno = true;
                    brojac++;
                }
            } else if (kolona+1>6 || data[red][kolona+1].getCoinOwner() == -turn) zatvorenDesno = true;
            //levo
            if (kolona-1>=0 && data[red][kolona-1].getCoinOwner() == turn){
                niz++;
                int brojac = 2;
                while (kolona-brojac >=0){
                    if (data[red][kolona-brojac].getCoinOwner() == turn) niz++;
                    else if (kolona+brojac>6 || data[red][kolona-brojac].getCoinOwner() == -turn) zatvorenLevo = true;
                    brojac++;
                }
            } else if (kolona-1<0 || data[red][kolona-1].getCoinOwner() == -turn) zatvorenLevo = true;
            if (niz >= 3) return 10000; //ako je pobeda
            else {
                if (zatvorenDesno && zatvorenLevo) return 0; //ako je zatvoreno sa obe strane a nije pobeda
                if (niz == 2) if (zatvorenDesno || zatvorenLevo) return 100; //ako su 3 u nizu
                if (niz == 1) if (zatvorenDesno || zatvorenLevo) return 40; //ako su 2 u nizu
            }
        }
        if (player == Constants.PLAYER_1){ //ako proveravam protivnikove
            if (kolona+1<=6 && data[red][kolona+1].getCoinOwner() == -turn){
                niz++;
                int brojac = 2;
                while (kolona+brojac <= 6){
                    if (data[red][kolona+brojac].getCoinOwner() == -turn) niz++;
                    else if (kolona-brojac<0 || data[red][kolona-brojac].getCoinOwner() == turn) zatvorenDesno = true;
                    brojac++;
                }
            } else if (kolona+1>6 || data[red][kolona+1].getCoinOwner() == turn) zatvorenDesno = true;
            if (kolona-1>=0 && data[red][kolona-1].getCoinOwner() == -turn){
                niz++;
                int brojac = 2;
                while (kolona-brojac >=0){
                    if (data[red][kolona-brojac].getCoinOwner() == -turn) niz++;
                    else if (kolona+brojac>6 || data[red][kolona-brojac].getCoinOwner() == turn) zatvorenLevo = true;
                    brojac++;
                }
            } else if (kolona-1<0 || data[red][kolona-1].getCoinOwner() == turn) zatvorenLevo = true;
            if (niz >= 3) return -10000; //ako je pobeda
            else {
                if (zatvorenDesno && zatvorenLevo) return 0; //ako je zatvoreno sa obe strane a nije pobeda
                if (niz == 2) if (zatvorenDesno || zatvorenLevo) return -100; //ako su 3 u nizu
                if (niz == 1) if (zatvorenDesno || zatvorenLevo) return -40; //ako su 2 u nizu
            }
        }

        return sum;
    }

    /*public static int pretragaLevoDesno(CoinItem[][] data, int kolona, int red, int turn, int znak, int smerPretrage, int player) {
        int suma = 0;
        //Log.d(TAG, "LevoDesno");
        if ((red >= 0) && (red <= 5) && (kolona+smerPretrage >= 0) && (kolona+smerPretrage <= 6) && data[red][kolona + smerPretrage].getCoinOwner() == -turn) {
            int brojac = 1;
            int niz = 0;
            while (kolona+brojac <= 6 && kolona-brojac >= 0) {
                if (data[red][kolona + brojac * smerPretrage].getCoinOwner() == -turn) niz++;
                if (niz >= 3 && player == Constants.PLAYER_2) {
                    suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                    return suma;
                } else {
                    if (player == Constants.PLAYER_1 && niz == 3){
                        suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                        return suma;
                    }
                    //ako se pretrazuju suprotne kuglice u okolini
                    //ako postoji sledece mesto za kuglicu
                    //i ako je sledeca kuglica moja
                    //ili ako je posle poslednje kuglice zid
                    if (player == Constants.PLAYER_1 && ((
                            kolona+brojac*smerPretrage+smerPretrage >= 0 &&
                                    kolona+brojac*smerPretrage+smerPretrage <= 6 &&
                                    data[red][kolona+brojac*smerPretrage+smerPretrage].getCoinOwner() == turn) ||
                            kolona+brojac*smerPretrage+smerPretrage < 0 ||
                            kolona+brojac*smerPretrage+smerPretrage > 6
                    )){
                        suma += evalClose(niz);
                    }
                    //ako postoji sledece mesto za kuglicu
                    //ako je sledece mesto prazno
                    else if (kolona+brojac*smerPretrage+smerPretrage >= 0 &&
                            kolona+brojac*smerPretrage+smerPretrage <= 6 &&
                            data[red][kolona+brojac*smerPretrage+smerPretrage].getCoinOwner() == Constants.COIN_OWNER_GRID)
                        suma += evalClosing(niz);
                    //ako se pretrazuju iste (Player_2)
                    if (player == Constants.PLAYER_2) {
                        suma += evalContinue(niz);
                    }
                }
                brojac++;
            }
            //Log.d(TAG, "LevoDesno: "+niz);//TODO u zavisnosti od vr niza, definisati iznos sume
        }
        return suma;
    }*/

    public static int pretragaD(CoinItem[][] data, int kolona, int red, int turn, int znak, int player){
        int sum = 0;
        int niz = 0;
        boolean zatvorenGore = false;
        boolean zatvorenDole = false;
        if (player == Constants.PLAYER_2){
            if (red+1<=5 && data[red+1][kolona].getCoinOwner() == turn){
                niz++;
                int brojac = 2;
                while (red+brojac <= 6){
                    if (data[red+brojac][kolona].getCoinOwner() == turn) niz++;
                    else if (red+brojac>5 || data[red+brojac][kolona].getCoinOwner() == -turn) zatvorenDole = true;
                    brojac++;
                }
            } else if (red-1<0) zatvorenGore = true;
            if (niz >= 3) return 10000; //ako je pobeda
            else {
                if (zatvorenDole && zatvorenGore) return 0; //ako je zatvoreno sa obe strane a nije pobeda
                if (niz == 2) if (zatvorenDole || zatvorenGore) return 100; //ako su 3 u nizu
                if (niz == 1) if (zatvorenDole || zatvorenGore) return 40; //ako su 2 u nizu
            }
        }
        return sum;
    }

    public static int pretragaDole(CoinItem[][] data, int kolona, int red, int turn, int znak, int player){
        int suma = 0;
        //Log.d(TAG, "Dole");
        if ((red >= 0) && (red <= 4) && (kolona >= 0) && (kolona <= 6) && data[red + 1][kolona].getCoinOwner() == -turn) {
            int brojac = 1;
            int niz = 0;
            while (red + brojac <= 5) {
                if (data[red + brojac][kolona].getCoinOwner() == -turn) niz++;
                if (player == Constants.PLAYER_1 && niz >= 3) {
                    suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                    //Log.d(TAG, "Pobeda: " + suma);
                    return suma;
                } else {
                    if (player == Constants.PLAYER_1 && niz == 3){
                        suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                        return suma;
                    }
                    if (player == Constants.PLAYER_1 &&
                            ((red+brojac+1 >= 0 &&
                                    red+brojac+1 <= 5 &&
                                    data[red+brojac+1][kolona].getCoinOwner() == turn) ||
                                    red+brojac+1 > 5)){
                        suma += evalClose(niz);
                    } else suma += evalClosing(niz);
                    if (player == Constants.PLAYER_2) suma += evalContinue(niz);
                }
                brojac++;
            }
            //Log.d(TAG, "Dole: "+niz);//TODO u zavisnosti od vr niza, definisati iznos sume
        }
        return suma;
    }

    public static int pretragaGDesnoDLevo(CoinItem[][] data, int kolona, int red, int turn, int znak, int smerPretrage, int player){
        int suma = 0;
        //Log.d(TAG, "GDesno DLevo");
        if ((red+smerPretrage >= 0) && (red+smerPretrage <= 5) && (kolona-smerPretrage >= 0) && (kolona-smerPretrage <= 6) && data[red+smerPretrage][kolona-smerPretrage].getCoinOwner() == -turn){
            int brojac = 1;
            int niz = 0;
            while (red-brojac >= 0 && red+brojac <= 5 && kolona-brojac >=0 && kolona+brojac <=6){
                if (data[red+brojac*smerPretrage][kolona-brojac*smerPretrage].getCoinOwner() == -turn) niz ++;
                if (player == Constants.PLAYER_1 && niz >= 3){
                    suma = (int) ((int) znak*Double.POSITIVE_INFINITY);
                    //Log.d(TAG, "Pobeda: " + suma);
                    return suma;
                } else {
                    if (player == Constants.PLAYER_1 && niz == 3){
                        suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                        return suma;
                    }
                    if (player == Constants.PLAYER_1 &&
                            ((red+brojac*smerPretrage+smerPretrage >= 0 &&
                                    red+brojac*smerPretrage+smerPretrage <= 5 &&
                                    kolona-brojac*smerPretrage-smerPretrage >= 0 &&
                                    kolona-brojac*smerPretrage-smerPretrage <= 6 &&
                                    data[red+brojac*smerPretrage+smerPretrage][kolona-brojac*smerPretrage-smerPretrage].getCoinOwner() == turn) ||
                                    (red+brojac*smerPretrage+smerPretrage < 0 || red+brojac*smerPretrage+smerPretrage > 5) && (kolona-brojac*smerPretrage-smerPretrage < 0 || kolona-brojac*smerPretrage-smerPretrage > 6))){
                        suma += evalClose(niz);
                    } else suma += evalClosing(niz);
                    if (player == Constants.PLAYER_2) suma += evalContinue(niz);
                }
                brojac++;
            }
            //Log.d(TAG, "GD i DL: "+niz);//TODO u zavisnosti od vr niza, definisati iznos sume
        }
        return suma;
    }

    public static int pretragaGLevoDDesno(CoinItem[][] data, int kolona, int red, int turn, int znak, int smerPretrage, int player){
        int suma = 0;
        //Log.d(TAG, "GLevo DDesno");
        if ((red+smerPretrage >= 0) && (red+smerPretrage <= 5) && (kolona+smerPretrage >= 0) && (kolona+smerPretrage <= 6) && data[red+smerPretrage][kolona+smerPretrage].getCoinOwner() == -turn){
            int brojac = 1;
            int niz = 0;
            while (red-brojac >= 0 && red+brojac <=5 && kolona-brojac >=0 && kolona+brojac <= 6){
                if (data[red+brojac*smerPretrage][kolona+brojac*smerPretrage].getCoinOwner() == -turn) niz++;
                if (player == Constants.PLAYER_1 && niz >= 3) {
                    suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                    //Log.d(TAG, "Pobeda: " + suma);
                    return suma;
                } else {
                    if (player == Constants.PLAYER_1 && niz == 3){
                        suma = (int) ((int) znak * Double.POSITIVE_INFINITY);
                        return suma;
                    }
                    if (player == Constants.PLAYER_1 &&
                            ((red+brojac*smerPretrage+smerPretrage >= 0 &&
                                    red+brojac*smerPretrage+smerPretrage <= 5 &&
                                    kolona+brojac*smerPretrage+smerPretrage >= 0 &&
                                    kolona+brojac*smerPretrage+smerPretrage <= 6 &&
                                    data[red+brojac*smerPretrage+smerPretrage][kolona+brojac*smerPretrage+smerPretrage].getCoinOwner() == turn) ||
                                    (red+brojac*smerPretrage+smerPretrage <0 || red+brojac*smerPretrage+smerPretrage >5) && (kolona+brojac*smerPretrage+smerPretrage <0 || kolona+brojac*smerPretrage+smerPretrage>6))){
                        suma += evalClose(niz);
                    } else suma += evalClosing(niz);
                    if (player == Constants.PLAYER_2) suma += evalContinue(niz);
                }
                brojac++;
            }
            //Log.d(TAG, "GL i DD: "+niz);//TODO u zavisnosti od vr niza, definisati iznos sume
        }
        return suma;
    }

    public static int evalContinue(int a){
        int x = 50;
        //Log.d(TAG, "Nastavak: "+stepenovanje(x, a));
        return (int)Math.pow(x, a);
    }

    public static int evalClosing(int b){
        int y = 30;
        //Log.d(TAG, "Zatvaranje: "+stepenovanje(y, b));
        return (int)Math.pow(y, b);
    }

    public static int evalClose(int c){
        int z = 70;
        //Log.d(TAG, "Zatvoren: "+stepenovanje(z, stepenovanje(c, c)));
        return (int)Math.pow(z, c);
    }

    public static int evalGroup(int d){
        int f = 0;
        //Log.d(TAG, "Grupisanje: "+stepenovanje(f, d));
        return (int)Math.pow(f, d);
    }

}

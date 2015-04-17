package rs.project4420.lines;

import android.widget.Button;

/**
 * Created by nevena on 14.4.15..
 */
public class Polje {
    int n;
    int m;
    Button b;

    public Polje() {
    }

    public Polje(int n, int m, Button b) {
        this.n = n;
        this.m = m;
        this.b = b;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public Button getB() {
        return b;
    }

    public void setB(Button b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return ""+n+m;
    }
}

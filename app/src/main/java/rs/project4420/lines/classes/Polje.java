package rs.project4420.lines.classes;

import android.widget.Button;

import rs.project4420.lines.R;

/**
 * Created by nevena on 14.4.15..
 */
public class Polje {
    int n;
    int m;
    DotItem dot;
    Button b;

    public Polje() {
    }

    public Polje(int n, int m) {
        this.n = n;
        this.m = m;
        dot = new DotItem();
        dot.setColor(R.color.grey);
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

    public DotItem getDot() {
        return dot;
    }

    public void setDot(DotItem dot) {
        this.dot = dot;
    }

    @Override
    public String toString() {
        return ""+n+m;
    }
}

package rs.project4420.lines;

/**
 * Created by nevena on 21.4.15..
 */
public class MatrixItem {
    private int xPrethodno;
    private int yPrethodno;
    private int xTrenutno;
    private int yTrenutno;
    private int value;
    private int koraci;
    private int pomeraj;
    private int obidjen;

    public MatrixItem() {
        this.xTrenutno = -1;
        this.yTrenutno = -1;
        this.koraci = -1;
        this.xPrethodno = -1;
        this.yPrethodno = -1;
        this.pomeraj = -1;
        this.obidjen = 0;
    }

    public MatrixItem(int xTrenutno, int yTrenutno, int value) {
        this.xTrenutno = xTrenutno;
        this.yTrenutno = yTrenutno;
        this.value = value;
        this.koraci = -1;
        this.xPrethodno = -1;
        this.yPrethodno = -1;
        this.pomeraj = -1;
        this.obidjen = 0;
    }

    public int getxPrethodno() {
        return xPrethodno;
    }

    public void setxPrethodno(int xPrethodno) {
        this.xPrethodno = xPrethodno;
    }

    public int getyPrethodno() {
        return yPrethodno;
    }

    public void setyPrethodno(int yPrethodno) {
        this.yPrethodno = yPrethodno;
    }

    public int getxTrenutno() {
        return xTrenutno;
    }

    public void setxTrenutno(int xTrenutno) {
        this.xTrenutno = xTrenutno;
    }

    public int getyTrenutno() {
        return yTrenutno;
    }

    public void setyTrenutno(int yTrenutno) {
        this.yTrenutno = yTrenutno;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKoraci() {
        return koraci;
    }

    public void setKoraci(int koraci) {
        this.koraci = koraci;
    }

    public int getPomeraj() {
        return pomeraj;
    }

    public void setPomeraj(int pomeraj) {
        this.pomeraj = pomeraj;
    }

    public int getObidjen() {
        return obidjen;
    }

    public void setObidjen(int obidjen) {
        this.obidjen = obidjen;
    }

    @Override
    public String toString() {
        return xPrethodno + "" + yPrethodno + " " + xTrenutno + "" + yTrenutno + " " + value+" "+koraci +" (pomeraj: "+pomeraj+")";
        //return value+":"+xTrenutno+""+yTrenutno;
    }
}

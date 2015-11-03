package rs.project4420.lines.connect4;

import android.util.Log;
import rs.project4420.lines.classes.Tree;

public class Connect4AI {
    private static final String TAG = Connect4AI.class.getSimpleName();

    public int next(CoinItem[][] data, int turn) {

        /*Tree root = new Tree("a");
        root.setData(copyData(data));
        long st = System.currentTimeMillis();
        createTree(root, turn);
        Log.d(TAG, "Alpha-beta value: " + alphaBetaAlgorithm(root, 1) + ", column: " + root.getAbTree());
        long et = System.currentTimeMillis();
        long tt = et-st;
        Log.d(TAG, "Time: "+tt);*/
        //return root.getAbTree();

        Tree root2 = new Tree("a");
        root2.setData(copyData(data));
        long st2 = System.currentTimeMillis();
        newCreateTree(root2, turn);
        Log.d(TAG, "Alpha-beta value2: " + alphaBetaAlgorithm(root2, 1) + ", column: " + root2.getAbTree());
        long et2 = System.currentTimeMillis();
        long tt2 = et2-st2;
        Log.d(TAG, "Time: "+tt2);


        return root2.getAbTree();
    }

    /*public Tree createTree(Tree parent, int turn){
        int maxDubina = 6;
        int znak;

        if (parent.getDubina() < maxDubina) { //izlazak iz rekurzije
            for (int i = 0; i < 7; i++) {
                CoinItem[][] copy = copyData(parent.getData());
                CoinItem[][] newCopy = addNode(copy, i, -1*turn);
                Tree node = new Tree();
                node.setParent(parent);
                node.setDubina(parent.getDubina() + 1);
                node.setColumn(i);
                if (node.getDubina() == maxDubina) node.setAbTree(node.getColumn());
                if (newCopy != null) {
                    node.setData(newCopy);
                    if (node.getDubina()%2 == 0) znak = 1; else znak = -1;
                    //node.setValue(NodeScoring.vratiSumu(node.getData(), node.getColumn(), -1*turn, znak)); // ni ova nije tako losa :)
                    node.setValue(znak*Evaluation.evalFunction(node.getData(), node.getColumn(), -1*turn, znak));
                    parent.addTreeChild(node);
                    if (Math.abs(node.getValue()) != (int)Double.POSITIVE_INFINITY)
                        createTree(node, -1*turn);  //rekurzivni poziv
                    else {
                        parent.setAbTree(node.getColumn());
                    }
                }
            }
            parent.setData(null);
        }
        else return null;

        return parent;
    }*/

    public Tree newCreateTree(Tree parent, int turn){
        int maxDubina = 6;
        int znak;
        if (parent.getDubina() < maxDubina){
            for (int i = 0; i < 7; i++) {
                if (parent.getAlpha() > parent.getBeta()) break;

                CoinItem[][] copy = copyData(parent.getData());
                CoinItem[][] newCopy = addNode(copy, i, -1*turn);

                Tree node = new Tree();
                node.setParent(parent);
                node.setAlpha(parent.getAlpha());
                node.setBeta(parent.getBeta());
                node.setDubina(parent.getDubina() + 1);
                node.setColumn(i);

                if (node.getDubina() == maxDubina || parent.getAbTree() == -1) node.setAbTree(i);
                if (node.getDubina()%2 == 0) znak = 1; else znak = -1;

                if (newCopy != null) {
                    node.setData(newCopy);
                    node.setValue(znak*Evaluation.evalFunction(node.getData(), node.getColumn(), -1*turn, znak));
                    parent.addTreeChild(node);

                    if (Math.abs(node.getValue()) != (int)Double.POSITIVE_INFINITY)
                        newCreateTree(node, -1*turn);  //rekurzivni poziv
                    else if (node.getValue() == (int)Double.POSITIVE_INFINITY){
                        parent.setAbTree(node.getColumn());
                    }

                    if (znak == 1){
                        if (node.getValue() > parent.getAlpha()) parent.setAlpha(node.getValue());
                        if (node.getValue() > parent.getValue()) {
                            parent.setValue(node.getValue());
                            parent.setAbTree(node.getColumn());
                        }
                    }

                    if (znak == -1){
                        if (node.getValue() < parent.getBeta()) parent.setBeta(node.getValue());
                        if (node.getValue() < parent.getValue()) {
                            parent.setValue(node.getValue());
                            parent.setAbTree(node.getColumn());
                        }
                    }
                }
            }
            parent.setData(null);
        }
        else return null;

        return parent;
    }

    public int miniMax(Tree parent, int param){
        for(Tree aChildren : parent.getChildren()) {
            miniMax(aChildren, param * (-1));
            if (param == 1 && aChildren.getValue() > parent.getValue()) parent.setValue(aChildren.getValue());
            else if (param == -1 && aChildren.getValue() < parent.getValue()) parent.setValue(aChildren.getValue());
        }
        return parent.getValue();
    }

    public int alphaBetaAlgorithm(Tree parent, int param){
        for (Tree aChildren : parent.getChildren()) {
            aChildren.setAlpha(parent.getAlpha());
            aChildren.setBeta(parent.getBeta());
            int alphaBeta = alphaBetaAlgorithm(aChildren, -param);
            if (parent.getAbTree() == -1) {
                parent.setAbTree(aChildren.getColumn());
            }
            if (!parent.getChildren().isEmpty()) {
                if (param == -1) {
                    if (alphaBeta < parent.getBeta()) parent.setBeta(alphaBeta);
                    if (alphaBeta < parent.getValue()) {
                        parent.setValue(aChildren.getValue());
                        parent.setAbTree(aChildren.getColumn());
                    }
                } else {
                    if (alphaBeta > parent.getAlpha()) parent.setAlpha(alphaBeta);
                    if (alphaBeta > parent.getValue()) {
                        parent.setValue(aChildren.getValue());
                        parent.setAbTree(aChildren.getColumn());
                    }
                }
                if (parent.getAlpha() > parent.getBeta()) break;
            }
        }
        return parent.getValue();
    }

    public CoinItem[][] addNode(CoinItem[][] data, int col, int turn){
        for (int i = 5; i >= 0; i--) {
            if (data[i][col].getCoinOwner() == Constants.COIN_OWNER_GRID) {
                data[i][col].setCoinOwner(turn);
                return data;
            }
        }
        return null;
    }

    public static CoinItem[][] copyData(CoinItem[][] data){
        CoinItem[][] copy = new CoinItem[6][7];
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                copy[x][y] = new CoinItem();
                copy[x][y].setCoinOwner(data[x][y].getCoinOwner());
            }
        }
        return copy;
    }
}

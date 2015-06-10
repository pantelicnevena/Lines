package rs.project4420.lines.classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import rs.project4420.lines.R;

/**
 * Created by nevena on 10.6.15..
 */
public class Matrix {
    private static final String TAG = "Matrix";
    DotItem item;
    DotItem[][] mMatrix = new DotItem[7][7];

    public Matrix(DotItem[][] matrix) {
        mMatrix = matrix;
    }

    public Matrix() {
    }

    public JSONObject toJSON (DotItem[][] matrix){
        JSONObject json = new JSONObject();
        mMatrix = matrix;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                int position= i*7+j;
                try {
                    json.put(""+position, mMatrix[i][j].getColor());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, ""+json);
        return json;
    }

    public DotItem[][] fromJSON(String jsonString){
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 49; i++) {
            if (obj.has(""+i)) try {
                DotItem di = new DotItem();
                di.setColor(obj.getInt("" + i));
                mMatrix[i/7][i%7] = di;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        printMatrix(mMatrix);

        return mMatrix;
    }

    public void printMatrix(DotItem[][] matrix){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Log.d(TAG, ""+matrix[i][j].getColor());
            }
        }
    }

    public static int[][] napraviKopiju (DotItem[][] matrix){
        int[][] kopija = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (matrix[i][j].getColor() == R.color.grey)
                    kopija[i][j] = 0;
                else kopija[i][j] = 1;
            }
        }
        return kopija;
    };

    public static MatrixItem[][] napraviKopijuPolja (DotItem[][] matrix){
        MatrixItem[][] kopija = new MatrixItem[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (matrix[i][j].getColor() == R.color.grey)
                    kopija[i][j] = new MatrixItem(i, j, 0);
                else kopija[i][j] = new MatrixItem(i, j, -1);
            }
        }
        return kopija;
    };

}

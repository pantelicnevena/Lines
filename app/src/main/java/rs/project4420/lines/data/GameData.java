package rs.project4420.lines.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.Matrix;

/**
 * Created by enco on 1.6.15..
 */
public class GameData {

    private static final String TAG = "GameData";
    public String data = "";
    public int turnCounter;
    public DotItem[][] matrix;
    public int score1;
    public int score2;

    public GameData() {
    }

    public GameData(byte[] data) {

    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        Matrix m = new Matrix();
        JSONObject jsonMatrix = m.toJSON(matrix);

        try {
            retVal.put("data", data);
            retVal.put("turnCounter", turnCounter);
            retVal.put("matrix", jsonMatrix);
            retVal.put("pl_1", score1);
            retVal.put("pl_2", score2);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of GameData.
    static public GameData unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new GameData();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        GameData retVal = new GameData();

        try {
            JSONObject obj = new JSONObject(st);

            if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }
            if (obj.has("matrix")) {
                String s = obj.getString("matrix");
                Matrix m = new Matrix();
                retVal.matrix = m.fromJSON(s);
            }
            if (obj.has("pl_1")){
                retVal.score1 = obj.getInt("pl_1");
            }
            if (obj.has("pl_2")){
                retVal.score2 = obj.getInt("pl_2");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }
}

package rs.project4420.lines.logic;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.project4420.lines.Adapter;
import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;
import rs.project4420.lines.classes.MatrixItem;
import rs.project4420.lines.classes.Polje;
import rs.project4420.lines.R;
import rs.project4420.lines.data.GameData;

/**
 * Created by nevena on 8.6.15..
 */
public class MultiplayerLogic {

    private static final String TAG = "MultiplayerLogic";

    /**
     *
     * @return lista svih mogucih boja za kuglice
     */
    public static List<Integer> returnColors(){
        List<Integer> colors = new ArrayList();
        Random rnd = new Random();
        List<Integer> pozicija = new ArrayList();
        colors = new ArrayList<>();
        colors.add(R.color.blue);
        colors.add(R.color.red);
        colors.add(R.color.purple);
        colors.add(R.color.yellow);
        colors.add(R.color.orange);
        colors.add(R.color.light_blue);
        colors.add(R.color.green);
        colors.add(R.color.grey);
        return colors;
    }

    public static DotItem[][] setMatrixColors(DotItem[][] matrix){
        Random rnd = new Random();
        List<Integer> colors = returnColors() ;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrix[i][j] = new DotItem(R.color.grey);
            }
        }
        for (int i = 0; i < 15; i++) {
            matrix[rnd.nextInt(7)][rnd.nextInt(7)].setColor(colors.get(rnd.nextInt(7)));
        }
        //TODO ne sme da dodje do ponavljanja pozicija na koja se ubacuju obojena polja

        return matrix;
    }

    /**
     *
     * @param matrix - matrica izgenerisanog polja tackica u prvom potezu
     * @param resources
     * @return vraca jason objekat sa podacima o x, y, color o svakoj tackici iz matrice
     */
    public static JSONObject returnMatrixJSON(DotItem[][] matrix, Resources resources, String points){
        List<Integer> colorArray = new ArrayList();
        JSONObject object = null;
        JSONArray json = new JSONArray();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                try {
                    object = new JSONObject();
                    object.put("x", String.valueOf(i));
                    object.put("y", String.valueOf(j));
                    object.put("color", matrix[i][j].getColor());
                    json.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Dots", json);
            Log.d(TAG, ""+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public static DotItem[][] returnMatrix (GameData gameData){
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json = new JSONObject(gameData.data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            array = json.getJSONArray("Dots");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Integer> colors = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject o = (JSONObject) array.get(i);
                colors.add(Integer.valueOf(o.optString("color")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        DotItem[][] matrix = new DotItem[7][7];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                DotItem item = new DotItem();
                item.setColor(colors.get(i * 7 + j));
                matrix[i][j] = item;
            }
        }
        return matrix;
    }


    public static void loadIcons(AsyncTask dit, TurnBasedMatch mMatch, GoogleApiClient mGoogleApiClient, ImageView playerIcon1, ImageView playerIcon2) {

        // Player 1
        if (Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl() != null){
            dit = new DownloadImageTask(playerIcon1)
                    .execute(Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl().toString());
        } else {
            dit = new DownloadImageTask(playerIcon1)
                    .execute("https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
        }
        (playerIcon1).invalidate();

        if (mMatch == null){
            dit = new DownloadImageTask(playerIcon2)
                    .execute("https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
            (playerIcon2).invalidate();
            return; }

        //Player 2
        if (mMatch.getParticipants().get(1).getIconImageUrl() != null) {
            dit = new DownloadImageTask(playerIcon2)
                    .execute(mMatch.getParticipants().get(1).getIconImageUrl().toString());
        }
        else {
            dit = new DownloadImageTask(playerIcon2)
                    .execute("https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
        }
        (playerIcon2).invalidate();
    }


    public static Polje returnNextColor(final DotItem[][] m) {
        Random rnd = new Random();

        List<Integer> colors = returnColors();
        int boja = colors.get(rnd.nextInt(7));
        DotItem dot = new DotItem();
        dot.setColor(boja);

        Polje p = new Polje();
        p.setDot(dot);

        return p;
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

    /**
     *
     * @param matrix
     * @return listaPraznihPolja sluzi za generisanje nove kuglice
     */
    public static List<Polje> vratiListuPraznihPolja(DotItem[][] matrix){
        List<Polje> praznaPolja = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (matrix[i][j].getColor() == R.color.grey) praznaPolja.add(new Polje(i,j));
            }
        }
//        Log.d(TAG, "Lista praznih polja: "+praznaPolja);
        return praznaPolja;
    };


    public static void tranzicija(final DotItem[][]matrix, int position, List<MatrixItem> put, final GridView gv){
        //animacija puta
        final List<ValueAnimator> animList = new ArrayList<>();
        for (int i = 0; i <put.size() ; i++) {

            ValueAnimator va = ValueAnimator.ofFloat(0, 1);
            animList.add(va);
            va.setDuration(300);
            va.setRepeatCount(2);
            final DotView tackica = (DotView) gv.getChildAt(put.get(i).getxTrenutno()*7+put.get(i).getyTrenutno());
            final int color = matrix[position/7][position%7].getColor();
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedFraction();
                    if (value<=1 && value>0.8) tackica.setColor(R.color.grey);
                    if (value<=0.8 && value>=0.2) tackica.setColor(color);
                    if (value<=0 && value>0.2) tackica.setColor(R.color.grey);
                    tackica.invalidate();
                }
            });
            va.start();

            //zaustavljanje animacije puta
            final List<MatrixItem> finalPutanja1 = put;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < finalPutanja1.size() ; i++) {
                        DotView tackica = (DotView) gv.getChildAt(finalPutanja1.get(i).getxTrenutno()*7+ finalPutanja1.get(i).getyTrenutno());
                        animList.get(i).end();
                        tackica.clearAnimation();
                        tackica.setColor(R.color.grey);
                        tackica.invalidate();

                    }
                }
            }, 350); //pauza 350ms
        }
    }

    /**
     *
     * @param m matrica trenutnog stanja
     * @param polje polje na kome ce se pojaviti sledeca kuglica
     * @return trenutno stanje matrice nakon ubacenog novog polja
     * i nakon povere da li postoji neko ponistavanje kuglica [osvojeni poeni]
     */
    static DotItem[][] matrix;
    public static DotItem[][] ubaciNoviDot(final DotItem[][] m, final Polje polje, final Adapter adapter, final View scoreBar, final TextView score) {
        Random rnd = new Random();
        List<Polje> praznaPolja = vratiListuPraznihPolja(m);
        int praznoPolje = rnd.nextInt(praznaPolja.size());
        final Polje p = polje;
        p.setN(praznaPolja.get(praznoPolje).getN());
        p.setM(praznaPolja.get(praznoPolje).getM());

        m[p.getN()][p.getM()].setColor(p.getDot().getColor());
        matrix = m;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                matrix = LineSuccess.ponistiNizove(p.getN(), p.getM(), matrix, score, scoreBar);

                adapter.notifyDataSetChanged();
            }
        }, 300);


        return matrix;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public static String getNextParticipantId(TurnBasedMatch mMatch, GoogleApiClient mGoogleApiClient) {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }
}

package rs.project4420.lines.logic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.project4420.lines.Adapter;
import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;
import rs.project4420.lines.classes.Matrix;
import rs.project4420.lines.classes.MatrixItem;
import rs.project4420.lines.classes.Polje;
import rs.project4420.lines.R;
import rs.project4420.lines.data.GameData;

/**
 * Created by nevena on 8.6.15..
 */
public class GameLogic {

    private static final String TAG = "MultiplayerLogic";

    /**
     *
     * @return lista svih mogucih boja za kuglice
     */
    public static List<Integer> returnColors(){
        List<Integer> colors = new ArrayList();
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

    /**
     *
     * @param matrix
     * @return matrica sa random postavljenim kuglicama
     */
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
     * @param dit - asinhroni task
     * @param mMatch - mec koji se igra
     * @param mGoogleApiClient
     * @param playerIcon1
     * @param playerIcon2
     * postavlja sliku ikonice igraca na sliku sa google+ naloga igraca
     */
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

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String player = mMatch.getParticipantId(playerId);

        //Player 2
        String url = null;
        if (player.equals("p_1")) { url = mMatch.getParticipants().get(1).getIconImageUrl(); }
        else if (player.equals("p_2")){ url = mMatch.getParticipants().get(0).getIconImageUrl(); }

        if (url != null){ dit = new DownloadImageTask(playerIcon2).execute(url.toString()); }
        else { dit = new DownloadImageTask(playerIcon2).execute(
                "https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
        }

        (playerIcon2).invalidate();
    }

    /**
     *
     * @param mGoogleApiClient
     * @param iv
     * @param dit
     */
    public static void loadSinglePlayerIcon(GoogleApiClient mGoogleApiClient, ImageView iv, AsyncTask dit){

        if (Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl() != null){
            String url = Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl().toString();
            dit = new DownloadImageTask(iv).execute(url);
        } else {
            dit = new DownloadImageTask(iv).execute("https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
        }

    }

    /**
     *
     * @param m
     * @return objekat polje koji sadrzi boju koja
     * ce se sledeca pojaviti prilikom pravljenja novog poteza
     */
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

    /**
     *
     * @param matrix
     * @param position - indeks pocetne pozicije u matrici
     * @param put - put kojim se kuglica krece da bi stigla od pocetne do krajnje pozicije
     * @param gv - matrica polja
     * postavljanje animacije za svako polje koje se nalazi u listi PUT (lista predjenih
     * polja od pocetne do krajnje pozicije)
     */
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
     * @param mMatch
     * @param mGoogleApiClient
     * @param gameData
     * @param score1
     * @param score2
     * postavlja
     */
    public static void showScoreUpdate(TurnBasedMatch mMatch, GoogleApiClient mGoogleApiClient, GameData gameData, TextView score1, TextView score2){
        if (mMatch.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)).equals("p_1")){
            score1.setText(String.valueOf(gameData.score1));
            score2.setText(String.valueOf(gameData.score2));}
        else if (mMatch.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)).equals("p_2")){
            score1.setText(String.valueOf(gameData.score2));
            score2.setText(String.valueOf(gameData.score1));}
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
        if (praznaPolja.size() == 0) { return matrix; }
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


    /**
     *
     * @param lastColor
     * @param lastSelected
     * @param position
     * @param matrix
     */
    public static void moveDot(int lastColor, int lastSelected, int position, DotItem[][] matrix){
        lastColor = matrix[lastSelected / 7][lastSelected % 7].getColor();
        matrix[lastSelected / 7][lastSelected % 7].setColor(R.color.grey);
        matrix[position / 7][position % 7].setColor(lastColor);
    }

    public static void vibrate (Vibrator vibe){
        vibe.vibrate(40);
    }

    /**
     *
     * @param mMatch
     * @param mGoogleApiClient
     * @param gameData
     * @param score1
     * @param score2
     */
    public static void ParticipantTurn(TurnBasedMatch mMatch, GoogleApiClient mGoogleApiClient, GameData gameData, TextView score1, TextView score2){
        if (gameData == null){
            gameData = new GameData();
        }

        Matrix m = new Matrix();
        gameData.data = m.toJSON(matrix).toString();
        gameData.turnCounter++;
        gameData.matrix = matrix;
        if (mMatch.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)).equals("p_1")){
            gameData.score1 = Integer.valueOf(score1.getText().toString());
            gameData.score2 = Integer.valueOf(score2.getText().toString());
        } else if (mMatch.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)).equals("p_2")){
            gameData.score2 = Integer.valueOf(score1.getText().toString());
            gameData.score1 = Integer.valueOf(score2.getText().toString());
        }


    }

    public static void setBorder(){

    }

}

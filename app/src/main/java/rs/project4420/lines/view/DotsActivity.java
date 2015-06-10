package rs.project4420.lines.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.project4420.lines.Adapter;
import rs.project4420.lines.R;
import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;
import rs.project4420.lines.classes.Matrix;
import rs.project4420.lines.classes.MatrixItem;
import rs.project4420.lines.classes.Polje;
import rs.project4420.lines.logic.DownloadImageTask;
import rs.project4420.lines.logic.LineSuccess;
import rs.project4420.lines.logic.GameLogic;
import rs.project4420.lines.solver.aStar;


public class DotsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DotsActivity";
    Adapter adapter;
    Random rnd;
    GridView table;
    GridView gridView;

    List<Integer> colors;

    DotItem[][] matrix = new DotItem[7][7];
    int[][] matrixCopy;
    MatrixItem[][] matrixCopyItem;
    boolean pronadjenCilj;
    List<MatrixItem> putanja;
    List<MatrixItem> put;
    List<ValueAnimator> listaVA;
    Polje next;

    ValueAnimator animator;
    int lastSelected = -1;
    private GoogleApiClient mGoogleApiClient;
    private AsyncTask<String, Void, Bitmap> dit;

    public DotsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Toolbar
        getSupportActionBar().hide();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);

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

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrix[i][j] = new DotItem(R.color.grey);
            }
        }
        for (int i = 0; i < 15; i++) {
            matrix[rnd.nextInt(7)][rnd.nextInt(7)].setColor(colors.get(rnd.nextInt(7)));
        }
        //TODO ne sme da dodje do ponavljanja pozicija na koja se ubacuju obojena polja

        gridView = (GridView)findViewById(R.id.table);
        adapter = new Adapter(this, matrix);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);

        //postavljanje sledece nove kuglice
        next = GameLogic.returnNextColor(matrix);
        View nextView = (View) findViewById(R.id.next_dot);
        nextView.setBackgroundResource(R.drawable.next_dot);
        GradientDrawable gd = (GradientDrawable) nextView.getBackground();
        gd.setColor(getResources().getColor(next.getDot().getColor()));


        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.d(TAG, "position:" + position);

        //stopiraj prethodnu
        if(animator != null && animator.isRunning()){
            animator.end();
            view.clearAnimation();
            animator = null;
        }
        //kliknuto drugo obojeno polje
        if(lastSelected != position){
            if (matrix[position/7][position%7].getColor() == R.color.grey){
                //ako prethodno nije kliknuto na obojeno dugme
                if (lastSelected == -1) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(40);
                } else {
                    pronadjenCilj = false;
                    matrixCopy = Matrix.napraviKopiju(matrix);
                    matrixCopyItem = Matrix.napraviKopijuPolja(matrix);
                    List<MatrixItem> putanja = new ArrayList<>();
                    int xCilj = position/7;
                    int yCilj = position%7;

                    aStar astar = new aStar();
                    putanja = astar.aZvezda(matrixCopyItem, (lastSelected / 7), (lastSelected % 7), (xCilj), (yCilj));

                    if (putanja != null) {
                        final List<MatrixItem> finalPutanja = putanja;
                        int lastColor = matrix[lastSelected / 7][lastSelected % 7].getColor();
                        matrix[lastSelected / 7][lastSelected % 7].setColor(R.color.grey);
                        matrix[position / 7][position % 7].setColor(lastColor);

                        GameLogic.tranzicija(matrix, position, putanja, gridView);
                        lastSelected = -1;

                        final View scoreBar = (View) findViewById(R.id.score_bar);
                        final TextView tv = (TextView)findViewById(R.id.score);

                        matrix = LineSuccess.ponistiNizove(xCilj, yCilj, matrix, tv, scoreBar);


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                GameLogic.ubaciNoviDot(matrix, next, adapter, scoreBar, tv);
                                next = GameLogic.returnNextColor(matrix);
                                View nextView = (View) findViewById(R.id.next_dot);
                                nextView.setBackgroundResource(R.drawable.next_dot);
                                GradientDrawable gd = (GradientDrawable) nextView.getBackground();
                                gd.setColor(getResources().getColor(next.getDot().getColor()));

                                adapter.notifyDataSetChanged();
                            }
                        }, 370);

                    }else { //ako ne moze da stigne do cilja
                        lastSelected = -1;
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(40);
                        Log.d(TAG, "NE MOZE DA SE DODJE DO CILJA!!!!!!!!!!!!!!!");
                    };

                    adapter.notifyDataSetChanged();
                }
            } else { //pokretanje animacije kliknutog dugmeta
                animator = ValueAnimator.ofFloat(0, (float) Math.PI);
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);

                final DotView dotView = ((DotView)view);
                final float origRadius = dotView.radius;

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        dotView.radius = origRadius - (float)(Math.abs(Math.sin(value)) * origRadius * .35f);
                        dotView.invalidate();
                    }
                });
                lastSelected = position;
                animator.start();
            }

        } else {  //kliknuto isto obojeno polje
            lastSelected = -1;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");

        ImageView iv = (ImageView) findViewById(R.id.player_image);


        if (Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl() != null){
            String url = Games.Players.getCurrentPlayer(mGoogleApiClient).getIconImageUrl().toString();
            dit = new DownloadImageTask(iv).execute(url);
        } else {
            dit = new DownloadImageTask(iv).execute("https://lh3.googleusercontent.com/-9x24WfH1Ri8/AAAAAAAAAAI/AAAAAAAAAAA/zhHK3nMbRXs/s120-c/photo.jpg");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}

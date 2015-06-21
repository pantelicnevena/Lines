package rs.project4420.lines.view;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
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

import rs.project4420.lines.Adapter;
import rs.project4420.lines.R;
import rs.project4420.lines.classes.DotItem;
import rs.project4420.lines.classes.DotView;
import rs.project4420.lines.classes.Matrix;
import rs.project4420.lines.classes.MatrixItem;
import rs.project4420.lines.classes.Polje;
import rs.project4420.lines.logic.LineSuccess;
import rs.project4420.lines.logic.GameLogic;
import rs.project4420.lines.solver.aStar;


public class DotsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DotsActivity";
    DotItem[][] matrix = new DotItem[7][7];
    int lastSelected = -1;

    Adapter adapter;
    GridView table;
    GridView gridView;
    Resources resources;
    View scoreBar;
    TextView tv;
    ImageView iv;
    View nextView;
    View nextView2;
    ValueAnimator animator;
    Polje next;
    Polje next2;
    Vibrator vibe;

    MatrixItem[][] matrixCopyItem;
    boolean pronadjenCilj;
    private GoogleApiClient mGoogleApiClient;
    private AsyncTask<String, Void, Bitmap> dit;
    Context context;

    public DotsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Toolbar
        getSupportActionBar().hide();
        context = this;
        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);
        scoreBar = (View) findViewById(R.id.score_bar);
        tv = (TextView)findViewById(R.id.score);
        iv = (ImageView) findViewById(R.id.player_image);
        resources = getResources();
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        nextView = (View) findViewById(R.id.next_dot);
        nextView2 = (View) findViewById(R.id.next_dot_second);
        nextView.setBackgroundResource(R.drawable.next_dot);
        nextView2.setBackgroundResource(R.drawable.next_dot);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        matrix = GameLogic.setMatrixColors(matrix);

        gridView = (GridView)findViewById(R.id.table);
        adapter = new Adapter(this, matrix);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);

        //postavljanje sledece nove kuglice
        next = GameLogic.returnNextColor(matrix);
        next2 = GameLogic.returnNextColor(matrix);

        GradientDrawable gd = (GradientDrawable) nextView.getBackground();
        GradientDrawable gd2 = (GradientDrawable) nextView2.getBackground();
        gd.setColor(getResources().getColor(next.getDot().getColor()));
        gd2.setColor(getResources().getColor(next2.getDot().getColor()));

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    GameLogic.vibrate(vibe);
                } else {
                    pronadjenCilj = false;
                    matrixCopyItem = Matrix.napraviKopijuPolja(matrix);
                    List<MatrixItem> putanja = new ArrayList<>();
                    int xCilj = position/7;
                    int yCilj = position%7;

                    aStar astar = new aStar();
                    putanja = astar.aZvezda(matrixCopyItem, (lastSelected / 7), (lastSelected % 7), (xCilj), (yCilj));

                    if (putanja != null) {
                        final List<MatrixItem> finalPutanja = putanja;
                        int lastColor = 0;

                        GameLogic.moveDot(lastColor, lastSelected, position, matrix);
                        GameLogic.tranzicija(matrix, position, putanja, gridView);

                        lastSelected = -1;
                        matrix = LineSuccess.ponistiNizove(xCilj, yCilj, matrix, tv, scoreBar);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GameLogic.ubaciNoviDot(matrix, next, adapter, scoreBar, tv);
                                GameLogic.ubaciNoviDot(matrix, next2, adapter, scoreBar, tv);
                                if (((List<Polje>)GameLogic.vratiListuPraznihPolja(matrix)).size() == 0)
                                    new AlertDialog.Builder(context)
                                            .setTitle("Kraj igre")
                                            .setMessage("Igra je završena.\nVaš rezultat je: "+tv.getText().toString()).show();
                                next = GameLogic.returnNextColor(matrix);
                                next2 = GameLogic.returnNextColor(matrix);
                                GradientDrawable gd = (GradientDrawable) nextView.getBackground();
                                GradientDrawable gd2 = (GradientDrawable) nextView2.getBackground();
                                gd.setColor(resources.getColor(next.getDot().getColor()));
                                gd2.setColor(resources.getColor(next2.getDot().getColor()));

                                adapter.notifyDataSetChanged();
                            }
                        }, 370);

                    }else { //ako ne moze da stigne do cilja
                        lastSelected = -1;
                        GameLogic.vibrate(vibe);
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
        GameLogic.loadSinglePlayerIcon(mGoogleApiClient, iv, dit);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}

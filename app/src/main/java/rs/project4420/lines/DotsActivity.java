package rs.project4420.lines;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class DotsActivity extends ActionBarActivity {

    private static final String TAG = "DotsActivity";

    Random rnd;
    GridView table;
    DotAdapter.Item selectedView;

    public DotsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        selectedView = null;
        super.onCreate(savedInstanceState);
        rnd = new Random();

        setContentView(R.layout.activity_dots);
        table = (GridView) findViewById(R.id.table);
        List<rs.project4420.lines.DotView> list = new ArrayList<>();

        GridView gridView = (GridView)findViewById(R.id.table);
        final DotAdapter adapter = new DotAdapter(this);
        gridView.setAdapter(adapter);

        //*** MATRICA ***//

        //Inicijalizacija
        int[][] polja = new int[6][6];
        boolean pronadjenCilj = false;
        List<Polje> zid = new ArrayList<>();

        //Kreiranje matrice
        for (int j = 0; j < 36; j++) {
            Button polje = (Button) gridView.getItemAtPosition(j);
            int n = j/6;
            int m = j%6;
            if (polje.getDrawingCacheBackgroundColor() == 2131230754){
                polja[n][m] = 0;
            } else {
                polja[n][m] = 1;
                zid.add(new Polje(n, m, polje));
            }
        }

        //Stampanje matrice
        for (int j = 0; j < 6; j++) {
            List lista = new ArrayList();
            for (int k = 0; k < 6; k++) {
                lista.add(polja[j][k]);
            }
            Log.d(TAG, j+": "+lista);
        }

        //Postavljanje kopije matrice
        int [][] kopija = polja;

    }

}

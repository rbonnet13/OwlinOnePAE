package owlinone.pae.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;

/**
 * Created by AnthonyCOPPIN on 13/12/2016.
 */

public class SplashScreen extends Activity {
    private String TAG = SplashScreen.class.getSimpleName();
    String merci = null;
    TextView textMerci = null;
    ImageView imgView = null;
    ImageView imgViewHibou = null;
    ImageView imghibou_cherche = null;
    ImageView imgBulle = null;
    long timeInMilliseconds = 0;

    Intent i = getIntent();
    // Splash screen timer
    private static int TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);


        textMerci             = (TextView) findViewById(R.id.merci);
        imgView               = (ImageView) findViewById(R.id.imgOwlDepart);
        imgViewHibou          = (ImageView) findViewById(R.id.imgOwlEcole);
        imghibou_cherche      = (ImageView) findViewById(R.id.hibou_cherche);
        imgBulle              = (ImageView) findViewById(R.id.imgBulle);
        Intent intent         = getIntent();
        final String activity = intent.getStringExtra("activity");
        textMerci .setVisibility(View.INVISIBLE);
        imgViewHibou .setVisibility(View.INVISIBLE);
        imgBulle .setVisibility(View.INVISIBLE);

        imghibou_cherche.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                imgView .setVisibility(View.INVISIBLE);
                imgBulle .setVisibility(View.VISIBLE);
            }
        });

        if("first".equals(activity))
        {
            imgView .setVisibility(View.INVISIBLE);
            textMerci .setVisibility(View.VISIBLE);
            imgViewHibou .setVisibility(View.VISIBLE);
            imghibou_cherche.setVisibility(View.INVISIBLE);
            imgBulle .setVisibility(View.INVISIBLE);
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if("first".equals(activity)) //Si on a ajouté un appartement dans la liste----------
                {
                    Intent i = new Intent(SplashScreen.this, Appartement.class);
                    startActivity(i);
                    finish();
                }
                else{                        //Si on vient de démarrer l'application----------------
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, TIME_OUT); //Temps d'affichage de cette page
    }

}
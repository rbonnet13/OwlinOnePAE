package owlinone.pae.article;

/**
 * Created by AnthonyCOPPIN on 18/12/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import owlinone.pae.R;

/**
 * Created by rudybonnet on 06/12/2016.
 */

public class DetailArticle extends AppCompatActivity {
    String strTitre;
    String strImage;
    String strCategorie;
    String strCorps;
    String strDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar20);
        setSupportActionBar(toolbar);

        //Affichage de la flèche de retour-----------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView textCategorie = (TextView) findViewById(R.id.categorie_detail);
        TextView textCorps     = (TextView) findViewById(R.id.corps_complet_detail);
        TextView textTitre     = (TextView) findViewById(R.id.titre_detail_article);
        TextView textDate      = (TextView) findViewById(R.id.date_detail);
        ImageView imgDetail    = (ImageView) findViewById(R.id.imgDetailArticle);
        TextView textTheme = (TextView) findViewById(R.id.categorie_detail);


        //Récupérer la largeur de l'écran
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        //Récupère le string strCategorie pour la catégorie de l'article----------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strCategorie = null;
            } else {
                strCategorie = extras.getString("strCategorie");
            }
        } else {
            strCategorie = (String) savedInstanceState.getSerializable("strCategorie");
        }

        //Récupère le string strTitre pour le titre de l'article------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strTitre = null;
            } else {
                strTitre = extras.getString("strTitre");
            }
        } else {
            strTitre = (String) savedInstanceState.getSerializable("strTitre");
        }

        //Récupère le string strImage pour l'image de l'article-------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strImage = null;
            } else {
                strImage = extras.getString("strImage");
            }
        } else {
            strImage = (String) savedInstanceState.getSerializable("strImage");
        }

        //Récupère le string strCorps pour le corps de l'article------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strCorps = null;
            } else {
                strCorps = extras.getString("strCorps");
            }
        } else {
            strCorps = (String) savedInstanceState.getSerializable("strCorps");
        }

        //Récupère le string strDate pour la date de l'article--------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strDate = null;
            } else {
                strDate = extras.getString("strDate");
            }
        } else {
            strDate = (String) savedInstanceState.getSerializable("strDate");
        }

        textTheme.setText(strCategorie);

        //Chargement de l'image de l'article--------------------------------------------------------
        //Récupère et décode les images en Base64 depuis la BDD
        String base64 = strImage.substring(strImage.indexOf(","));
        byte[] decodedBase64 = Base64.decode(base64, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
        imgDetail.setImageBitmap(image);


        /*Picasso.with(this)
                .load(strImage)
                .resize(width,600)
                .into(imgDetail);*/

        //Affichage dans les TextView avec les string précèdent-------------------------------------
        textCategorie.setText(strCategorie);
        textCorps.setText(strCorps);
        textTitre.setText(strTitre);
        textDate.setText(strDate);
    }
}
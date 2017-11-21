package owlinone.pae;

/**
 * Created by AnthonyCOPPIN on 18/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import owlinone.pae.R;
import com.squareup.picasso.Picasso;

/**
 * Created by rudybonnet on 06/12/2016.
 */

public class DetailArticle extends Activity {
    String strTitre;
    String strImage;
    String strCategorie;
    String strCorps;
    String strDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        TextView textCategorie = (TextView) findViewById(R.id.categorie_detail);
        TextView textCorps     = (TextView) findViewById(R.id.corps_complet_detail);
        TextView textTitre     = (TextView) findViewById(R.id.titre_detail_article);
        TextView textDate      = (TextView) findViewById(R.id.date_detail);
        ImageView imgDetail    = (ImageView) findViewById(R.id.imgDetailArticle);

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

        //Chargement de l'image de l'article--------------------------------------------------------
        Picasso.with(this)
                .load(strImage)
                .resize(width,600)
                .into(imgDetail);

        //Affichage dans les TextView avec les string précèdent-------------------------------------
        textCategorie.setText(strCategorie);
        textCorps.setText(strCorps);
        textTitre.setText(strTitre);
        textDate.setText(strDate);
    }
}
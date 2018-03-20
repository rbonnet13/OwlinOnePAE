package owlinone.pae.appartement;

/**
 * Created by Julian on 09/12/2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import owlinone.pae.R;

public class DetailAppart extends AppCompatActivity implements OnMapReadyCallback{
    String strDetail= "";
    String strDetailAppart = "";
    String strDetailTel= "";
    String strNomPropDetail= "";
    Double doubleLongitude= 0.0;
    Double doubleLatitude= 0.0;
    String strMail= "";
    String strAdresse= "";
    String strVille= "";
    String strImagePrinc= "";
    String strImageSecond= "";
    String strPrix= "";
    String strDispoContext= "";
    SupportMapFragment supportMapFragment = new SupportMapFragment();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailappart);

        supportMapFragment = (SupportMapFragment) this
                .getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        // ajoute la fonction toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Appartement.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        TextView textDetail = (TextView) findViewById(R.id.detail);
        ImageButton textTel = (ImageButton) findViewById(R.id.detail_tel);
        TextView textNomPropDetail = (TextView) findViewById(R.id.nom_prop_detail);
        ImageButton textMail = (ImageButton) findViewById(R.id.detail_mail);
        TextView textDetailAppart = (TextView) findViewById(R.id.detail_appart);
        TextView textDetailPrix = (TextView) findViewById(R.id.detail_prix);
        TextView textDetailAdresse = (TextView) findViewById(R.id.detail_adresse);
        TextView textDispo = (TextView) findViewById(R.id.detail_dispo);
        TextView textePhoto = (TextView) findViewById(R.id.id_photo);
        ImageView imagePrincipale = (ImageView) findViewById(R.id.image_principale_detail);
        ImageView imageSecondaire = (ImageView) findViewById(R.id.image_secondaire_detail);

        //Récupère le string srtDetail pour les détails--------------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strDetail = null;
            } else {
                strDetail = extras.getString("strDetail");
            }
        } else {
            strDetail = (String) savedInstanceState.getSerializable("strDetail");
        }

        //Récupère le string srtDetailTel pour le telephone-----------------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strDetailTel = null;
            } else {
                strDetailTel = extras.getString("strDetailTel");
            }
        } else {
            strDetailTel = (String) savedInstanceState.getSerializable("strDetailTel");
        }

        //Récupère le string strNomPropDetail pour le nom du propriétaire---------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strNomPropDetail = null;
            } else {
                strNomPropDetail = extras.getString("strNomPropDetail");
            }
        } else {
            strNomPropDetail = (String) savedInstanceState.getSerializable("strNomPropDetail");
        }

        //Récupère le string strLongitude pour la longitude de l'adresse---------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                doubleLongitude = null;
            } else {
                doubleLongitude = extras.getDouble("strLongitude");
            }
        } else {
            doubleLongitude = (Double) savedInstanceState.getSerializable("strLongitude");
        }

        //Récupère le string strLongitude pour la latitude de l'adresse---------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                doubleLatitude = null;
            } else {
                doubleLatitude = extras.getDouble("strLatitude");
            }
        } else {
            doubleLatitude = (Double) savedInstanceState.getSerializable("strLatitude");
        }

        //Récupère le string strMail pour le mail du propriétaire-----------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strMail = null;
            } else {
                strMail = extras.getString("strMail");
            }
        } else {
            strMail = (String) savedInstanceState.getSerializable("strMail");
        }

        //Récupère le string strAdresse pour l'adresse de l'appartement-----------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strAdresse = null;
            } else {
                strAdresse = extras.getString("strAdresse");
            }
        } else {
            strAdresse = (String) savedInstanceState.getSerializable("strAdresse");
        }

        //Récupère le string strVille pour la ville de l'appartement--------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strVille = null;
            } else {
                strVille = extras.getString("strVille");
            }
        } else {
            strVille = (String) savedInstanceState.getSerializable("strVille");
        }

        //Récupère le string strVille pour le prix de l'appartement---------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strPrix= null;
            } else {
                strPrix = extras.getString("strPrix");
            }
        } else {
            strPrix= (String) savedInstanceState.getSerializable("strPrix");
        }

        //Récupère le string strDispoContext pour la disponibilité----------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strDispoContext = null;
            } else {
                strDispoContext = extras.getString("strDispoContext");
            }
        } else {
            strDispoContext = (String) savedInstanceState.getSerializable("strDispoContext");
        }

        //Récupère le string strDispoContext pour le détail (T2 etc..)----------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strDetailAppart = null;
            } else {
                strDetailAppart = extras.getString("strDetailAppart");
            }
        } else {
            strDetailAppart = (String) savedInstanceState.getSerializable("strDetailAppart");
        }

        //Récupère le string photo principale----------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strImagePrinc = null;
            } else {
                strImagePrinc = extras.getString("strImagePrinc");
            }
        } else {
            strImagePrinc = (String) savedInstanceState.getSerializable("strImagePrinc");
        }

        //Récupère le string seconde photo ----------------------------------
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                strImageSecond = null;
            } else {
                strImageSecond = extras.getString("strImageSecond");
            }
        } else {
            strImageSecond = (String) savedInstanceState.getSerializable("strImageSecond");
        }

        // dans la toolbar
        textDetailAppart.setText(strDetailAppart);
        // sous le google map
        textDetailPrix.setText(strPrix + "€ CC/mois");
        textNomPropDetail.setText(strNomPropDetail);
        textDetailAdresse.setText(strAdresse.trim() + "\n" + strVille + "\n");
        // description appart
        textDetail.setText(strDetail.trim() + "\n");
        textDispo.setText(strDispoContext + "\n");

        if (strImageSecond != null){
            textePhoto.setText("Photos");
        }else{
            textePhoto.setText("Photo");
        }

        if(strImagePrinc != null){
            String base64Princ = strImagePrinc.substring(strImagePrinc.indexOf(","));
            byte[] decodedBase64Second = Base64.decode(base64Princ, Base64.DEFAULT);
            Bitmap imagePrinc = BitmapFactory.decodeByteArray(decodedBase64Second, 0, decodedBase64Second.length);
            imagePrincipale.setImageBitmap(imagePrinc);
        }

        if(strImagePrinc != null){
            String base64Second = strImageSecond.substring(strImageSecond.indexOf(","));
            byte[] decodedBase64Princ = Base64.decode(base64Second, Base64.DEFAULT);
            Bitmap imageSecond = BitmapFactory.decodeByteArray(decodedBase64Princ, 0, decodedBase64Princ.length);
            imageSecondaire.setImageBitmap(imageSecond);
        }

        //click pour appeler au téléphone
        textTel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + strDetailTel;
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });

        // si le mail est vide on affiche pas le bouton
        if(strMail.equals("")){
            textMail.setVisibility(View.INVISIBLE);
        }else{
            textMail.setVisibility(View.VISIBLE);
        }

        //click pour envoyer un mail
        textMail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + strMail);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    //méthode pour démarrer google map avec la position de 2 markers avec l'animation de départ
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_esaip))
                .position(new LatLng(47.464051, -0.497334)));
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_home))
                .position(new LatLng( doubleLatitude,doubleLongitude
                )));
        LatLng position = new LatLng(47.46848551035859, -0.5252838134765625);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 12);
        map.animateCamera(yourLocation);
    }

    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Appartement.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
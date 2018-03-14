package owlinone.pae.covoiturage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.calendrier.CalendarExtra;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.divers.APropos;
import owlinone.pae.divers.Bug;
import owlinone.pae.main.MainActivity;
import owlinone.pae.session.Compte;
import owlinone.pae.session.Session;

public class Covoiturage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Déclaration des variables
    Session session;
    private  String adresse ="" ;
    private  String ville ="" ;
    String email, name, photoBDD, nbNotif;
    private TextView notifcovoit;
    Button mDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

        // Affiche le contenu de l'activté sélectionnée
       setContentView(R.layout.activity_covoiturage);

        // Affiche la toolbar correspondant à l'activité affichée
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Active le drawer dans l'activité affichée
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Met en surbrillance dans le drawer l'activité affichée
        navigationView.setCheckedItem(R.id.nav_covoiturage);

        // User Session Manager
        session = new Session(getApplicationContext());
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        name = user.get(Session.KEY_NAME);
        // get email
        email = user.get(Session.KEY_EMAIL);
        // get base 64 photo code from BDD
        photoBDD = user.get(Session.KEY_PHOTO);
        // get ville
        ville = user.get(Session.KEY_VILLE);
        // get adresse
        adresse = user.get(Session.KEY_ADRESSE);

        notifcovoit = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_covoiturage));
        new DataNotifConducteur().execute();

        // Show user data on activity
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = (ImageView)header.findViewById(R.id.image_menu);

        // Récupère et décode les images en Base64 depuis la BDD pour le header du drawer
        if(!photoBDD.equals("no image")){
            try {
                String base64 = photoBDD.substring(photoBDD.indexOf(","));
                byte[] decodedBase64 = Base64.decode(base64, Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
                photo.setImageBitmap(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mDriver = (Button) findViewById(R.id.conducteur);
       final Button mCustomer = (Button) findViewById(R.id.client);

       mDriver.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (adresse == null && ville == null)
               {
                   Toast.makeText(getApplicationContext(), R.string.saisirCoordonneeCovoiturage, Toast.LENGTH_LONG).show();
                   Intent intent = new Intent(Covoiturage.this, Compte.class);
                   startActivity(intent);
               }
               else{
                   Intent intent = new Intent(Covoiturage.this, ConducteurTab.class);
                   intent.putExtra("nbNotif", nbNotif);
                   startActivity(intent);
               }
               finish();
               return;
           }
       });

       mCustomer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (adresse == null && ville == null)
               {
                   Toast.makeText(getApplicationContext(), R.string.saisirCoordonneeCovoiturage, Toast.LENGTH_LONG).show();
                   Intent intent = new Intent(Covoiturage.this, Compte.class);
                   startActivity(intent);
               }
               else{
                   Intent intent = new Intent(Covoiturage.this, Client.class);
                   intent.putExtra("nbNotif", nbNotif);
                   startActivity(intent);
               }
               finish();
               return;
           }
       });
   }
    private class DataNotifConducteur extends AsyncTask<String, Void, String> {
        Exception exception;
        protected String doInBackground(String... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();

                String urlNotification = AddressUrl.strNbNotif;
                parametersConducteur.put("name",name);

                String jsonresult = sh.performPostCall(urlNotification, parametersConducteur);
                return jsonresult;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if (result.equals("") || result == null) {
                //Toast.makeText(MainActivity.this, "Problème de connexion au serveur", Toast.LENGTH_LONG).show();
                return;
            }

            int jsonResult = MainActivity.returnParsedJsonObject(result);
            if (jsonResult == 0) {
                //Toast.makeText(MainActivity.this, "Le pseudo ou l'email est déjà utilisé", Toast.LENGTH_LONG).show();
                return;
            }
            nbNotif = Integer.toString(jsonResult);

            notifcovoit.setGravity(Gravity.CENTER_VERTICAL);
            notifcovoit.setTypeface(null, Typeface.BOLD);
            notifcovoit.setTextColor(getResources().getColor(R.color.colorRed));
            notifcovoit.setText(nbNotif);

        }
    }
    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {
        // Check si le drawer est ouvert. Si oui, on le ferme
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else { // Sinon on va à l'activité Articles
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    // Ouverture d'une activité en cas de clic dans le drawer
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_deconnexion){
            session.logoutUser();
            finish();
        } else if (id == R.id.nav_compte) {
            Intent searchIntent = new Intent(getApplicationContext(), Compte.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_article) {
            Intent searchIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_appartement) {
            Intent searchIntent = new Intent(getApplicationContext(), Appartement.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_calendrier) {
            Intent searchIntent = new Intent(getApplicationContext(), CalendarExtra.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_bug) {
            Intent searchIntent = new Intent(getApplicationContext(), Bug.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_a_propos) {
            Intent searchIntent = new Intent(getApplicationContext(), APropos.class);
            startActivity(searchIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Animation de fermeture du drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}


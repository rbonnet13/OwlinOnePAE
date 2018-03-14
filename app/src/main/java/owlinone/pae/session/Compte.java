package owlinone.pae.session;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.calendrier.CalendarExtra;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HideKeyboard;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.covoiturage.Covoiturage;
import owlinone.pae.divers.APropos;
import owlinone.pae.divers.Bug;
import owlinone.pae.main.MainActivity;

/**
 * Created by rudyb on 17/01/2018.
 */

public class Compte extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Déclaration des variables
    Session session;
    private TextView user_username;
    private TextView user_email;
    private EditText user_tel;
    private EditText user_nom;
    private EditText user_prenom;
    private EditText user_ville;
    private EditText user_adresse ;
    private EditText user_cp;
    private CheckBox user_covoiturage;
    Context context;
    private String enteredNom;
    private String enteredPrenom ;
    private String enteredPrenomEnvoi ;
    private String enteredVilleEnvoi;
    private String enteredNomEnvoi;
    private String enteredAdressEnvoi;
    private String enteredAdress ;
    private String enteredVille;
    private String enteredTel;
    private String enteredCP;
    private TextView notifcovoit;
    private String usernameEnvoi;

    private  String username ="";
    private  String usernameAffichage ="";
    private  String password ="";
    private  String email ="";
    private  String prenomAffichage ="";
    private  String nomAffichage ="";
    private  String adresseAffichage ="" ;
    private  String villeAffichage ="" ;
    private  String prenom ="";
    private  String nom ="";
    private  String adresse ="" ;
    private  String ville ="" ;
    private  String cp  ="";
    private  String telephone  ="";
    private  String photoBDD ="";
    private  String covoiturage ="";
    private  String response ="";
    private  String nbNotif ="";

    HttpHandler sh = new HttpHandler();
    Geocoder geocoder;
    Address addressName = new Address(Locale.FRANCE);
    double latitude = 0.0;
    double longitude = 0.0;
    private final String serverUrl = AddressUrl.strTriIndexCompte;


    private RatingBar mRatingBar;List<Address> addresses = new List<Address>() {
        @Override
        public int size() {
            return 0;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }
        @Override
        public boolean contains(Object o) {
            return false;
        }
        @NonNull
        @Override
        public Iterator<Address> iterator() {
            return null;
        }
        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }
        @NonNull
        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }
        @Override
        public boolean add(Address address) {
            return false;
        }
        @Override
        public boolean remove(Object o) {
            return false;
        }
        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean addAll(Collection<? extends Address> collection) {
            return false;
        }
        @Override
        public boolean addAll(int i, Collection<? extends Address> collection) {
            return false;
        }
        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }
        @Override
        public void clear() {
        }
        @Override
        public Address get(int i) {
            return null;
        }
        @Override
        public Address set(int i, Address address) {
            return null;
        }
        @Override
        public void add(int i, Address address) {
        }
        @Override
        public Address remove(int i) {
            return null;
        }
        @Override
        public int indexOf(Object o) {
            return 0;
        }
        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }
        @Override
        public ListIterator<Address> listIterator() {
            return null;
        }
        @NonNull
        @Override
        public ListIterator<Address> listIterator(int i) {
            return null;
        }
        @NonNull
        @Override
        public List<Address> subList(int i, int i1) {
            return null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le contenu de l'activté sélectionnée
        setContentView(R.layout.activity_compte);
        context = getBaseContext();
        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.setupUI(findViewById(R.id.layout_compte));

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
        navigationView.setCheckedItem(R.id.nav_compte);
        // User Session Manager
        session = new Session(getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();

        // Récupération des données utilisateur
        username = user.get(Session.KEY_NAME);
        usernameEnvoi = username.replace("'","''");
        password = user.get(Session.KEY_PASSWORD);
        email = user.get(Session.KEY_EMAIL);
        prenom = user.get(Session.KEY_PRENOM);
        nom = user.get(Session.KEY_NOM);
        ville = user.get(Session.KEY_VILLE);
        adresse = user.get(Session.KEY_ADRESSE);
        cp = user.get(Session.KEY_CP);
        photoBDD = user.get(Session.KEY_PHOTO);
        telephone = user.get(Session.KEY_TEL);
        covoiturage = user.get(Session.KEY_COVOITURAGE);

        user_username = (TextView) findViewById(R.id.user_field);
        user_email = (TextView) findViewById(R.id.user_email);
        user_tel = (EditText) findViewById(R.id.user_telephone);
        user_nom = (EditText) findViewById(R.id.nom_user);
        user_prenom = (EditText) findViewById(R.id.prenom_user);
        user_adresse = (EditText) findViewById(R.id.adresse_user);
        user_ville = (EditText) findViewById(R.id.ville_user);
        user_cp= (EditText) findViewById(R.id.code_postal);
        user_covoiturage = (CheckBox) findViewById(R.id.checkBox);
        Button SaveButton = (Button) findViewById(R.id.save_user);

        user_username.setText(usernameAffichage);
        user_email.setText(email);
        user_nom.setText(nomAffichage);
        user_prenom.setText(prenomAffichage);
        user_ville.setText(villeAffichage);
        user_adresse.setText(adresseAffichage);
        user_cp.setText(cp);
        user_tel.setText(telephone);
        user_covoiturage.setChecked(Boolean.valueOf(covoiturage));

        notifcovoit = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_covoiturage));
        new DataNotifConducteur().execute();

        // Affiche les données utilisateur dans le header du drawer
        View header = (navigationView).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + username);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = header.findViewById(R.id.image_menu);

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

        SaveButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                String country = "FRANCE";
                enteredNom = user_nom.getText().toString();
                enteredNomEnvoi = enteredNom.replace("'","''");
                enteredPrenom = user_prenom.getText().toString();
                enteredPrenomEnvoi = enteredPrenom.replace("'","''");
                enteredVille = user_ville.getText().toString();
                enteredVilleEnvoi = enteredVille.replace("'","''");
                enteredAdress = user_adresse.getText().toString();
                enteredAdressEnvoi = enteredAdress.replace("'","''");
                enteredCP = user_cp.getText().toString();
                enteredTel = user_tel.getText().toString();
                if (user_covoiturage.isChecked()) {
                    covoiturage ="true";
                }else
                    covoiturage ="false";

                final Context context = getApplicationContext();
                String strFinalAdresse = enteredAdress + "," + enteredVille + " "+ enteredCP
                        + ", "+ country;
                geocoder = new Geocoder(context, Locale.getDefault());
                try {
                        addresses = geocoder.getFromLocationName(strFinalAdresse, 1);
                        Log.e("adresse:", String.valueOf(addresses));
                        if(addresses != null && addresses.size() > 0){
                            addressName = addresses.get(0);
                        }

                        Log.e("adresse:", String.valueOf(addressName.getLongitude()));

                        if(addressName.getLongitude() != 0.0)
                        {
                            longitude = addressName.getLongitude();
                            latitude = addressName.getLatitude();
                        }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if ( enteredNom.length() <= 1 || enteredPrenom.length() <= 1 || enteredVille.length() <= 1 || enteredAdress.length() <= 1 || enteredCP.length() != 5 || enteredTel.length() != 10) {
                    Toast.makeText(Compte.this, R.string.remplirToutChamps, Toast.LENGTH_LONG).show();
                    return;
                }
                new Compte.AsyncDataClass().execute();
            }

        });
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
        } else if (id == R.id.nav_article) {
            Intent searchIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_appartement) {
            Intent searchIntent = new Intent(getApplicationContext(), Appartement.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_covoiturage) {
            Intent searchIntent = new Intent(getApplicationContext(), Covoiturage.class);
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

    private class AsyncDataClass extends AsyncTask<Void, Void, Void> {
        Exception exception;
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                Log.e("username:", username);
                Log.e("enteredNomEnvoi:", enteredNomEnvoi);
                Log.e("enteredPrenomEnvoi:", enteredPrenomEnvoi);
                Log.e("enteredVilleEnvoi:", enteredVilleEnvoi);
                Log.e("enteredAdressEnvoi:", enteredAdressEnvoi);

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("username", usernameEnvoi);
                parameters.put("nom", enteredNomEnvoi);
                parameters.put("prenom", enteredPrenomEnvoi);
                parameters.put("telephone", enteredTel);
                parameters.put("ville", enteredVilleEnvoi);
                parameters.put("adresse", enteredAdressEnvoi);
                parameters.put("latitude", String.valueOf(latitude));
                parameters.put("longitude", String.valueOf(longitude));
                parameters.put("cp", enteredCP);
                parameters.put("covoiturage", covoiturage);
                response = sh.performPostCall(serverUrl, parameters);
                return null;
            } catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + response);
            if (response == null) {
                Toast.makeText(Compte.this, R.string.problemeReseau, Toast.LENGTH_LONG).show();
                return;
            }

            int jsonResult = sh.returnParsedJsonObject(response);
            if(jsonResult == 0){
                Toast.makeText(Compte.this, R.string.problemeReseau, Toast.LENGTH_LONG).show();
                return;
            }

            if (jsonResult == 1) {
                session.createUserLoginSession(username,enteredPrenom,enteredNom,enteredVille,enteredAdress,String.valueOf(latitude) ,String.valueOf(longitude),enteredCP,email,enteredTel,password,photoBDD,covoiturage);
                Intent intent = new Intent(Compte.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    private class DataNotifConducteur extends AsyncTask<String, Void, String> {
        Exception exception;
        protected String doInBackground(String... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();

                String urlNotification = AddressUrl.strNbNotif;
                parametersConducteur.put("name",username);

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
}

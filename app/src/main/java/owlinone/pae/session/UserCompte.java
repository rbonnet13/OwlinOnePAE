package owlinone.pae.session;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.main.MainActivity;

/**
 * Created by rudyb on 17/01/2018.
 */

public class UserCompte extends AppCompatActivity {


    private TextView user_username;
    private TextView user_email;
    private EditText user_tel;
    private EditText user_nom;
    private EditText user_prenom;
    private EditText user_ville;
    private EditText user_adresse ;
    private EditText user_cp;
    private String enteredNom;
    private String enteredPrenom ;
    private String enteredAdress ;
    private String enteredVille;
    private String enteredTel;
    private String enteredCP ;
    private  String username ="";
    private  String password ="";
    private  String email ="";
    private  String prenom ="";
    private  String nom ="";
    private  String adresse ="" ;
    private  String ville ="" ;
    private  String cp  ="";
    private  String tel  ="";
    private  String photo ="";
    private  String response ="";
    HttpHandler sh = new HttpHandler();
    Geocoder geocoder;
    Address addressName = new Address(Locale.FRANCE);
    double latitude = 0.0;
    double longitude = 0.0;
    private final String serverUrl = AddressUrl.strTriIndexCompte;
    Session session;
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
        setContentView(R.layout.compte);
        session = new Session(getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        // get name
          username = user.get(Session.KEY_NAME);
          password = user.get(Session.KEY_PASSWORD);
          email = user.get(Session.KEY_EMAIL);
          prenom = user.get(Session.KEY_PRENOM);
          nom = user.get(Session.KEY_NOM);
          ville = user.get(Session.KEY_VILLE);
          adresse = user.get(Session.KEY_ADRESSE);
          cp = user.get(Session.KEY_CP);
          photo = user.get(Session.KEY_PHOTO);
          tel = user.get(Session.KEY_TEL);

        user_username = (TextView) findViewById(R.id.user_field);
        user_email = (TextView) findViewById(R.id.user_email);
        user_tel = (EditText) findViewById(R.id.user_telephone);
        user_nom = (EditText) findViewById(R.id.nom_user);
        user_prenom = (EditText) findViewById(R.id.prenom_user);
        user_adresse = (EditText) findViewById(R.id.adresse_user);
        user_ville = (EditText) findViewById(R.id.ville_user);
        user_cp= (EditText) findViewById(R.id.code_postal);
        Button SaveButton = (Button) findViewById(R.id.save_user);
        user_username.setText(username);
        user_email.setText(email);
        user_nom.setText(nom);
        user_prenom.setText(prenom);
        user_ville.setText(ville);
        user_adresse.setText(adresse);
        user_cp.setText(cp);
        user_tel.setText(tel);
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar50);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCompte.this, MainActivity.class);
                startActivity(intent);            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                String country = "FRANCE";
                enteredNom = user_nom.getText().toString();
                enteredPrenom = user_prenom.getText().toString();
                enteredVille = user_ville.getText().toString();
                enteredAdress = user_adresse.getText().toString();
                enteredCP = user_cp.getText().toString();
                enteredTel = user_tel.getText().toString();
                final Context context = getApplicationContext();
                String strFinalAdresse = enteredAdress + "," + enteredCP + " "+ enteredVille
                        + ", "+ country;
                geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    for (int i=0; i<10;i++)
                    {
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
                            i=10;
                        }
                        i++;
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if ( enteredNom.length() <= 1 || enteredPrenom.length() <= 1 || enteredVille.length() <= 1 || enteredAdress.length() <= 1 || enteredCP.length() != 5 || enteredTel.length() != 10) {
                    Toast.makeText(UserCompte.this, "Remplir tout les champs SVP", Toast.LENGTH_LONG).show();
                    return;
                }
                new UserCompte.AsyncDataClass().execute();
            }

        });
    }
    @Override
    public void onBackPressed(){

    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionsettings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private class AsyncDataClass extends AsyncTask<Void, Void, Void> {
        Exception exception;
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("username", username);
                parameters.put("nom", enteredNom);
                parameters.put("prenom", enteredPrenom);
                parameters.put("password", password);
                parameters.put("email", email);
                parameters.put("telephone", enteredTel);
                parameters.put("ville", enteredVille);
                parameters.put("adresse", enteredAdress);
                parameters.put("latitude", String.valueOf(latitude));
                parameters.put("longitude", String.valueOf(longitude));
                parameters.put("cp", enteredCP);
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
                Toast.makeText(UserCompte.this, "Problème de connexion au serveur", Toast.LENGTH_LONG).show();
                return;
            }

            int jsonResult = sh.returnParsedJsonObject(response);
            if(jsonResult == 0){
                Toast.makeText(UserCompte.this, "erreur", Toast.LENGTH_LONG).show();
                return;
            }

            if (jsonResult == 1) {
                session.createUserLoginSession(username,enteredPrenom,enteredNom,enteredVille,enteredAdress,String.valueOf(latitude) ,String.valueOf(longitude),enteredCP,email,enteredTel,password,photo);
                Intent intent = new Intent(UserCompte.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}

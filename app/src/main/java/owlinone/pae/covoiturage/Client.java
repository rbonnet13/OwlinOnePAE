package owlinone.pae.covoiturage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.CovoitViewCircle;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.main.MainActivity;
import owlinone.pae.session.Session;

import static android.view.View.VISIBLE;

/**
 * Created by rudyb on 15/01/2018.
 */

public class Client extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener{
    Session session;
    private  String username ="";
    private  String password ="";
    private  String email ="";
    private  String prenom ="";
    private  String nom ="";
    private  String adresse ="" ;
    private  String ville ="" ;
    private  String cp  ="";
    private  String latit ="";
    private  String longit ="";
    private  String telephone ="";
    private  String geolat ="0";
    private  String geolong ="0";
    private  String IdConducteur ="";
    private  String IdConducteurtest ="";
    private  String Usernametest ="";


    Boolean test = false;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();

    private String TAG = Appartement.class.getSimpleName();
    String strUsernameConducteur = "",nbNotif="", strNom = "", strPrenom = "", strAdresse = "", strTelephone = "", strDestination = "";
    ArrayList<LinkedHashMap<String, String>> covoitList;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Geocoder geocoder;
    Address addressName = new Address(Locale.FRANCE);
    double latitude = 0.0;
    double longitude = 0.0;
    private ProgressDialog dialog;
    private Button  mRequest;
    private ToggleButton toggle;
    Handler handler;
    private SupportMapFragment mapFragment;

    List<Address> addresses = new List<Address>() {
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
    private LatLng destinationLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientmap);

        // Récupération de l'utilisateur
        session = new Session(getApplicationContext());
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        username = user.get(Session.KEY_NAME);
        password = user.get(Session.KEY_PASSWORD);
        email = user.get(Session.KEY_EMAIL);
        prenom = user.get(Session.KEY_PRENOM);
        nom = user.get(Session.KEY_NOM);
        ville = user.get(Session.KEY_VILLE);
        adresse = user.get(Session.KEY_ADRESSE);
        cp = user.get(Session.KEY_CP);
        latit = user.get(Session.KEY_LATITUDE);
        longit = user.get(Session.KEY_LONGITUDE);
        telephone = user.get(Session.KEY_TEL);
        Log.e("adresse:", adresse);

        // Récupération du context de l'activité
        final Context context = getApplicationContext();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Récupération de la lontitude et de la latitude de l'addresse finale
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            String fulladresse = adresse +" "+ ville ;
            addresses = geocoder.getFromLocationName(fulladresse, 1);
            Log.e("adresse:", String.valueOf(addresses));
            if(addresses != null && addresses.size() > 0){
                addressName = addresses.get(0);
                Log.e("adresse:", String.valueOf(addressName.getLongitude()));

            }else{
                Toast.makeText(getApplicationContext(),
                        "Erreur de localisation veuillez vous déconnecter",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            if(addressName.getLongitude() != 0.0)
            {
                longitude = addressName.getLongitude();
                latitude = addressName.getLatitude();
            }else{
                Toast.makeText(getApplicationContext(),
                        "Erreur de localisation veuillez vous déconnecter",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        destinationLatLng = new LatLng(0.0,0.0);

        mapFragment.getMapAsync(this);

        // Récupération du nbNotif de l'activité précédente
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                nbNotif = null;
            } else {
                nbNotif = extras.getString("nbNotif");
            }
        } else {
            nbNotif = (String) savedInstanceState.getSerializable("nbNotif");
        }

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar16);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Covoiturage.class);
                intent.putExtra("nbNotif", nbNotif);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        toggle  = (ToggleButton) findViewById(R.id.toggleDestination);
        // After some action
        if (toggle.isChecked()) {
            // The toggle is enabled
            strDestination ="home";
        } else {
            // The toggle is disabled
            strDestination ="school";
        }

        //Test si première connexion pour afficher bulle information bouton
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.premiereConnexionClient), false);
        if(!previouslyStarted) {

            final CovoitViewCircle fadeBackground = findViewById(R.id.fadeBackground);
            fadeBackground.bringToFront();
            fadeBackground.setVisibility(VISIBLE);
            fadeBackground.animate().alpha(0.5f);
            new AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setIcon(R.drawable.owl_in_one_logo)
                    .setMessage("Vous devez choisir la destination en cliquant sur le bouton en surbrillance")
                    .setPositiveButton("J'ai compris", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            // After some action
                            if (toggle.isChecked()) {
                                // The toggle is enabled
                                strDestination ="home";
                            } else {
                                // The toggle is disabled
                                strDestination ="school";
                            }
                            fadeBackground.setVisibility(View.GONE);
                        }
                    }).create().show();
            //On change la valeur dans le cache en true
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.premiereConnexionClient), Boolean.TRUE);
            edit.commit();


            //Animation du Toggle button
            toggle.setScaleY(0.7f);
            toggle.setScaleX(0.7f);

                final AnimatorSet animatorSetHeart = new AnimatorSet();

                ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(toggle, "scaleY", 1f, 0.7f);
                imgScaleUpYAnim.setDuration(300);
                imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(toggle, "scaleX", 1f, 0.7f);
                imgScaleUpXAnim.setDuration(300);
                imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

                ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(toggle, "scaleY", 0.7f, 1f);
                imgScaleDownYAnim.setDuration(300);
                imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(toggle, "scaleX", 0.7f, 1f);
                imgScaleDownXAnim.setDuration(300);
                imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator imgScaleUpYAnim2 = ObjectAnimator.ofFloat(toggle, "scaleY", 1.1f, 0.7f);
                imgScaleUpYAnim2.setDuration(300);
                imgScaleUpYAnim2.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator imgScaleUpXAnim2 = ObjectAnimator.ofFloat(toggle, "scaleX", 1.1f, 0.7f);
                imgScaleUpXAnim2.setDuration(300);
                imgScaleUpXAnim2.setInterpolator(DECCELERATE_INTERPOLATOR);

                animatorSetHeart.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);
                animatorSetHeart.play(imgScaleUpYAnim2).with(imgScaleUpXAnim2);
                animatorSetHeart.start();

                animatorSetHeart.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        // Si l'imge toggle est clické
                        toggle.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                test = true;
                            }
                        });
                        // Si pas clické le bouton reste animé
                        if(test == false) {
                            animatorSetHeart.start();
                        }
                        // On enlève la view avec le cercle et on sort
                        else {
                            fadeBackground.setVisibility(View.GONE);

                            // After some action
                            if (toggle.isChecked()) {
                                // The toggle is enabled
                                strDestination ="home";
                            } else {
                                // The toggle is disabled
                                strDestination ="school";
                            }
                            return;
                        }
                    }
                });
            }

            mRequest = (Button) findViewById(R.id.request);
            mRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            covoitList = new ArrayList<>();
            new Client.AsyncDataClass().execute();
            LatLng position = new LatLng(Double.valueOf(latit),Double.valueOf(longit));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 15);
            mMap.animateCamera(yourLocation);
            }
        });
    }

    //Click sur un marker
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        InfoWindowData info = (InfoWindowData) marker.getTag();
        Log.e(TAG, "Marker info: " + info.getTel());

        sendIntent.setData(Uri.parse("sms:" + info.getTel()));
        startActivity(sendIntent);

    }

    //Click long sur un marker
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        InfoWindowData info = (InfoWindowData) marker.getTag();
        Uri data = Uri.parse("mailto:" + info.getMail());
        intent.setData(data);
        startActivity(intent);
    }

    private class AsyncDataClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        public double convertRad(double input){
            return (Math.PI * input)/180;
        }

        public double Distance(double lat_a_degre, double lon_a_degre, double lat_b_degre, double lon_b_degre){

            int R = 6378137 ; //Rayon de la terre en mètre

            double lat_a = convertRad(lat_a_degre);
            double lon_a = convertRad(lon_a_degre);
            double lat_b = convertRad(lat_b_degre);
            double lon_b = convertRad(lon_b_degre);
            double dlo = (lon_b - lon_a)/2 ;
            double dla = (lat_b - lat_a)/2 ;
            double a = (Math.sin(dla) * Math.sin(dla)) + Math.cos(lat_a) * Math.cos(lat_b) * (Math.sin(dlo) * Math.sin(dlo));
            double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return (R * d);
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
        HttpHandler sh = new HttpHandler();
        String jsonStr = null;

        jsonStr = sh.makeServiceCall(AddressUrl.strTriIndexGPS);

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(jsonStr);
                // Boucle sur tout les appartements
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject a            = jsonArray.getJSONObject(i);
                    int id_covoit           = a.getInt("id");
                    String username         = a.getString("username");
                    String prenom           = a.getString("PRENOM");
                    String nom              = a.getString("NOM");
                    String adresseMail      = a.getString("email");
                    String telephone        = a.getString("TELEPHONE");
                    String ville            = a.getString("VILLE");
                    String adresse          = a.getString("ADRESSE");
                    String CP               = a.getString("CP");
                    Double latitudeAppart   = a.getDouble("LATITUDE");
                    Double longitudeAppart  = a.getDouble("LONGITUDE");

                    double result_covoiturage =  Distance(latitude,longitude,latitudeAppart,longitudeAppart);

                    // Affiche les appartements que s'il est disponible ou non disponible
                    if(!adresseMail.equals(email))
                    {
                        if (result_covoiturage <= 10000.0 ){
                            LinkedHashMap<String, String> covoit = new LinkedHashMap<>();
                            // adding each child node to HashMap key => value
                            covoit.put("id", String.valueOf(id_covoit));
                            covoit.put("username", username);
                            covoit.put("PRENOM", prenom);
                            covoit.put("NOM", nom);
                            covoit.put("email", adresseMail);
                            covoit.put("TELEPHONE", telephone);
                            covoit.put("VILLE", ville);
                            covoit.put("ADRESSE", adresse);
                            covoit.put("CP", CP);
                            covoit.put("LATITUDE", String.valueOf(latitudeAppart));
                            covoit.put("LONGITUDE", String.valueOf(longitudeAppart));
                            // adding contact to contact list
                            covoitList.add(covoit);
                        }
                    }
                }
            } catch (final JSONException e)
            {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else
        {
            Log.e(TAG, "Problème de chargement");
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getApplicationContext(),
                            "OUPS!! Essayer plus tard!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            String getNom ="" ;
            String getPrenom ="" ;
            String getEmail="" ;
            String getAdresse ="" ;
            String getTelephone ="" ;

            super.onPostExecute(result);
            List<Marker> markers = new ArrayList<Marker>();
            for (LinkedHashMap<String, String> map : covoitList){
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue();
                    if (key == "LATITUDE") {
                        geolat = value;
                    }
                    if (key == "LONGITUDE") {
                        geolong = value;
                    }
                    if (key == "id") {
                        IdConducteur = value;
                    }
                    if (key == "NOM") {
                        getNom = value;
                    }
                    if (key == "PRENOM") {
                        getPrenom = value;
                    }
                    if (key == "email") {
                        getEmail = value;
                    }
                    if (key == "ADRESSE") {
                        getAdresse = value;
                    }
                    if (key == "TELEPHONE") {
                        getTelephone = value;
                    }
                }
                //On créé un nouveau marqueur associé à un étudiant
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_conducteur))
                        .title(IdConducteur)
                        .snippet(getPrenom + " " + getNom)
                        .position(new LatLng( Double.valueOf(geolat),Double.valueOf(geolong)
                        )));
                InfoWindowData info = new InfoWindowData();

                info.setMail(getEmail);
                info.setTel(getTelephone);
                info.setAdresse(getAdresse);
                marker.setTag(info);
                markers.add(marker);
            }
            // On a fini le chargement
            dialog.dismiss();

            markers.size();

            if(mMap != null) {
                for (int i = 0; i < markers.size(); i++) {
                    Marker mark = markers.get(i);
                }
            }
        }

    }
    //Click sur un marker
    public boolean onMarkerClick(final Marker marker) {
        final Button notifButton = (Button) findViewById(R.id.btn_notification);
        notifButton.setVisibility(View.VISIBLE);

        // Si on click sur le marker de l'école ou la notre on affiche pas le boutton notification
        if (marker.getTitle().equals("Maison") || marker.getTitle().equals("ESAIP")){
            mRequest.setVisibility(VISIBLE);
            notifButton.setVisibility(View.INVISIBLE);
        }else {

            // Check if a click count was set, then display the click count.
            for (LinkedHashMap<String, String> map : covoitList) {
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue();
                    if(key == "id") {
                        IdConducteurtest = value ;
                    }
                    if(key == "username") {
                        Usernametest = value ;
                    }

                    if (marker.getTitle().isEmpty()){

                    }else{
                        if (marker.getTitle().equals(IdConducteurtest)){

                            strUsernameConducteur = Usernametest;
                            strNom = nom;
                            strPrenom = prenom;
                            strAdresse = adresse;
                            strTelephone = telephone;
                        }
                    }
                }
            }
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        strDestination ="home";
                    } else {
                        // The toggle is disabled
                        strDestination ="school";
                    }
                }
            });

            mRequest.setVisibility(View.INVISIBLE);
            // Si on click sur le bouton notification
            notifButton.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(final View v) {
                    new Client.sendUsers().execute();
                    new Client.sendGCM().execute();

                    Toast.makeText(getApplicationContext(), "Notification envoyée !", Toast.LENGTH_LONG).show();
                    v.setEnabled(false);
                    v.setVisibility(View.INVISIBLE);
                    handler = new Handler();
                    Toast.makeText(getApplicationContext(), "Merci d'attendre 20 secondes avant de pouvoir renvoyer une notification", Toast.LENGTH_LONG).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            v.setVisibility(VISIBLE);
                            v.setEnabled(true);


                        }
                    }, 20000);
                }
            });


        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        // Infos marker Esaip
        Marker markerEsaip = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_esaip))
                .snippet("GROUPE ESAIP")
                .title("ESAIP")
                .position(new LatLng(47.464051, -0.497334)));
        InfoWindowData info = new InfoWindowData();
        info.setMail("contact.esaip@esaip.org");
        info.setTel("02414517475");
        info.setAdresse("3 rue du 8 mai 1945");
        markerEsaip.setTag(info);

        // Infos marker Domicile
        Marker markerAppart = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_home))
                .snippet(" DOMICILE")
                .title("Maison")
                .position(new LatLng( Double.valueOf(latit),Double.valueOf(longit)
                )));
        InfoWindowData info1 = new InfoWindowData();
        info1.setMail(" " + email);
        info1.setTel(" " + telephone);
        info1.setAdresse(" " + adresse);
        markerAppart.setTag(info1);
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);

        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // On enlève le dialog de chargement
        dialog.dismiss();

        LatLng position = new LatLng(47.46848551035859, -0.5252838134765625);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 12);
        map.animateCamera(yourLocation);

    }


    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){
            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Client.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    final int LOCATION_REQUEST_CODE = 1;
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapFragment.getMapAsync(this);


                } else {
                    Toast.makeText(getApplicationContext(), "S'il vous plaît, donnez-nous la permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private class sendUsers extends AsyncTask<Void, Void, Void> {
        Exception exception;
        protected Void doInBackground(Void... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();
                String urlNotification = AddressUrl.strNotifUser;
                parametersConducteur.put("PSEUDO_CONDUCTEUR_NOTIF", strUsernameConducteur);
                parametersConducteur.put("NOM_NOTIF",strNom);
                parametersConducteur.put("PRENOM_NOTIF", strPrenom);
                parametersConducteur.put("ADRESSE_NOTIF",strAdresse );
                parametersConducteur.put("TELEPHONE_NOTIF", strTelephone);
                parametersConducteur.put("DESTINATION_NOTIF", strDestination );
                sh.performPostCall(urlNotification, parametersConducteur);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

    private class sendGCM extends AsyncTask<Void, Void, Void> {
        Exception exception;
        protected Void doInBackground(Void... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();
                String urlNotification = AddressUrl.strIndexGCM;
                parametersConducteur.put("prenom", prenom);
                parametersConducteur.put("nom", nom);
                parametersConducteur.put("DESTINATION_NOTIF", strDestination);
                parametersConducteur.put("PSEUDO_CONDUCTEUR_NOTIF", strUsernameConducteur);
                sh.performPostCall(urlNotification, parametersConducteur);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {
        dialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), Covoiturage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("nbNotif", nbNotif);
        startActivity(intent);
        finish();
    }

}

package owlinone.pae.covoiturage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.main.MainActivity;
import owlinone.pae.session.Session;

/**
 * Created by rudyb on 15/01/2018.
 */

public class Client extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {
    Session session;
    private  String username ="";
    private  String password ="";
    private  String email ="";
    private  String prenom ="";
    private  String nom ="";
    private  String adresse ="" ;
    private  String ville ="" ;
    private  String cp  ="";
    private  String photo ="";
    private  String latit ="";
    private  String longit ="";
    private  String telephone ="";
    private  String geolat ="0";
    private  String geolong ="0";
    private  String IdConducteur ="";
    private  String IdConducteurtest ="";
    private  String Usernametest ="";

    HttpHandler sh = new HttpHandler();
    private String TAG = Appartement.class.getSimpleName();
    String url= null;
    String strUsernameConducteur = "", strNom = "", strPrenom = "", strAdresse = "", strTelephone = "", strDestination = "";

    HashMap <String, String> obj = new HashMap();
    HashMap <String, String> objDispo = new HashMap();
    ArrayList<LinkedHashMap<String, String>> covoitList;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Geocoder geocoder;
    Address addressName = new Address(Locale.FRANCE);
    double latitude = 0.0;
    double longitude = 0.0;
    private final String serverUrl = AddressUrl.strTriIndexGPS;

    SupportMapFragment supportMapFragment = new SupportMapFragment();
    private Button  mRequest;

    private LatLng pickupLocation;

    private Boolean requestBol = false;

    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng;

    private LinearLayout mDriverInfo;

    private ImageView mDriverProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    private RadioGroup mRadioGroup;

    private RatingBar mRatingBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientmap);


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
        photo = user.get(Session.KEY_PHOTO);
        latit = user.get(Session.KEY_LATITUDE);
        longit = user.get(Session.KEY_LONGITUDE);
        telephone = user.get(Session.KEY_TEL);

        final Context context = getApplicationContext();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Récupération de la lontitude et de la latitude de l'addresse finale
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            String fulladresse = adresse + ville ;
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
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        destinationLatLng = new LatLng(0.0,0.0);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Client.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


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
    private class AsyncDataClass extends AsyncTask<Void, Void, Void> {
        // public List<ArrayList<HashMap<String,String>>> Covoite = new ArrayList<ArrayList<HashMap<String,String>>>();

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
                // looping through All Appartements
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
                    String Photo            = a.getString("photo");

                    double result_covoiturage =  Distance(latitude,longitude,latitudeAppart,longitudeAppart);

                    // Affiche les appartements que s'il est disponible ou non disponible
                    if(adresseMail.equals(email))
                    {
                        //on n'ajoute pas l'adresse de l'utilisateur courant
                    }else{
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
                            covoit.put("photo", Photo);
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
            String getPhoto ="" ;

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
                    if (key == "photo") {
                        getPhoto = value;
                    }
                }
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_conducteur))
                        .title(IdConducteur)
                        .snippet("Conducteur :"+getNom+" "+getPrenom)
                        .position(new LatLng( Double.valueOf(geolat),Double.valueOf(geolong)
                        )));
                InfoWindowData info = new InfoWindowData();
                info.setImage(getPhoto);
                info.setHotel("EMAIL : "+getEmail);
                info.setFood("TELEPHONE : "+getTelephone);
                info.setTransport("ADRESSE : "+getAdresse);
                marker.setTag(info);
                markers.add(marker);
            }
            markers.size();

            if(mMap != null) {
                for (int i = 0; i < markers.size(); i++) {
                    Marker mark = markers.get(i);
                }
            }
        }
    }
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getTitle().equals("Maison") || marker.getTitle().equals("ESAIP")){
            mRequest.setVisibility(View.VISIBLE);
            Button notifButton = (Button) findViewById(R.id.btn_notification);
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
                            strDestination = "home";
                        }
                    }
                }
            }
            mRequest.setVisibility(View.INVISIBLE);
            Button notifButton = (Button) findViewById(R.id.btn_notification);
            notifButton.setVisibility(View.VISIBLE);
            notifButton.setOnClickListener(new View.OnClickListener() {
                Date currentTime = Calendar.getInstance().getTime();
                Date TimeOfClick;
                @Override

                public void onClick(View v) {

                        TimeOfClick = currentTime ;
                        new Client.sendUsers().execute();
                        Toast.makeText(getApplicationContext(),
                                "Notification envoyée !",
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),
                                "Merci d'attendre 1 minute avant d'envoyer une nouvelle notification",
                                Toast.LENGTH_LONG).show();

                }
            });
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        Marker markerEsaip = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_esaip))
                .title("ESAIP")
                .position(new LatLng(47.464051, -0.497334)));
        InfoWindowData info = new InfoWindowData();
        info.setImage("");
        info.setHotel("EMAIL : contact.esaip@esaip.org");
        info.setFood("TELEPHONE : 02414517475");
        info.setTransport("ADRESSE : 3rue du 8 mai 1945 ");
        markerEsaip.setTag(info);
        Marker markerAppart = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_home))
                .title("Maison")
                .position(new LatLng( Double.valueOf(latit),Double.valueOf(longit)
                )));
        InfoWindowData info1 = new InfoWindowData();
        info1.setImage("");
        info1.setHotel("EMAIL : "+email);
        info1.setFood("TELEPHONE : "+telephone);
        info1.setTransport("ADRESSE : "+adresse);
        markerAppart.setTag(info1);
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);

        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.setOnMarkerClickListener(this);


        LatLng position = new LatLng(47.46848551035859, -0.5252838134765625);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 12);
        map.animateCamera(yourLocation);

        // You can customize the marker_conducteur image using images bundled with
        // your app, or dynamically generated bitmaps.
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
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
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

    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Covoiturage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
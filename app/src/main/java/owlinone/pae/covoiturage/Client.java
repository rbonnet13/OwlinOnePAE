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
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.session.Session;

/**
 * Created by rudyb on 15/01/2018.
 */

public class Client extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
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
    private  String response ="";
    HttpHandler sh = new HttpHandler();
    private String TAG = Appartement.class.getSimpleName();
    private ListView lv;
    String url= null;
    String strDetail = "", strDetailTel = "", strNomPropDetail = "", strLongitude = "", strLatitude = "";
    String strMail = "", strAdresse = "", strCommentaire = "RAS", strVille = "", strPrix = "", strDispoContext = "";
    String strNomContext = "", strIdContext = "", disponible = "Disponible", nonDisponible = "Non disponible";

    HashMap <String, String> obj = new HashMap();
    HashMap <String, String> objDispo = new HashMap();
    ArrayList<HashMap<String, String>> covoitList;
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
        setContentView(R.layout.clientmap);
        session = new Session(getApplicationContext());
        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(Client.this, Covoiturage.class);
                startActivity(intent);
            }
        });
        mRequest = (Button) findViewById(R.id.request);

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Client.AsyncDataClass().execute();

            }
        });
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

            int R = 6378000 ; //Rayon de la terre en mètre

            double lat_a = convertRad(lat_a_degre);
            double lon_a = convertRad(lon_a_degre);
            double lat_b = convertRad(lat_b_degre);
            double lon_b = convertRad(lon_b_degre);

            double d = R * (Math.PI/2 - Math.asin( Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));
            return d;
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
                    String prenom           = a.getString("PRENOM");
                    String nom              = a.getString("NOM");
                    String adresseMail      = a.getString("email");
                    String telephone        = a.getString("TELEPHONE");
                    String ville            = a.getString("VILLE");
                    String adresse          = a.getString("ADRESSE");
                    String CP               = a.getString("CP");
                    Double latitudeAppart   = a.getDouble("LATITUDE");
                    Double longitudeAppart  = a.getDouble("LONGITUDE");

                    double result_covoitureage =  Distance(latitude,longitude,latitudeAppart,longitudeAppart);

                    // Affiche les appartements que s'il est disponible ou non disponible
                    if(adresseMail.equals(email))
                    {
                        //on n'ajoute pas l'adresse de l'utilisateur courant
                    }else{
                        if (result_covoitureage <= 500.0 ){
                            HashMap<String, String> covoit = new HashMap<>();
                            // adding each child node to HashMap key => value
                            covoit.put("id", String.valueOf(id_covoit));
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
    }



    @Override
    public void onMapReady(GoogleMap map) {
        Marker markerEsaip = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_esaip))
                .position(new LatLng(47.464051, -0.497334)));
        Marker markerAppart = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_home))
                .position(new LatLng( Double.valueOf(latit),Double.valueOf(longit)
                )));
        LatLng position = new LatLng(47.46848551035859, -0.5252838134765625);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 12);
        map.animateCamera(yourLocation);
        // You can customize the marker image using images bundled with
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
}
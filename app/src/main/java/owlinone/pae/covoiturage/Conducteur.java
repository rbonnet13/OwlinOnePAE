package owlinone.pae.covoiturage;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.session.Session;

/**
 * Created by rudyb on 15/01/2018.
 */

public class Conducteur extends AppCompatActivity {

    Session session;
    long timeInMilliseconds = 0;
    private String TAG = Conducteur.class.getSimpleName();
    private ListView lv;
    String url= null;
    String  strPrenom = "", strNameUser = "", strNom = "", strAdresse = "", strDate = "", strTel = "", strDestination = "";


    HashMap <String, String> obj = new HashMap();
    ArrayList<HashMap<String, String>> notifList;

    SwipeRefreshLayout mSwipeRefreshLayout;

    //Redémarre l'activité
    private void restartActivity()
    {
        Intent intent = getIntent();
        intent.putExtra("url",url);
        finish();
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notif, menu);
        return true;
    }
    //Item de sélection de l'item pour le tri des appartements---------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
            case R.id.DestinationNotif:
                return true;
            case R.id.HomeNotif:
                url = AddressUrl.strNotifHome ;
                restartActivity();
                return true;
            case R.id.SchoolNotif:
                url = AddressUrl.strNotifSchool;
                restartActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conducteurmap);
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar9);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conducteur.this, Covoiturage.class);
                startActivity(intent);              }
        });

        //Affichage de la flèche de retour-----------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Glisser du doigt pour rafraichir----------------------------------------------------------
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.notification_activity_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(5,199,252));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                mSwipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mSwipeRefreshLayout.setRefreshing(false);
                        notifList = new ArrayList<>();
                        lv = (ListView) findViewById(R.id.listNotification);
                        new Conducteur.GetNotif().execute();

                    }
                }, 3000);
            }
        });

        // User Session Manager
        session = new Session(getApplicationContext());
        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_LONG).show();
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        strNameUser = user.get(Session.KEY_NAME);

        notifList = new ArrayList<>();
        lv         = (ListView) findViewById(R.id.listNotification);
        new GetNotif().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  JSONObject sTest = new JSONObject();
                //  String strTest = String.valueOf(lv.getItemAtPosition(position));
                obj              = (HashMap)lv.getItemAtPosition(position);
                strNom        = obj.get("NOM_NOTIF");
                strPrenom        = obj.get("PRENOM_NOTIF");
                strAdresse = obj.get("ADRESSE_NOTIF");
                strTel    = obj.get("TELEPHONE_NOTIF");
                strDate    = obj.get("DATE_NOTIF");
                strDestination    = obj.get("DESTINATION_NOTIF");


                //On récupère les valeurs et utilisont INTENT pour l'enregistrer pour la prochaine activité
                Intent intentNotification = new Intent(getApplicationContext(), DetailNotif.class);
                intentNotification.putExtra("strNom",strNom);
                intentNotification.putExtra("strPrenom",strPrenom);
                intentNotification.putExtra("strAdresse",strAdresse);
                intentNotification.putExtra("strTel",strTel);
                intentNotification.putExtra("strDate",strDate);
                intentNotification.putExtra("strDestination",strDestination);
                startActivity(intentNotification);
            }
        });

        //Sert pour l'appuie long (Entregistre le context du menu
        registerForContextMenu(lv);
    }

    private class GetNotif extends AsyncTask<Void, Void, Void>
    {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String responseRequete= "";
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HashMap<String, String> parameters = new HashMap<>();
                HttpHandler sh = new HttpHandler();
                parameters.put("PSEUDO_CONDUCTEUR_NOTIF", strNameUser);
                if(getIntent().getSerializableExtra("url")==null)
                {
                    Calendar c = Calendar.getInstance();
                    int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

                    if(timeOfDay >= 0 && timeOfDay < 12){
                        responseRequete = sh.performPostCall(AddressUrl.strNotifSchool, parameters);

                    }else{
                        responseRequete = sh.performPostCall(AddressUrl.strNotifHome, parameters);
                    }
                }
                else {responseRequete = sh.performPostCall(url, parameters);}
                Log.e(TAG, "url: " + url);
                System.out.println("User :: " + strNameUser);

                JSONArray jsonArray = new JSONArray(responseRequete);
                // looping through All Notification
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject a = jsonArray.getJSONObject(i);
                    String nom_notif = a.getString("NOM_NOTIF");
                    String prenom_notif = a.getString("PRENOM_NOTIF");
                    String adresse_notif = a.getString("ADRESSE_NOTIF");
                    String tel_notif = a.getString("TELEPHONE_NOTIF");
                    String date_notif = a.getString("DATE_NOTIF");
                    String destination_notif = a.getString("DESTINATION_NOTIF");


                    //AgoTime: récupération de la date de publication et actuelle
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(date_notif);
                        timeInMilliseconds = mDate.getTime();
                        System.out.println("Date in milli :: " + timeInMilliseconds);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long time = System.currentTimeMillis();

                    //Fonction pour utiliser agoTime
                    String agoTime = (String) DateUtils.getRelativeTimeSpanString(timeInMilliseconds, time, DateUtils.SECOND_IN_MILLIS);

                    HashMap<String, String> notification = new HashMap<>();
                    // adding each child node to HashMap key => value
                    notification.put("NOM_NOTIF", nom_notif);
                    notification.put("PRENOM_NOTIF", prenom_notif);
                    notification.put("ADRESSE_NOTIF", adresse_notif);
                    notification.put("TELEPHONE_NOTIF", tel_notif);
                    if(new String("Home").equals(destination_notif)){notification.put("DESTINATION_NOTIF", String.valueOf(R.drawable.notif_vers_home));}
                    else {notification.put("DESTINATION_NOTIF", String.valueOf(R.drawable.notif_vers_esaip));}
                    notification.put("DATE_NOTIF", agoTime);
                    // adding contact to contact list
                    notifList.add(notification);
            }
            return null;
        } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(Conducteur.this, notifList,
                    R.layout.list_notif, new String[]{"NOM_NOTIF","PRENOM_NOTIF","ADRESSE_NOTIF","TELEPHONE_NOTIF","DESTINATION_NOTIF","DATE_NOTIF"},
                    new int[]{R.id.nom_notif, R.id.prenom_notif,R.id.adresse_notif,R.id.tel_notif,R.id.destination_notif,R.id.date_notif});
            lv.setAdapter(adapter);
        }
    }
}
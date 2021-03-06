package owlinone.pae.covoiturage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.session.Session;

/**
 * Created by AnthonyCOPPIN on 02/03/2018.
 */

public class ConducteurTab extends AppCompatActivity{
    TabLayout myTabs;
    ViewPager myPage;
    private ProgressDialog dialogChargementEsaip;
    private ProgressDialog dialogChargementHome;


    HashMap<String, String> notification;
    Session session;
    long timeInMilliseconds = 0;

    NotificationEsaip notifEsaip;
    NotificationHome notifHome;
    String url = AddressUrl.strNotifGeneral;
    String strPrenom = "",nbNotif="", strNameUser = "", strNom = "", strAdresse = "", strDate = "", strTel = "", strDestination = "", strPseudo = "", strPrenomUser = "", strNomUser = "";


    HashMap<String, String> obj = new HashMap();
    ArrayList<HashMap<String, String>> notifHomeList;
    ArrayList<HashMap<String, String>> notifEsaipList;


    SwipeRefreshLayout mSwipeRefreshLayout;
    private String strNameUserEnvoi;
    private int toeic;
    private boolean obtentionDiplome;

    //Redémarre l'activité
    private void restartActivity() {
        Intent intent = getIntent();
        intent.putExtra("url", url);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conducteur_tab);

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar9);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
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

        myTabs = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        myTabs.addTab(myTabs.newTab().setText("VERS ESAIP"));
        myTabs.addTab(myTabs.newTab().setText("VERS DOMICILE"));
        myTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        myPage = (ViewPager) findViewById(R.id.pager);

        myPage.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(myTabs));

        notifHomeList = new ArrayList<>();
        notifEsaipList = new ArrayList<>();
        notifEsaip = new NotificationEsaip();
        notifHome = new NotificationHome();
        // User Session Manager
        session = new Session(getApplicationContext());
        if (session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        strNameUser = user.get(Session.KEY_NAME);
        strNameUserEnvoi   = strNameUser.replace("'","''");
        // get name
        strPrenomUser = user.get(Session.KEY_PRENOM);
        strNomUser = user.get(Session.KEY_NOM);

        new GetNotif().execute();

    }

    private class GetNotif extends AsyncTask<Void, Void, Void> implements TabLayout.OnTabSelectedListener {
        Intent intent = getIntent();
        String responseRequete = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HashMap<String, String> parameters = new HashMap<>();
                HttpHandler sh = new HttpHandler();
                parameters.put("PSEUDO_CONDUCTEUR_NOTIF", strNameUserEnvoi);

                responseRequete = sh.performPostCall(url, parameters);

                System.out.println("User :: " + strNameUserEnvoi);

                JSONArray jsonArray = new JSONArray(responseRequete);
                // looping through All Notification
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject a = jsonArray.getJSONObject(i);
                    String id_notif = a.getString("ID_NOTIF");
                    String pseudo_notif = a.getString("PSEUDO_CONDUCTEUR_NOTIF");
                    String nom_notif = a.getString("NOM_NOTIF");
                    String prenom_notif = a.getString("PRENOM_NOTIF");
                    String adresse_notif = a.getString("ADRESSE_NOTIF");
                    String tel_notif = a.getString("TELEPHONE_NOTIF");
                    String date_notif = a.getString("DATE_NOTIF");
                    String destination_notif = a.getString("DESTINATION_NOTIF");
                    String clicked_notif = a.getString("CLICKED_NOTIF");


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

                    notification = new HashMap<>();
                    // adding each child node to HashMap key => value
                    notification.put("ID_NOTIF", id_notif);
                    notification.put("PSEUDO_CONDUCTEUR_NOTIF", pseudo_notif);
                    notification.put("NOM_NOTIF", nom_notif);
                    notification.put("PRENOM_NOTIF", prenom_notif);
                    notification.put("ADRESSE_NOTIF", adresse_notif);
                    notification.put("TELEPHONE_NOTIF", tel_notif);
                    notification.put("DESTINATION_NOTIF", destination_notif);
                    notification.put("USER_PRENOM", strPrenomUser);
                    notification.put("USER_NAME", strNomUser);
                    notification.put("CLICKED_NOTIF", clicked_notif);


                    notification.put("DATE_NOTIF", agoTime);
                    if (new String("home").equals(destination_notif)) {
                        notification.put("LOGO", String.valueOf(R.drawable.logo_maison));

                        notifHomeList.add(notification);
                    }
                    if (new String("school").equals(destination_notif)) {
                        notification.put("LOGO", String.valueOf(R.drawable.logo_esaip));

                        notifEsaipList.add(notification);
                    }
                    notifEsaip.setNotif(notifEsaipList);
                    notifHome.setNotif(notifHomeList);
                }
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Creating our pager adapter
            if(!notifEsaipList.isEmpty() || !notifHomeList.isEmpty()){
                Pager adapter = new Pager(getSupportFragmentManager(), myTabs.getTabCount(),notifEsaip, notifHome);
                myPage.setAdapter(adapter);
                myTabs.setOnTabSelectedListener(this);
            }

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            myPage.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), Covoiturage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("nbNotif", nbNotif);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}


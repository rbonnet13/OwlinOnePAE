package owlinone.pae.calendrier;

/**
 * Created by AnthonyCOPPIN on 04/01/2017.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.covoiturage.Covoiturage;
import owlinone.pae.divers.APropos;
import owlinone.pae.divers.Bug;
import owlinone.pae.main.MainActivity;
import owlinone.pae.session.Compte;
import owlinone.pae.session.Session;

public class CalendarExtra extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = CalendarExtra.class.getSimpleName();
    CompactCalendarView compactCalendar;
    private ListView lvEvent;

    long timeInMilliseconds = 0;
    Integer erreurMonth = 1;
    Integer erreurYear = 1900;

    ArrayList<HashMap<String, String>> eventClicked;
    ArrayList<HashMap<String, String>> eventCurrentDay;
    ArrayList<HashMap<String, String>> eventList;
    HashMap<String, String> testEvent = new HashMap();

    String strHourFinal;
    String strMinuteFinal;
    String email, name, photoBDD;

    ImageView imgLogo = null;
    Date date = new Date();
    Session session;


    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
    private TextView notifcovoit;
    private String nbNotif;
    private String nameEnvoi;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le contenu de l'activté sélectionnée
        setContentView(R.layout.activity_calendrier);

        // Affiche la toolbar correspondant à l'activité affichée
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Calendrier");

        // Active le drawer dans l'activité affichée
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        eventList = new ArrayList<>();
        imgLogo = (ImageView) findViewById(R.id.imgOwlEvent);
        lvEvent = (ListView) findViewById(R.id.listEvent);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Met en surbrillance dans le drawer l'activité affichée
        navigationView.setCheckedItem(R.id.nav_calendrier);

        // User Session Manager
        session = new Session(getApplicationContext());
        if (session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        name = user.get(Session.KEY_NAME);
        if(name != null){
            nameEnvoi = name.replace("'", "''");}
        // get email
        email = user.get(Session.KEY_EMAIL);
        // get base 64 photo code from BDD
        photoBDD = user.get(Session.KEY_PHOTO);


        notifcovoit = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_covoiturage));
        new DataNotifConducteur().execute();
        
        // Show user data on activity
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = header.findViewById(R.id.image_menu);

        //arrayListEvent = new ArrayList<EventCalendar>();
        new ReadJsonEvent().execute();

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

        TextView dateText = (TextView) findViewById(R.id.date_calendar);
        dateText.setText(dateFormatMonth.format(date));

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // insertion
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                try {
                    timeInMilliseconds = dateClicked.getTime();
                    Log.e("Time", "Date cliqué: " + timeInMilliseconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                eventClicked = new ArrayList<>();

                for (HashMap<String, String> entry : eventList) {
                    if (dateClicked.getDate() == Integer.valueOf(entry.get("DAY_EVENEMENT")) && dateClicked.getMonth() == Integer.valueOf(entry.get("MONTH_EVENEMENT")) &&
                            dateClicked.getYear() == Integer.valueOf(entry.get("YEAR_EVENEMENT"))) {
                        imgLogo.setVisibility(View.INVISIBLE);
                        lvEvent.setVisibility(View.VISIBLE);
                        HashMap<String, String> mylistEvent = new HashMap<>();

                        mylistEvent.put("DATE", entry.get("ALL_EVENEMENT"));
                        mylistEvent.put("LOGO_FACEBOOK", entry.get("FACEBOOK_EVENEMENT"));
                        mylistEvent.put("LIEN_FACEBOOK", entry.get("LIEN_EVENEMENT"));

                        eventClicked.add(mylistEvent);
                    }
                }

                if (eventClicked.size() != 0) {
                    ListAdapter adapter = new SimpleAdapter(CalendarExtra.this, eventClicked,
                            R.layout.event_activity, new String[]{"DATE", "LOGO_FACEBOOK"},
                            new int[]{R.id.text_event_calendar, R.id.facebook_logo_event});
                    lvEvent.setAdapter(adapter);

                    lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            testEvent = (HashMap) lvEvent.getItemAtPosition(position);

                            String lien_event_click = testEvent.get("LIEN_FACEBOOK");
                            Log.e("Event", "Lien onclick: " + lien_event_click);

                            if (lien_event_click != "null") {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                intent.setData(Uri.parse(lien_event_click));
                                startActivity(intent);
                            }
                        }
                    });
                }
                else {
                    imgLogo.setVisibility(View.VISIBLE);
                    lvEvent.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onMonthScroll(Date fisrtDayOfNewMonth) {
                //actionBar.setTitle(dateFormatMonth.format(fisrtDayOfNewMonth));
                TextView dateText = (TextView) findViewById(R.id.date_calendar);
                dateText.setText(dateFormatMonth.format(fisrtDayOfNewMonth));
                imgLogo.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.INVISIBLE);
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

        if (id == R.id.nav_deconnexion) {
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
        } else if (id == R.id.nav_covoiturage) {
            Intent searchIntent = new Intent(getApplicationContext(), Covoiturage.class);
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

    class ReadJsonEvent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = null;
            jsonStr = sh.makeServiceCall(AddressUrl.strConnexionEvent);
            Log.e(TAG, "Reponse from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject a = jsonArray.getJSONObject(i);
                        String date_event = a.getString("DEBUT_EVENEMENT");
                        String titre_event = a.getString("NOM_EVENEMENT");
                        String lien_event = a.getString("LIEN_EVENEMENT");
                        HashMap<String, String> evenement = new HashMap<>();

                        if (lien_event != "null") {
                            Log.e(TAG, "FACEBOOK_EVENEMENT : " + String.valueOf(R.drawable.facebook));
                            evenement.put("FACEBOOK_EVENEMENT", String.valueOf(R.drawable.facebook));
                        } else {
                            evenement.put("FACEBOOK_EVENEMENT", null);
                        }

                        // adding each child node to HashMap key => value
                        evenement.put("LIEN_EVENEMENT", lien_event);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date mDate = sdf.parse(date_event);

                            evenement.put("DAY_EVENEMENT", String.valueOf(mDate.getDate()));
                            evenement.put("MONTH_EVENEMENT", String.valueOf(mDate.getMonth()));
                            evenement.put("YEAR_EVENEMENT", String.valueOf(mDate.getYear()));
                            if((String.valueOf(mDate.getHours()).length() == 1)) {
                                strHourFinal = "0" + mDate.getHours();
                            } else {
                                strHourFinal = String.valueOf(mDate.getHours());
                            }
                            if((String.valueOf(mDate.getMinutes()).length() == 1)) {
                                strMinuteFinal = mDate.getMinutes() + "0";
                            } else {
                                strMinuteFinal = String.valueOf(mDate.getMinutes());
                            }
                            evenement.put("ALL_EVENEMENT", strHourFinal + ":" + strMinuteFinal + "     " + titre_event);
                            timeInMilliseconds = mDate.getTime();
                            evenement.put("DATEMILLI_EVENEMENT", String.valueOf(timeInMilliseconds));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // adding contact to contact list
                        eventList.add(evenement);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            for (HashMap<String, String> entry : eventList) {
                long date = Long.parseLong(entry.get("DATEMILLI_EVENEMENT"));
                Log.e(TAG, "Date Event: " + date);
                Event evl2 = new Event(Color.RED, date, "");
                compactCalendar.addEvent(evl2);
            }

            TextView dateText = (TextView) findViewById(R.id.date_calendar);
            dateText.setText(dateFormatMonth.format(date));

            compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
            compactCalendar.setUseThreeLetterAbbreviation(true);


            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentDay = cal.get(Calendar.DAY_OF_MONTH);

            eventCurrentDay = new ArrayList<>();

            for (HashMap<String, String> entry : eventList) {
                if (currentDay == Integer.valueOf(entry.get("DAY_EVENEMENT")) && currentMonth == Integer.valueOf(entry.get("MONTH_EVENEMENT")) + erreurMonth &&
                        currentYear == Integer.valueOf(entry.get("YEAR_EVENEMENT")) + erreurYear) {
                    imgLogo.setVisibility(View.INVISIBLE);
                    lvEvent.setVisibility(View.VISIBLE);
                    HashMap<String, String> mylistEvent = new HashMap<>();

                    mylistEvent.put("DATE", entry.get("ALL_EVENEMENT"));
                    mylistEvent.put("LOGO_FACEBOOK", entry.get("FACEBOOK_EVENEMENT"));
                    mylistEvent.put("LIEN_FACEBOOK", entry.get("LIEN_EVENEMENT"));
                    System.out.println("Boucle for:");
                    for (Map.Entry mapentry : mylistEvent.entrySet()) {
                        System.out.println("clé: " + mapentry.getKey()
                                + " | valeur: " + mapentry.getValue());
                    }
                    eventCurrentDay.add(mylistEvent);
                }
            }

            if (eventCurrentDay.size() != 0) {
                ListAdapter adapter = new SimpleAdapter(CalendarExtra.this, eventCurrentDay,
                        R.layout.event_activity, new String[]{"DATE", "LOGO_FACEBOOK"},
                        new int[]{R.id.text_event_calendar, R.id.facebook_logo_event});
                lvEvent.setAdapter(adapter);

                lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        testEvent = (HashMap) lvEvent.getItemAtPosition(position);

                        String lien_event_click = testEvent.get("LIEN_FACEBOOK");

                        if (lien_event_click != "null") {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(lien_event_click));
                            startActivity(intent);
                        }
                    }
                });
            }
            else {
                imgLogo.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.INVISIBLE);
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
                parametersConducteur.put("name",nameEnvoi);

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

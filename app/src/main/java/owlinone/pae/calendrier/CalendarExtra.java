package owlinone.pae.calendrier;

/**
 * Created by AnthonyCOPPIN on 04/01/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import owlinone.pae.stage.Stage;

import static owlinone.pae.configuration.AddressUrl.strPhoto;


public class CalendarExtra extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = CalendarExtra.class.getSimpleName();
    CompactCalendarView compactCalendar;
    ArrayAdapter<String> adapter;
    private ListView lvEvent;
    ArrayList<HashMap<String, String>> eventClicked;
    ArrayList<HashMap<String, String>> eventCurrentDay;
    Integer erreurMonth = 1;
    Integer erreurYear = 1900;
    ArrayList<HashMap<String, String>> eventList;
    HashMap<String, String> testEvent = new HashMap();
    long timeInMilliseconds = 0;
    ImageView imgLogo = null;
    Date date = new Date();
    //ArrayList<EventCalendar> arrayListEvent;
    Session session;


    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String name = user.get(Session.KEY_NAME);
        // get email
        String email = user.get(Session.KEY_EMAIL);
        String photoT = user.get(Session.KEY_PHOTO);

        // Show user data on activity
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = (ImageView) header.findViewById(R.id.image_menu);

        //arrayListEvent = new ArrayList<EventCalendar>();
        new ReadJsonEvent().execute();

        //image
        if (!user.get(Session.KEY_PHOTO).equals("sans image")) {
            String url_image = strPhoto + user.get(Session.KEY_PHOTO);
            url_image = url_image.replace(" ", "%20");
            try {
                Log.i("RESPUESTA IMAGE: ", "" + url_image);
                Glide.with(this).load(url_image).into(photo);
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
                //lvEvent = (ListView) findViewById(R.id.listEvent);

                eventClicked = new ArrayList<>();

                Log.e("Prout", "Date cliqué Year: " + dateClicked.getYear());
                for (HashMap<String, String> entry : eventList) {
                    if (dateClicked.getDate() == Integer.valueOf(entry.get("DAY_EVENEMENT")) && dateClicked.getMonth() == Integer.valueOf(entry.get("MONTH_EVENEMENT")) &&
                            dateClicked.getYear() == Integer.valueOf(entry.get("YEAR_EVENEMENT"))) {
                        imgLogo.setVisibility(View.INVISIBLE);
                        lvEvent.setVisibility(View.VISIBLE);
                        HashMap<String, String> mylistEvent = new HashMap<>();

                        mylistEvent.put("DATE", entry.get("ALL_EVENEMENT"));
                        mylistEvent.put("LOGO_FACEBOOK", entry.get("FACEBOOK_EVENEMENT"));
                        mylistEvent.put("LIEN_FACEBOOK", entry.get("LIEN_EVENEMENT"));
                        System.out.println("Boucle for:");

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
        /*} else if (id == R.id.nav_calendrier) {
            Intent searchIntent = new Intent(getApplicationContext(), CalendarExtra.class);
            searchIntent.putExtra("mylist", arrayListEvent);
            startActivity(searchIntent);
            finish();*/
        } else if (id == R.id.nav_stage) {
            Intent searchIntent = new Intent(getApplicationContext(), Stage.class);
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
                        Log.e(TAG, "DEBUT_EVENEMENT : " + date_event);
                        Log.e(TAG, "NOM_EVENEMENT : " + titre_event);
                        Log.e(TAG, "LIEN_EVENEMENT : " + lien_event);

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
                            Log.e(TAG, "YEARDATE : " + String.valueOf(mDate.getYear()));
                            Log.e(TAG, "MONTHDATE : " + String.valueOf(mDate.getMonth())); //TODO changer pour deprecated et 1900 valeur
                            Log.e(TAG, "DAYDATE : " + String.valueOf(mDate.getDate()));
                            evenement.put("MINUTE_EVENEMENT", String.valueOf(mDate.getMinutes()));
                            evenement.put("HOUR_EVENEMENT", String.valueOf(mDate.getHours()));
                            evenement.put("DAY_EVENEMENT", String.valueOf(mDate.getDate()));
                            evenement.put("MONTH_EVENEMENT", String.valueOf(mDate.getMonth()));
                            evenement.put("YEAR_EVENEMENT", String.valueOf(mDate.getYear()));
                            evenement.put("ALL_EVENEMENT", mDate.getHours() + ":" + mDate.getMinutes() + "     " + titre_event);
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
                Event evl2 = new Event(Color.BLUE, date, "");
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
            Log.e("Current", "TEST DAY: " + currentYear + " " + currentMonth +" " + currentDay);

            eventCurrentDay = new ArrayList<>();

            for (HashMap<String, String> entry : eventList) {
                Log.e("Current", "TEST FOR: " + Integer.valueOf(entry.get("YEAR_EVENEMENT")) + " " + Integer.valueOf(entry.get("MONTH_EVENEMENT")) +" " + Integer.valueOf(entry.get("DAY_EVENEMENT")));
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
                Log.e("Current", "EventCurrent: " + eventCurrentDay.toString());
                ListAdapter adapter = new SimpleAdapter(CalendarExtra.this, eventCurrentDay,
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
    }
}

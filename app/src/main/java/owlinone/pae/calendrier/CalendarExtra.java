package owlinone.pae.calendrier;

/**
 * Created by AnthonyCOPPIN on 04/01/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
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
    long timeInMilliseconds = 0;
    ImageView imgLogo = null;
    ArrayList<String> mylistEvent = null;
    Date date = new Date();
    ArrayList<EventCalendar> arrayListEvent;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Met en surbrillance dans le drawer l'activité affichée
        navigationView.setCheckedItem(R.id.nav_calendrier);

        // User Session Manager
        session = new Session(getApplicationContext());
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        String name = user.get(Session.KEY_NAME);
        // get email
        String email = user.get(Session.KEY_EMAIL);
        String photoT = user.get(Session.KEY_PHOTO);

        // Show user data on activity
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = (ImageView)header.findViewById(R.id.image_menu);

        //image
        if(!user.get(Session.KEY_PHOTO).equals("sans image")){
            String url_image = strPhoto + user.get(Session.KEY_PHOTO);
            url_image = url_image.replace(" ","%20");
            try {
                Log.i("RESPUESTA IMAGE: ",""+url_image);
                Glide.with(this).load(url_image).into(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final ArrayList<EventCalendar> myList = (ArrayList<EventCalendar>) getIntent().getSerializableExtra("mylist");
        imgLogo = (ImageView) findViewById(R.id.imgOwlEvent);

        TextView dateText = (TextView) findViewById(R.id.date_calendar);
        dateText.setText(dateFormatMonth.format(date));


        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        for(EventCalendar d : myList)
        {
            long finalTest1 = Long.parseLong(d.getStrDate());
            Event evl2 = new Event(Color.BLUE,finalTest1,"");
            compactCalendar.addEvent(evl2);
        }


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
                lvEvent = (ListView) findViewById(R.id.listEvent);


                mylistEvent = new ArrayList<String>();


                imgLogo.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.INVISIBLE);

                Context context = getApplicationContext();
                Log.e("Prout", "Date cliqué: " + dateClicked.toString());

                for(EventCalendar d : myList)
                {

                    long finalTest1 = Long.parseLong(d.getStrDate());

                    if (dateClicked.getDate() == d.getiDay() && dateClicked.getMonth()==d.getiMonth() &&
                            dateClicked.getYear() == d.getiYear()){
                        imgLogo.setVisibility(View.INVISIBLE);
                        lvEvent.setVisibility(View.VISIBLE);

                        mylistEvent.add(d.getStrEvent());
                        Log.e("List", "myListEvent: " + mylistEvent.toString());
                }
                }
                Log.e("List", "listSize: " + mylistEvent.size());

                if(mylistEvent.size() != 0)
                {
                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.event_activity, mylistEvent);

                    lvEvent.setAdapter(adapter);
                }
            }
            @Override
            public void onMonthScroll(Date fisrtDayOfNewMonth) {
                //actionBar.setTitle(dateFormatMonth.format(fisrtDayOfNewMonth));
               TextView dateText = (TextView) findViewById(R.id.date_calendar);
               dateText.setText(dateFormatMonth.format(fisrtDayOfNewMonth));
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
}


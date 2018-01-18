package owlinone.pae;

/**
 * Created by AnthonyCOPPIN on 04/01/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import owlinone.pae.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarExtra extends AppCompatActivity implements Serializable{

    private String TAG = CalendarExtra.class.getSimpleName();
    CompactCalendarView compactCalendar;
    ArrayList newArrayList = null;
    long finalTest;
    ArrayAdapter<String> adapter;
    private ListView lvEvent;
    long timeInMilliseconds = 0;
    List<Event> events;
    TextView textEvent = null;
    ImageView imgLogo = null;
    ArrayList<String> mylistEvent = null;
    Date date = new Date();


    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_extra);
        final ArrayList<EventCalendar> myList = (ArrayList<EventCalendar>) getIntent().getSerializableExtra("mylist");
        imgLogo = (ImageView) findViewById(R.id.imgOwlEvent);

        final ActionBar actionBar = getSupportActionBar();
        // affichage flèche
        actionBar.setTitle(dateFormatMonth.format(date));
        actionBar.setDisplayHomeAsUpEnabled(false);


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

                actionBar.setTitle(dateFormatMonth.format(fisrtDayOfNewMonth));
            }
        });
    }
    }


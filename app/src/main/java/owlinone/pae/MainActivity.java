package owlinone.pae;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import owlinone.pae.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<Article> arrayList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    long timeInMilliseconds = 0;
    int index;
    int top;
    ArrayList<EventCalendar> arrayListEvent;
    Session session;
    private final String strPhoto = AddressUrl.strPhoto;

    @Override
    public void onRestart() {
        super.onRestart();

        arrayList = new ArrayList<Article>();
        lv = (ListView) findViewById(R.id.listviewperso);
        new ReadJson().execute();
        arrayListEvent = new ArrayList<EventCalendar>();
        new ReadJsonEvent().execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        String name = user.get(Session.KEY_NAME);
        // get email
        String pass = user.get(Session.KEY_PASSWORD);
        // get email
        String photoT = user.get(Session.KEY_PHOTO);




        // Show user data on activity
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText(name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(pass);
        ImageView photo = (ImageView)header.findViewById(R.id.image_menu);

        //image
        Toast.makeText(MainActivity.this, "Photo = "+ photoT, Toast.LENGTH_LONG).show();
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


        arrayList = new ArrayList<Article>();
        lv = (ListView) findViewById(R.id.listviewperso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        arrayListEvent = new ArrayList<EventCalendar>();
        new ReadJsonEvent().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Glisser du doigt pour rafraichir la page donc l'activité----------------------------------
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_article);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(5,199,252)); // Couleur colorPrimary
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        arrayList = new ArrayList<Article>();
                        lv = (ListView) findViewById(R.id.listviewperso);
                        new ReadJson().execute();

                    }
                }, 3000);
            }
        });
        new ReadJson().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Article articleSelected =   arrayList.get(position);
                Intent intent = new Intent(getApplicationContext(), DetailArticle.class);

                index = lv.getFirstVisiblePosition();
                View v = lv.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

                class sendView extends AsyncTask<Void, Void, Void>
                {
                    ProgressDialog pd;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        if (pd != null)
                        {
                            pd.dismiss();
                        }
                    }
                    Exception exception;
                    @Override
                    protected Void doInBackground(Void... arg0)
                    {
                        try
                        {
                            HashMap<String, String> parameters = new HashMap<>();
                            String url = AddressUrl.strNumberViewArticle;
                            parameters.put("ID_ARTICLE", String.valueOf(articleSelected.getStrID()));

                            performPostCall(url, parameters);
                            return null;
                        } catch (Exception e)
                        {
                            this.exception = e;
                            return null;
                        }
                    }

                    public String  performPostCall(String requestURL,
                                                   HashMap<String, String> postDataParams)
                    {
                        URL url;
                        String response = "";
                        try
                        {
                            url = new URL(requestURL);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(15000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(getPostDataString(postDataParams));
                            writer.flush();
                            writer.close();
                            os.close();
                            int responseCode=conn.getResponseCode();
                            Log.e("HTTP code:", String.valueOf(responseCode));
                            if (responseCode == HttpsURLConnection.HTTP_OK)
                            {
                                String line;
                                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                while ((line=br.readLine()) != null)
                                {
                                    response+=line;
                                }
                            }
                            else
                            {
                                response="";
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return response;
                    }
                    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
                    {
                        StringBuilder result = new StringBuilder();
                        boolean first = true;
                        for(Map.Entry<String, String> entry : params.entrySet())
                        {
                            if (first)
                                first = false;
                            else
                                result.append("&");
                            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                        }
                        return result.toString();
                    }
                }
                new sendView().execute();
                //Récupérer les string pour l'INTENT. Utilisation dans la classe DetailArticle------
                intent.putExtra("strImage"    , articleSelected.getStrImg());
                intent.putExtra("strCategorie", articleSelected.getStrCategorie());
                intent.putExtra("strCorps"    , articleSelected.getStrCorps());
                intent.putExtra("strTitre"    , articleSelected.getStrTitre());
                intent.putExtra("strDate"     , articleSelected.getStrDate());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    //Navigation de la partie du menu principal-----------------------------------------------------
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_planning) {
            Intent intentPlanning = new Intent(getApplicationContext(), CalendarExtra.class);
            intentPlanning.putExtra("mylist", arrayListEvent);
            startActivity(intentPlanning);

        } else if (id == R.id.nav_appartements) {
            Intent intentAppart = new Intent(getApplicationContext(), Appartement.class);
            startActivity(intentAppart);
        } else if (id == R.id.nav_covoit) {
            Intent intentCovoit = new Intent(getApplicationContext(), Covoiturage.class);
            startActivity(intentCovoit);
        } else if (id == R.id.nav_stage) {
            Intent intentStage = new Intent(getApplicationContext(), Stage.class);
            startActivity(intentStage);
        } else if (id == R.id.nav_connexion) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            MenuItem itemCo = menu.findItem(R.id.nav_deconnexion);
            itemCo.setVisible(false);
            Intent intentConnexion = new Intent(getApplicationContext(), MainLogin.class);
            startActivity(intentConnexion);
        } else if(id == R.id.nav_deconnexion){
            View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
            ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("");
            ((TextView) header.findViewById(R.id.id_email_user)).setText("Vous n'êtes pas connecté");
            ImageView photo = (ImageView)header.findViewById(R.id.image_menu);
            photo.setImageResource(R.drawable.profile);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            MenuItem itemCo = menu.findItem(R.id.nav_connexion);
            itemCo.setVisible(true);
            MenuItem itemDeco = menu.findItem(R.id.nav_deconnexion);
            itemDeco.setVisible(false);
            session.logoutUser();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class ReadJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = null;
            jsonStr = sh.makeServiceCall(AddressUrl.strArticle);
            Log.e(TAG, "Reponse from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject a        = jsonArray.getJSONObject(i);
                        int id_article      = a.getInt("ID_ARTICLE");
                        String tit_article  = a.getString("TITRE_ARTICLE");
                        String cat_article  = a.getString("CATEGORIE_ARTICLE");
                        String img_article  = a.getString("IMAGE_ARTICLE");
                        String cor_article  = a.getString("CORPS_ARTICLE");
                        String date_article = a.getString("DATE_ARTICLE");
                        int nb_view         = a.getInt("NOMBRE_VUE_ARTICLE");
                        int nb_like         = a.getInt("NOMBRE_LIKE_ARTICLE");

                        Article art         = new Article();
                        art.setStrID(id_article);
                        art.setStrTitre(tit_article);
                        art.setStrCategorie(cat_article);
                        art.setStrImg(img_article);
                        art.setStrCorps(cor_article);
                        art.setIntVueArticle(nb_view);
                        art.setIntLike(nb_like);

                        //AgoTime: récupération de la date de publication et actuelle
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date mDate = sdf.parse(date_article);
                            timeInMilliseconds = mDate.getTime();
                            System.out.println("Date in milli :: " + timeInMilliseconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long time= System.currentTimeMillis();

                        //Fonction pour utiliser agoTime
                        String agoTime = (String) DateUtils.getRelativeTimeSpanString(timeInMilliseconds, time,DateUtils.SECOND_IN_MILLIS);

                        art.setStrDate(agoTime);

                        arrayList.add(art);

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
            ArticleAdapter adapter = new ArticleAdapter(getApplicationContext(), R.layout.row_list, arrayList);
            if(lv.getAdapter()==null)
                lv.setAdapter(adapter);
            else{
                lv.setAdapter(adapter);
                lv.setSelectionFromTop(index, top);
            }

        }
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
                        JSONObject a        = jsonArray.getJSONObject(i);
                        int id_event      = a.getInt("ID_EVENEMENT");
                        String date_event = a.getString("DEBUT_EVENEMENT");
                        String titre_event  = a.getString("NOM_EVENEMENT");
                        EventCalendar event         = new EventCalendar();
                        event.setStrID(id_event);
                        event.setStrTitre(titre_event);

                        //AgoTime: récupération de la date de publication et actuelle
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date mDate = sdf.parse(date_event);
                            Log.e(TAG, "Reponse from url: " + mDate.getHours());
                            event.setiDay(mDate.getDate());
                            event.setiMonth(mDate.getMonth());
                            event.setiYear(mDate.getYear());
                            event.setiHeure(mDate.getHours());
                            event.setiMinute(mDate.getMinutes());
                            event.setStrEvent(mDate.getHours() + ":" + mDate.getMinutes() + "     " + titre_event);
                            timeInMilliseconds = mDate.getTime();

                            System.out.println("Date in milli :: " + timeInMilliseconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        event.setStrDate(String.valueOf(timeInMilliseconds));
                        arrayListEvent.add(i,event);


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
        }
    }
}
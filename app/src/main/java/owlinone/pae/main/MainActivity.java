package owlinone.pae.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.appartement.Appartement;
import owlinone.pae.article.Article;
import owlinone.pae.article.ArticleAdapter;
import owlinone.pae.article.DetailArticle;
import owlinone.pae.calendrier.CalendarExtra;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.covoiturage.Covoiturage;
import owlinone.pae.divers.APropos;
import owlinone.pae.divers.Bug;
import owlinone.pae.session.Compte;
import owlinone.pae.session.Session;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<Article> arrayList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    long timeInMilliseconds = 0;
    int index;
    int top;
    Session session;
    TextView notifcovoit;
    private  String nbNotif = "";
    private String email, name, photoBDD = "";
    private ProgressDialog pDialog;
    private String nameEnvoi;

    @Override
    public void onRestart() {
        super.onRestart();
        showProgressDialog();
        arrayList = new ArrayList<Article>();
        lv = (ListView) findViewById(R.id.listviewperso);
        new ReadJson().execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le contenu de la page home (activity_main)
        setContentView(R.layout.activity_main);
        showProgressDialog();

        // Affiche la toolbar correspondant à l'activité affichée
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        navigationView.setCheckedItem(R.id.nav_article);

        // User Session Manager
        session = new Session(getApplicationContext());
        if(session.checkLogin())
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

        notifcovoit =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_covoiturage));
        new DataNotifConducteur().execute();
        // Show user data on activity
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.id_pseudo_user)).setText("Bienvenue " + name);
        ((TextView) header.findViewById(R.id.id_email_user)).setText(email);
        ImageView photo = header.findViewById(R.id.image_menu);

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

        arrayList = new ArrayList<Article>();
        lv = (ListView) findViewById(R.id.listviewperso);

        //Glisser le doigt pour rafraichir la page donc l'activité----------------------------------
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
                }, 2000);
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
                    HttpHandler sh = new HttpHandler();
                    @Override
                    protected void onPreExecute() {
                        showProgressDialog();
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        dismissProgressDialog();
                        super.onPostExecute(result);
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

                            sh.performPostCall(url, parameters);
                            return null;
                        } catch (Exception e)
                        {
                            this.exception = e;
                            return null;
                        }
                    }
                }
                new sendView().execute();
                // Récupérer les string pour l'INTENT. Utilisation dans la classe DetailArticle-----
                intent.putExtra("strImage"    , articleSelected.getStrImg());
                intent.putExtra("strCategorie", articleSelected.getStrCategorie());
                intent.putExtra("strCorps"    , articleSelected.getStrCorps());
                intent.putExtra("strTitre"    , articleSelected.getStrTitre());
                intent.putExtra("strDate"     , articleSelected.getStrDate());
                startActivity(intent);
            }
        });
    }

    // Permet de fermer le drawer à l'appui de la touche retour si ce premier est ouvert
    @Override
    public void onBackPressed() {
        dismissProgressDialog();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else { // On kill l'application pour économiser la batterie
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
        Runtime.getRuntime().gc();
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
        } else if (id == R.id.nav_appartement) {
            Intent searchIntent = new Intent(getApplicationContext(), Appartement.class);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_covoiturage) {
            Intent searchIntent = new Intent(getApplicationContext(), Covoiturage.class);
            searchIntent.putExtra("nbNotif", nbNotif);
            startActivity(searchIntent);
            finish();
        } else if (id == R.id.nav_calendrier) {
            Intent searchIntent = new Intent(getApplicationContext(), CalendarExtra.class);
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

    /**********************************************************
    * Scripts concernant l'affichage de la liste des articles *
    ***********************************************************/

    class ReadJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
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
                        String cat_article  = a.getString("LIBELLE_CATEGORIE");
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

                        // AgoTime: récupération de la date de publication et actuelle
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date mDate = sdf.parse(date_article);
                            timeInMilliseconds = mDate.getTime();
                            System.out.println("Date in milli :: " + timeInMilliseconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long time= System.currentTimeMillis();
                        // Fonction pour utiliser agoTime
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
                                    R.string.problemeServeur,
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
            dismissProgressDialog();
            ArticleAdapter adapter = new ArticleAdapter(getApplicationContext(), R.layout.row_list, arrayList);
            if(lv.getAdapter()==null)
                lv.setAdapter(adapter);
            else{
                lv.setAdapter(adapter);
                lv.setSelectionFromTop(index, top);
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
            showProgressDialog();
            super.onPreExecute();
        }

        @Override

        protected void onPostExecute(String result) {
            dismissProgressDialog();
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if (result.equals("") || result == null) {
                return;
            }

            int jsonResult = MainActivity.returnParsedJsonObject(result);
            if (jsonResult == 0) {
                return;
            }
            nbNotif = Integer.toString(jsonResult);

            notifcovoit.setGravity(Gravity.CENTER_VERTICAL);
            notifcovoit.setTypeface(null, Typeface.BOLD);
            notifcovoit.setTextColor(getResources().getColor(R.color.colorRed));
            notifcovoit.setText(nbNotif);

        }
    }
    public static int returnParsedJsonObject(String result){
        JSONObject resultObject = null;
        int returnedResult = 0;
        try {
            resultObject = new JSONObject(result);
            returnedResult = resultObject.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResult;
    }


        private void showProgressDialog() {
            if (pDialog == null) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Chargement...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
            }
            pDialog.show();
        }

        private void dismissProgressDialog() {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
}
package owlinone.pae.appartement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.calendrier.CalendarExtra;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.covoiturage.Covoiturage;
import owlinone.pae.divers.APropos;
import owlinone.pae.divers.Bug;
import owlinone.pae.main.MainActivity;
import owlinone.pae.session.Compte;
import owlinone.pae.session.Session;


public class Appartement extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Déclaration des variables
    Session session;
    ArrayList<Appart> arrayListAppart;

    private String TAG = Appartement.class.getSimpleName();
    private ListView lv;
    String url= null;
    String email, name, photoBDD;
    String strDetail = "", strDetailTel = "", strNomPropDetail = "", strLongitude = "", strLatitude = "" , strDetailAppart = "", strImagePrinc="", strImageSecond="";
    String strMail = "", strAdresse = "", strCommentaire = "RAS", strVille = "", strPrix = "", strDispoContext = "";
    String strNomContext = "", strIdContext = "", disponible = "Disponible", nonDisponible = "Non disponible";

    HashMap <String, String> obj = new HashMap();
    HashMap <String, String> objDispo = new HashMap();

    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView notifcovoit;
    private String nbNotif;
    private ProgressDialog pDialog;
    private String nameEnvoi;
    private int index;
    private int top;

    //Redémarre l'activité
    private void restartActivity()
    {
        Intent intent = getIntent();
        intent.putExtra("url",url);
        startActivity(intent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(Appart appart) {
        Toast.makeText(this, appart.getStrNom(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.Trierpar:
                return true;
            case R.id.TrierDispo:
                url = AddressUrl.strTriAppartDispo ;
                restartActivity();
                return true;
            case R.id.TrierPrix:
                url = AddressUrl.strTriAppartPrix;
                restartActivity();
                return true;
            case R.id.TrierDeux:
                url = AddressUrl.strTriAppartPrixDispo;
                restartActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showProgressDialog();
        // Affiche le contenu de l'activté sélectionnée
        setContentView(R.layout.activity_appartement);

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
        navigationView.setCheckedItem(R.id.nav_appartement);

        // User Session Manager
        session = new Session(getApplicationContext());
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get name
        name = user.get(Session.KEY_NAME);
        nameEnvoi = name.replace("'", "''");

        // get email
        email = user.get(Session.KEY_EMAIL);
        // get base 64 photo code from BDD
        photoBDD = user.get(Session.KEY_PHOTO);

        notifcovoit = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
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

        // Glisser le doigt pour rafraichir----------------------------------------------------------
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.appartement_activity_swipe_refresh_layout);
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
                        arrayListAppart = new ArrayList<Appart>();
                        lv = (ListView) findViewById(R.id.list);
                        new GetApparts().execute();

                    }
                }, 2000);
            }
        });

        // Bouton pour ajouter un appartement--------------------------------------------------------
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.buttonAddAppart);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentAppart = new Intent(getApplicationContext(), AddApart.class);
                startActivity(intentAppart);
            }
        });

        arrayListAppart = new ArrayList<Appart>();
        lv         = (ListView) findViewById(R.id.list);
        new GetApparts().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Appart appartSelected =   arrayListAppart.get(position);
                Intent intentAppart = new Intent(Appartement.this, DetailAppart.class);

                index = lv.getFirstVisiblePosition();
                View v = lv.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

                // Récupérer les string pour l'INTENT. Utilisation dans la classe DetailArticle-----
                intentAppart.putExtra("strDetail",appartSelected.getStrDetail());
                intentAppart.putExtra("strDetailTel",appartSelected.getStrTel());
                intentAppart.putExtra("strLongitude",appartSelected.getLongitude());
                intentAppart.putExtra("strLatitude",appartSelected.getLatitude());
                intentAppart.putExtra("strNomPropDetail",appartSelected.getStrNom());
                intentAppart.putExtra("strMail",appartSelected.getStrMail());
                intentAppart.putExtra("strAdresse",appartSelected.getStrAdresse());
                intentAppart.putExtra("strVille",appartSelected.getStrVille());
                intentAppart.putExtra("strPrix",appartSelected.getPrix());
                intentAppart.putExtra("strDispoContext",appartSelected.getStrDispo());
                intentAppart.putExtra("strDetailAppart",appartSelected.getStrDetail());
                intentAppart.putExtra("strId",appartSelected.getStrID());
                Log.e(TAG, "strId: " + appartSelected.getStrID());

                startActivity(intentAppart);
            }
        });

        // Sert pour l'appuie long (Entregistre le context du menu)
        registerForContextMenu(lv);
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
        /*} else if (id == R.id.nav_appartement) {
            Intent searchIntent = new Intent(getApplicationContext(), Appartement.class);
            startActivity(searchIntent);
            finish();*/
        } else if (id == R.id.nav_covoiturage) {
            Intent searchIntent = new Intent(getApplicationContext(), Covoiturage.class);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.list)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int index = info.position;
            // On récupère l'objet de l'appartement à l'aide de la position de l'item appuyé
            final Appart appartSelected =   arrayListAppart.get(index);
            //Récupération des données par HASHMAP
            strDispoContext = appartSelected.getStrDispo();
            strNomContext   = appartSelected.getStrNom();
            strIdContext    = String.valueOf(appartSelected.getStrID());
            menu.setHeaderTitle(strNomContext);

            if(disponible.equals(strDispoContext)) // Si l'appart est disponible on affiche ceci
            {
                menu.add(0, v.getId(), 0, "Non disponible");
                menu.add(0, v.getId(), 0, "Vendu");
                menu.add(0, v.getId(), 0, "Signaler");
            }
            else if (nonDisponible.equals(strDispoContext)) // Si l'appart est indisponible on affiche ceci
            {
                menu.add(0, v.getId(), 0, "Disponible");
                menu.add(0, v.getId(), 0, "Vendu");
                menu.add(0, v.getId(), 0, "Signaler");
            }
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        strDispoContext = (String)item.getTitle();

        if("Signaler".equals(item.getTitle()))
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Appartement.this);
            alertDialog.setTitle("Signaler");
            alertDialog.setMessage("Commentaire:");

            final EditText input = new EditText(Appartement.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.alarm);
            alertDialog.setPositiveButton("VALIDER",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            strCommentaire = input.getText().toString();

                                new sendDispo(getApplicationContext(), strCommentaire).execute();
                                restartActivity();
                        }

                    });

            alertDialog.setNegativeButton("ANNULER",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
        else
        {
            new sendDispo(this, strCommentaire).execute();
            restartActivity();
        }

        return true;
    }

    private class GetApparts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            showProgressDialog();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();
            Intent intent = getIntent();
            String url = intent.getStringExtra("url");
            String jsonStr = null;

            if(getIntent().getSerializableExtra("url")==null)
            {
                jsonStr = sh.makeServiceCall(AddressUrl.strConnexionAppartement);
            }
            else  { jsonStr = sh.makeServiceCall(url); }
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    // looping through All Appartements
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject a              = jsonArray.getJSONObject(i);
                        int id_appart             = a.getInt("ID_APPART");
                        String strId_appart     = String.valueOf(id_appart);
                        String nom_prop           = a.getString("NOM_PROP");
                        String adresse_appart     = a.getString("ADRESSE_APPART");
                        String ville_appart       = a.getString("VILLE_APPART");
                        String descrip_appart     = a.getString("DESCRIP_APPART");
                        String detail_appart      = a.getString("DETAIL_APPART");
                        String telephone_prop     = a.getString("TELEPHONE_PROP");
                        int prix_appart           = a.getInt("PRIX_APPART");
                        String dispo_appart       = a.getString("DISPO_APPART");
                        int cp_appart             = a.getInt("CP_APPART");
                        Double longitudeAppart    = a.getDouble("LONGITUDE_APPART");
                        Double latitudeAppart     = a.getDouble("LATITUDE_APPART");
                        String strPrix_appart     = String.valueOf(prix_appart) + " ";
                        String strCp_appart       = String.valueOf(cp_appart) + " ";
                        String adresseMail        = a.getString("ADRESSE_MAIL");
                        String strImage_princ     = a.getString("IMAGE_PRINCIPALE");
                        String strValidation      = a.getString("VALIDATION");
                        Log.e(TAG, "strValidation: " + strValidation);

                        // Affiche les appartements que s'il est disponible ou non disponible
                        if((dispo_appart.equals("Disponible") || dispo_appart.equals("Non disponible")) && ("TRUE".equals(strValidation)))
                        {
                                Appart appartement = new Appart();
                                appartement.setStrID(strId_appart);
                                appartement.setStrNom(nom_prop);
                                appartement.setStrAdresse(adresse_appart);
                                appartement.setStrVille(ville_appart);
                                appartement.setStrDescript(descrip_appart);
                                appartement.setStrDetail(detail_appart);
                                appartement.setStrTel(telephone_prop);
                                appartement.setPrix(strPrix_appart);
                                appartement.setStrDispo(dispo_appart);
                                appartement.setStrCp(strCp_appart);
                                appartement.setLongitude(longitudeAppart);
                                appartement.setLatitude(latitudeAppart);
                                appartement.setStrMail(adresseMail);
                                appartement.setStrImagePrinc(strImage_princ);

                                // adding contact to contact list
                                arrayListAppart.add(appartement);
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
                                R.string.problemeServeur,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            dismissProgressDialog();
            super.onPostExecute(result);
            AppartementAdapter adapter = new AppartementAdapter(getApplicationContext(), R.layout.content_appartement, arrayListAppart);
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

    private class sendDispo extends AsyncTask<Void, Void, Void> {
        Exception exception;
        String comSend = null;
        private Context context;
        public sendDispo(Context context, String com) {
            comSend = com;
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersDispo = new HashMap<>();
                String urlDispo = AddressUrl.strModifierDispo;
                parametersDispo.put("ID_APPART", strIdContext);
                parametersDispo.put("DISPO_APPART", strDispoContext);
                parametersDispo.put("COM_SIGNALER", strCommentaire);
                sh.performPostCall(urlDispo, parametersDispo);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }
    private void showProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(Appartement.this);
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
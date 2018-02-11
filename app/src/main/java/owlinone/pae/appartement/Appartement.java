package owlinone.pae.appartement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.main.MainActivity;
import owlinone.pae.R;

public class Appartement extends AppCompatActivity
{
    private String TAG = Appartement.class.getSimpleName();
    private ListView lv;
    String url= null;
    String strDetail = "", strDetailTel = "", strNomPropDetail = "", strLongitude = "", strLatitude = "";
    String strMail = "", strAdresse = "", strCommentaire = "RAS", strVille = "", strPrix = "", strDispoContext = "";
    String strNomContext = "", strIdContext = "", disponible = "Disponible", nonDisponible = "Non disponible";

    HashMap <String, String> obj = new HashMap();
    HashMap <String, String> objDispo = new HashMap();
    ArrayList<HashMap<String, String>> appartList;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appartement_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Affichage de la flèche de retour-----------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Appartement.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Glisser du doigt pour rafraichir----------------------------------------------------------
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
                        appartList = new ArrayList<>();
                        lv = (ListView) findViewById(R.id.list);
                        new GetApparts().execute();

                    }
                }, 3000);
            }
        });

        //Bouton pour ajouter un appartement--------------------------------------------------------
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.buttonAddAppart);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentAppart = new Intent(getApplicationContext(), AddApart.class);
                startActivity(intentAppart);
            }
        });

        appartList = new ArrayList<>();
        lv         = (ListView) findViewById(R.id.list);
        new GetApparts().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                obj              = (HashMap)lv.getItemAtPosition(position);
                strDetail        = obj.get("DETAIL_APPART");
                strNomPropDetail = obj.get("NOM_PROP");
                strDetailTel     = obj.get("TELEPHONE_PROP");
                strLongitude     = obj.get("LONGITUDE_APPART");
                strLatitude      = obj.get("LATITUDE_APPART");
                strMail          = obj.get("ADRESSE_MAIL");
                strPrix          = obj.get("PRIX_APPART");
                strAdresse       = obj.get("ADRESSE_APPART");
                strVille         = obj.get("VILLE_APPART");
                strDispoContext  = obj.get("DISPO_APPART");

                //On récupère les valeurs et utilisont INTENT pour l'enregistrer pour la prochaine activité
                Intent intentAppart = new Intent(getApplicationContext(), DetailAppart.class);
                intentAppart.putExtra("strDetail",strDetail);
                intentAppart.putExtra("strDetailTel",strDetailTel);
                intentAppart.putExtra("strLongitude",strLongitude);
                intentAppart.putExtra("strLatitude",strLatitude);
                intentAppart.putExtra("strNomPropDetail",strNomPropDetail);
                intentAppart.putExtra("strMail",strMail);
                intentAppart.putExtra("strAdresse",strAdresse);
                intentAppart.putExtra("strVille",strVille);
                intentAppart.putExtra("strPrix",strPrix);
                intentAppart.putExtra("strDispoContext",strDispoContext);
                startActivity(intentAppart);
            }
        });

        //Sert pour l'appuie long (Entregistre le context du menu
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.list)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int index = info.position;
            //On récupère l'objet de l'appartement à l'aide de la position de l'item appuyé
            objDispo  = (HashMap)lv.getItemAtPosition(index);
            //Récupération des données par HASHMAP
            strDispoContext = objDispo.get("DISPO_APPART");
            strNomContext   = objDispo.get("NOM_PROP");
            strIdContext    = objDispo.get("ID_APPART");
            menu.setHeaderTitle(strNomContext);

            if(disponible.equals(strDispoContext)) //Si l'appart il est disponible on affiche ceci
            {
                menu.add(0, v.getId(), 0, "Non disponible");
                menu.add(0, v.getId(), 0, "Vendu");
                menu.add(0, v.getId(), 0, "Signaler");
            }
            else if (nonDisponible.equals(strDispoContext)) //Si l'appart il est indisponible on affiche ceci
            {
                menu.add(0, v.getId(), 0, "Disponible");
                menu.add(0, v.getId(), 0, "Vendu");
                menu.add(0, v.getId(), 0, "Signaler");
            }
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        obj = (HashMap)lv.getItemAtPosition(index);
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
                        JSONObject a           = jsonArray.getJSONObject(i);
                        int id_appart          = a.getInt("ID_APPART");
                        String nom_prop        = a.getString("NOM_PROP");
                        String adresse_appart  = a.getString("ADRESSE_APPART");
                        String ville_appart    = a.getString("VILLE_APPART");
                        String descrip_appart  = a.getString("DESCRIP_APPART");
                        String detail_appart   = a.getString("DETAIL_APPART");
                        String telephone_prop  = a.getString("TELEPHONE_PROP");
                        int prix_appart        = a.getInt("PRIX_APPART");
                        String dispo_appart    = a.getString("DISPO_APPART");
                        int cp_appart          = a.getInt("CP_APPART");
                        Double longitudeAppart = a.getDouble("LONGITUDE_APPART");
                        Double latitudeAppart  = a.getDouble("LATITUDE_APPART");
                        String strCp_appart    = String.valueOf(cp_appart) + " ";
                        String adresseMail     = a.getString("ADRESSE_MAIL");

                        // Affiche les appartements que s'il est disponible ou non disponible
                        if(dispo_appart.equals("Disponible") || dispo_appart.equals("Non disponible") )
                        {
                            HashMap<String, String> appartement = new HashMap<>();
                            // adding each child node to HashMap key => value
                            appartement.put("ID_APPART", String.valueOf(id_appart));
                            appartement.put("NOM_PROP", nom_prop);
                            appartement.put("ADRESSE_APPART", adresse_appart);
                            appartement.put("VILLE_APPART", ville_appart);
                            appartement.put("DESCRIP_APPART", descrip_appart);
                            appartement.put("DETAIL_APPART", detail_appart);
                            appartement.put("TELEPHONE_PROP", telephone_prop);
                            appartement.put("PRIX_APPART", String.valueOf(prix_appart));
                            appartement.put("DISPO_APPART", dispo_appart);
                            appartement.put("CP_APPART", strCp_appart);
                            appartement.put("LONGITUDE_APPART", String.valueOf(longitudeAppart));
                            appartement.put("LATITUDE_APPART", String.valueOf(latitudeAppart));
                            appartement.put("ADRESSE_MAIL", adresseMail);
                            // adding contact to contact list
                            appartList.add(appartement);
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
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(Appartement.this, appartList,
                    R.layout.list_appart, new String[]{ "DESCRIP_APPART","PRIX_APPART","ADRESSE_APPART","CP_APPART","VILLE_APPART","DISPO_APPART","NOM_PROP"},
                    new int[]{R.id.descrip_appart,R.id.prix_appart, R.id.adresse_apart,R.id.cp_appart,R.id.ville_apart,R.id.dispo_appart,R.id.nom_prop});
            lv.setAdapter(adapter);
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
}






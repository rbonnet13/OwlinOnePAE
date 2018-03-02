package owlinone.pae.password;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HideKeyboard;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.session.MainLogin;

/**
 * Created by Julian on 15/01/2018.
 */

public class PasswordReset extends AppCompatActivity {
    private EditText email;
    private String url = AddressUrl.strEmailRecup;
    private String username= null;
    private String enteredEmail= null;
    private Boolean mauvaisMail= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le contenu de l'activté sélectionnée
        setContentView(R.layout.password_reset);
        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.setupUI(findViewById(R.id.layout_reset));

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar15);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mauvaisMail = false;
            } else {
                mauvaisMail = extras.getBoolean("mauvaisMail");
            }
        } else {
            mauvaisMail = (Boolean) savedInstanceState.getSerializable("mauvaisMail");
        }
        // Si la personne a mis un mail qui n'existe pas
        if(mauvaisMail == true){
            Toast.makeText(PasswordReset.this, "Cet email est inconnu", Toast.LENGTH_LONG).show();
        }
        // Déclarations ID
        email = (EditText) findViewById(R.id.emailReset);
        Button validation = (Button) findViewById(R.id.btn_reset);

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredEmail = email.getText().toString();

                String expression = "^[\\w\\.-]+@esaip.org"; //Regex pour savoir si le mail est de bonne forme
                CharSequence inputStr = enteredEmail;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);
                if (!matcher.matches()) {
                    Toast.makeText(PasswordReset.this, "Cet email est inconnu", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    new GetMail().execute();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.actionsettings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetMail extends AsyncTask<Void, Void, Void>
    {
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
                parameters.put("MDP_OUBLIE", enteredEmail);

                responseRequete = sh.performPostCall(url, parameters);

                Log.e("URL", "url: " + url);
                System.out.println("Mail : " + enteredEmail);

                JSONArray jsonArray = new JSONArray(responseRequete);
                JSONObject a = jsonArray.getJSONObject(0);
                 username = a.getString("username");
                Log.e("username", "username: " + username);

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
            final Context context = getApplicationContext();
            if(username != null) {
                Intent intent = new Intent(context, PasswordActivity.class);
                Log.e("enteredEmail", "enteredEmail: " + enteredEmail);
                intent.putExtra("email", enteredEmail);
                intent.putExtra("username", username);
                startActivity(intent);
            }
            else{
                Intent intent = getIntent();
                intent.putExtra("mauvaisMail", true);
                startActivity(intent);

            }
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}








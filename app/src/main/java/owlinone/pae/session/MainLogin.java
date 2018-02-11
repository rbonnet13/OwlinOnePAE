package owlinone.pae.session;

/**
 * Created by rudy on 22/11/2017.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;


public class MainLogin extends AppCompatActivity {

    protected EditText username;
    private EditText password;
    private EditText mdp;
    protected String enteredUsername;
    protected String enteredPassword;
    protected String enteredPhoto;

    private final String serverUrl = AddressUrl.strTriIndex;

    private Session session;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_view);
        // User Session Manager
        session = new Session(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        //Affichage de la flèche de retour-----------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        username = (EditText) findViewById(R.id.username_field);
        password = (EditText) findViewById(R.id.password_field);
        TextView mdpValiation = (TextView) findViewById(R.id.mpd_oublie);


        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_LONG).show();

        Button loginButton = (Button) findViewById(R.id.login);
        Button registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                enteredUsername = username.getText().toString();
                enteredPassword = password.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(MainLogin.this, "Pseudo et mot de passe requis", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredUsername.length() <= 1 || enteredPassword.length() <= 1) {
                    Toast.makeText(MainLogin.this, "Le pseudo et le login doivent dépasser 1 caractère", Toast.LENGTH_LONG).show();
                    return;
                }
// request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, enteredUsername, enteredPassword);
            }

        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLogin.this, RegisterActivity.class);
                startActivity(intent);
            }

        });

        mdpValiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLogin.this, PasswordReset.class);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(MainLogin.this, "Connexion obligatoire", Toast.LENGTH_LONG).show();
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.


        return true;

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

    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);
            String jsonResult = "";

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", params[1]));
                nameValuePairs.add(new BasicNameValuePair("password", params[2]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return jsonResult;

        }

        @Override

        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if (result.equals("") || result == null) {
                Toast.makeText(MainLogin.this, "Problème de connexion au serveur", Toast.LENGTH_LONG).show();
                return;
            }

            int jsonResult = returnParsedJsonObject(result);
            if (jsonResult == 0) {
                Toast.makeText(MainLogin.this, "Mot de passe ou email incorrect", Toast.LENGTH_LONG).show();
                return;
            }

            if (jsonResult == 1) {
                String jsonresultImg = returnParsedJsonObjectImg(result);
                String jsonresultEmail = returnParsedJsonObjectEmail(result);

                Intent intent = new Intent(MainLogin.this, LoginActivity.class);
                intent.putExtra("USERNAME", enteredUsername);
                intent.putExtra("MESSAGE", "Connexion réussie");
                session.createUserLoginSession(enteredUsername,null,null,null,null,null,null, null,jsonresultEmail,null,enteredPassword,jsonresultImg);
                Toast.makeText(MainLogin.this, "Pseudo = "+enteredUsername, Toast.LENGTH_LONG).show();

                startActivity(intent);
                finish();
            }
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }

    private int returnParsedJsonObject(String result){
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
    private String returnParsedJsonObjectImg (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectEmail (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
}
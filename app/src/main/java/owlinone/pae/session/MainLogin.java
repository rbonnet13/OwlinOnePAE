package owlinone.pae.session;

/**
 * Created by rudy on 22/11/2017.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.SecretPassword;
import owlinone.pae.password.PasswordReset;

import static owlinone.pae.configuration.SecretPassword.generateKey;


public class MainLogin extends AppCompatActivity {

    // Déclaration des variables
    protected EditText username;
    private EditText password;
    protected String enteredUsername;
    protected String enteredPassword;
    protected String passwordEncrypted;
    private final String serverUrl = AddressUrl.strTriIndex;
    private Session session;
    private SecretKey secret = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche la vue du login
        setContentView(R.layout.login_main_view);
        setupUI(findViewById(R.id.layout_login));

        // User Session Manager
        session = new Session(getApplicationContext());

        // Affichage la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        username = (EditText) findViewById(R.id.username_field);
        password = (EditText) findViewById(R.id.password_field);
        TextView mdpValiation = (TextView) findViewById(R.id.mpd_oublie);

        final Button loginButton = (Button) findViewById(R.id.login);
        Button registerButton = (Button) findViewById(R.id.register_button);

        // Lorsque appui sur la touche Enter du clavier à la fin du MDP, simule un clic sur loginButton
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                enteredUsername = username.getText().toString();
                enteredPassword = password.getText().toString();
                try {
                    secret = generateKey();
                    passwordEncrypted = SecretPassword.encryptMsg(enteredPassword, secret);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InvalidParameterSpecException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }


                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(MainLogin.this, "Pseudo et mot de passe requis", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredUsername.length() <= 3 || enteredPassword.length() <= 3) {
                    Toast.makeText(MainLogin.this, "Le pseudo et le mot de passe doivent dépasser 3 caractères", Toast.LENGTH_LONG).show();
                    return;
                }
// request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                Log.e("enteredUsername", "enteredUsername: " + enteredUsername);
                Log.e("passwordEncrypted", "passwordEncrypted: " + passwordEncrypted);

                asyncRequestObject.execute(serverUrl, enteredUsername, passwordEncrypted);
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
                Toast.makeText(MainLogin.this, "Pseudo ou Mot de passe incorrect", Toast.LENGTH_LONG).show();
                return;
            }

            if (jsonResult == 1) {

                String jsonresultImg = returnParsedJsonObjectImg(result);
                String jsonresultEmail = returnParsedJsonObjectEmail(result);
                String jsonresultPrenom = returnParsedJsonObjectPrenom(result);
                String jsonresultNom = returnParsedJsonObjectNom(result);
                String jsonresultVille = returnParsedJsonObjectVille(result);
                String jsonresultAdresse = returnParsedJsonObjectAdresse(result);
                String jsonresultLatitude = returnParsedJsonObjectLat(result);
                String jsonresultLongitude = returnParsedJsonObjectLong(result);
                String jsonresultTelephone = returnParsedJsonObjectTelephone(result);
                String jsonresultCP = returnParsedJsonObjectCP(result);

                if( jsonresultPrenom.equals("null")){
                     jsonresultPrenom = null;
                }
                if (jsonresultNom.equals("null")){
                    jsonresultNom = null;
                }
                if (jsonresultAdresse.equals("null")){
                    jsonresultAdresse = null;
                }
                if (jsonresultVille.equals("null")){
                    jsonresultVille = null;
                }
                if (jsonresultCP.equals("null")){
                    jsonresultCP = null;
                }
                if(jsonresultTelephone.equals("null")){
                    jsonresultTelephone = null;
                }

                Intent intent = new Intent(MainLogin.this, LoginActivity.class);
                intent.putExtra("USERNAME", enteredUsername);
                intent.putExtra("MESSAGE", "Connexion réussie");

                session.createUserLoginSession(enteredUsername,jsonresultPrenom,jsonresultNom,jsonresultVille,jsonresultAdresse,jsonresultLatitude,jsonresultLongitude, jsonresultCP,jsonresultEmail,jsonresultTelephone,passwordEncrypted,jsonresultImg,"true");
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
        String returnedResultImg2 = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("IMAGE_USER");

            // A OPTIMISER: Le JSONObject place des \ et des \n dans le code Base64. On les supprime
            returnedResultImg2 = returnedResultImg.replaceAll("\\\\","");
            returnedResultImg = returnedResultImg2.replaceAll("\\n","");
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
    private String returnParsedJsonObjectNom (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("NOM");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectPrenom (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("PRENOM");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectAdresse (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("ADRESSE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectVille (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("VILLE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectCP (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("CP");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectLat (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("LATITUDE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectLong (String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("LONGITUDE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    private String returnParsedJsonObjectTelephone(String result){
        JSONObject resultObject = null;
        String returnedResultImg = "";
        try {
            resultObject = new JSONObject(result);
            returnedResultImg = resultObject.getJSONArray("user").getJSONObject(0).getString("TELEPHONE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResultImg;
    }
    // Les deux méthodes suivantes permettent de cacher le clavier quand on clique autrpart
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainLogin.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Connexion obligatoire")
                .setIcon(R.drawable.owl_in_one_logo)
                .setMessage("Êtes-vous sûr de vouloir quitter?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }
}
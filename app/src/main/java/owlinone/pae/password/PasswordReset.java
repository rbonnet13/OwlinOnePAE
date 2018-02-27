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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.configuration.SecretPassword;

import static owlinone.pae.configuration.SecretPassword.generateKey;

/**
 * Created by Julian on 15/01/2018.
 */

public class PasswordReset extends AppCompatActivity {
    private EditText email;
    private String url = AddressUrl.strEmailRecup;
    private String password= null;
    private String passwordFinal= null;
    private String enteredEmail= null;
    private SecretKey secret = null;
    byte[] passByte =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
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
                secret = generateKey();

                HashMap<String, String> parameters = new HashMap<>();
                HttpHandler sh = new HttpHandler();
                parameters.put("MDP_OUBLIE", enteredEmail);

                responseRequete = sh.performPostCall(url, parameters);

                Log.e("URL", "url: " + url);
                System.out.println("Mail : " + enteredEmail);

                JSONArray jsonArray = new JSONArray(responseRequete);
                JSONObject a = jsonArray.getJSONObject(0);
                 password = a.getString("password");


                passwordFinal = SecretPassword.decryptMsg(password, secret);


                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidParameterSpecException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            final Context context = getApplicationContext();

            Intent intent = new Intent(context, PasswordActivity.class);
            Log.e("enteredEmail", "enteredEmail: " + enteredEmail);
            Log.e("password", "password: " + passwordFinal);
            intent.putExtra("email",enteredEmail );
            intent.putExtra("password", passwordFinal );
            startActivity(intent);
        }
    }

}








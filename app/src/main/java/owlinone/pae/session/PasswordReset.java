package owlinone.pae.session;

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
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;

/**
 * Created by Julian on 15/01/2018.
 */

public class PasswordReset extends AppCompatActivity {
    private EditText email;
    //private final String serverUrl = AddressUrl.StrIndexMDP;

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
                String enteredEmail = email.getText().toString();
                String expression = "^[\\w\\.-]+@esaip.org"; //Regex pour savoir si le mail est de bonne forme
                CharSequence inputStr = enteredEmail;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);
                if (!matcher.matches()) {
                    Toast.makeText(PasswordReset.this, "Cet email est inconnu", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(PasswordReset.this, ResetPasswordFragment.class);
                    startActivity(intent);
                }

// request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                //asyncRequestObject.execute(serverUrl, enteredEmail);
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
                // nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                //jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                System.out.println("Returned Json object " + jsonResult.toString());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }
    }
}








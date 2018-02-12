package owlinone.pae.session;

/**
 * Created by rudy on 22/11/2017.
 */

import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import android.view.MenuItem;

import android.widget.TextView;

import owlinone.pae.main.MainActivity;
import owlinone.pae.R;

public class LoginActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        int TIME_OUT = 1500;
        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        String loggedUser = intentBundle.getString("USERNAME");
        loggedUser = capitalizeFirstCharacter(loggedUser);
        String message = intentBundle.getString("MESSAGE");

        TextView loginUsername = (TextView)findViewById(R.id.login_user);
        TextView successMessage = (TextView)findViewById(R.id.message);
        loginUsername.setText(loggedUser);
        successMessage.setText(message);

        if(message.equals("Connexion réussie")){
            System.out.println("success message : " + successMessage.getText());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent u = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(u);
                    finish();
                }
            }, TIME_OUT);
        }else  if (message.equals("Inscription réussie")){
            System.out.println("success message : " + successMessage.getText());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(LoginActivity.this, MainLogin.class);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);
        }
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

    private String capitalizeFirstCharacter(String textInput){
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;

    }
}
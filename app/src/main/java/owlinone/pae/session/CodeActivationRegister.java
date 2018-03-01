package owlinone.pae.session;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ThreadLocalRandom;

import owlinone.pae.R;
import owlinone.pae.configuration.GMailSender;

/**
 * Created by AnthonyCOPPIN on 28/02/2018.
 */

public class CodeActivationRegister extends AppCompatActivity {
    private EditText codeActivation;
    private String enteredCode;
    private String enteredMail;
    private String enteredUsername;

    private int min = 1000;
    private int max = 9998;
    private int randomNum;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
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
        randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                enteredUsername = null;
            } else {
                enteredUsername = extras.getString("USERNAME");
            }
        } else {
            enteredUsername = (String) savedInstanceState.getSerializable("USERNAME");
        }


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                enteredMail = null;
            } else {
                enteredMail = extras.getString("MAIL");
            }
        } else {
            enteredMail = (String) savedInstanceState.getSerializable("MAIL");
        }
        //Envoi mail par gmail
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("owlinone.esaip@gmail.com",
                            "AIzaSyCyZbnFvalPGR9h1aJZJel8_7VtcDfCmPc");
                    sender.sendMail("Activation compte OwlIneOne", " Salut "+ enteredUsername+", \n Ton code pour activer ton compte: " + randomNum + ". \n Retourne sur l'application pour l'activer. \n L'équipe OwlInOne,",
                            "owlinone.esaip@gmail.com", enteredMail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();

        // Déclarations ID
        codeActivation = (EditText) findViewById(R.id.code_activation);
        Button validation = (Button) findViewById(R.id.btn_activation);
        randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredCode = codeActivation.getText().toString();
                Log.e("enteredCode", "enteredCode: " + enteredCode);
                Log.e("randomNum", "randomNum: " + String.valueOf(randomNum));


                if (!enteredCode.equals(String.valueOf(randomNum))) {
                    Toast.makeText(CodeActivationRegister.this, "Code faux", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(CodeActivationRegister.this, LoginActivity.class);
                    intent.putExtra("USERNAME", enteredUsername);
                    intent.putExtra("MESSAGE", "Inscription réussie");
                    startActivity(intent);
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
}


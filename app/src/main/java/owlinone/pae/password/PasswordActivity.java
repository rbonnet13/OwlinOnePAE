package owlinone.pae.password;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.SecretKey;

import owlinone.pae.R;
import owlinone.pae.configuration.*;
import owlinone.pae.session.MainLogin;

import static owlinone.pae.configuration.SecretPassword.generateKey;

public class PasswordActivity extends AppCompatActivity {

    String mailRecup     = null;
    String username = null ;
    EditText editReNewPassword     = null;
    EditText editNewPassword = null ;
    EditText editCode = null ;
    String newPassword = null ;
    String reNewPassword = null ;
    String codePassword = null ;
    Button buttonValidate = null ;
    private String url = AddressUrl.strChangerMDP;
    int min = 1000;
    int max = 9998;
    int randomNum;
    SecretKey secret = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le contenu de l'activté sélectionnée
        setContentView(R.layout.activity_password);
        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.setupUI(findViewById(R.id.layout_password));

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
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
        editNewPassword = (EditText) findViewById(R.id.new_password_field);
        editReNewPassword = (EditText) findViewById(R.id.renew_password_field);
        editCode = (EditText) findViewById(R.id.code_field);
        buttonValidate = (Button) findViewById(R.id.sign_up_new_password);


        //Récupération des valeurs de la page précèdente
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mailRecup = null;
            } else {
                mailRecup = extras.getString("email");
            }
        } else {
            mailRecup = (String) savedInstanceState.getSerializable("email");
        }

         if (savedInstanceState == null) {
             Bundle extras = getIntent().getExtras();
             if (extras == null) {
                 username = null;
             } else {
                 username = extras.getString("username");
             }
         } else {
             username = (String) savedInstanceState.getSerializable("username");
         }

         // Création d'un nombre aléatoire à 4 chiffres
         randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
         Log.e("randomNum", "randomNum: "+ String.valueOf(randomNum));

        // Envoie du code à 4 chiffres par Gmail
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(GMailSender.mailOwlInOne,
                            GMailSender.mdpOwlInOne);
                    sender.sendMail("Owl In One - Code de vérification pour mot de passe oublié", "Bonjour "+username+",\n\nVous avez récemment fait une demande de mot de passe oublié sur notre application Owl In One.\nPour pouvoir modifier votre mot de passe, merci de rentrer ce code dans l'application: " + randomNum + ".\n\nL'équipe Owl In One.",
                            "owlinone.esaip@gmail.com", mailRecup);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
                                            
        }).start();

        buttonValidate.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override

            public void onClick(View v) {
                newPassword = editNewPassword.getText().toString();
                newPassword = newPassword.replace("'","''");
                reNewPassword = editReNewPassword.getText().toString();
                reNewPassword = reNewPassword.replace("'","''");
                codePassword = editCode.getText().toString();
                Log.e("randomNum", "randomNum: " + String.valueOf(randomNum));
                Log.e("codePassword", "codePassword: " + codePassword);

                if (newPassword.length() <= 3) {
                    Toast.makeText(PasswordActivity.this, R.string.mdpCourt, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!newPassword.equals(reNewPassword)) {
                    Toast.makeText(PasswordActivity.this, R.string.mdpDifferent, Toast.LENGTH_LONG).show();
                    return;
                }
                if (newPassword.equals("") || reNewPassword.equals("") || codePassword.equals("")) {
                    Toast.makeText(PasswordActivity.this, R.string.remplirToutChamps, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!codePassword.equals(String.valueOf(randomNum))) {
                    Toast.makeText(PasswordActivity.this, R.string.codeFaux, Toast.LENGTH_LONG).show();
                    return;
                }
                new ChangeMDP().execute();
            }
        });
    }

       private class ChangeMDP extends AsyncTask<Void, Void, Void>
    {
        String responseRequete= "";
        Exception exception;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            try
            {
                secret = generateKey();
                HashMap<String, String> parameters = new HashMap<>();
                HttpHandler sh = new HttpHandler();
                parameters.put("NEW_PASSWORD", SecretPassword.encryptMsg(newPassword, secret));
                parameters.put("MAIL", mailRecup);

                responseRequete = sh.performPostCall(url, parameters);
                return null;
            } catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Toast.makeText(PasswordActivity.this, R.string.mdpChange, Toast.LENGTH_LONG).show();

            super.onPostExecute(result);
            Intent intent = new Intent(getApplicationContext(), MainLogin.class);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PasswordReset.class);
        startActivity(intent);
        finish();
    }
}

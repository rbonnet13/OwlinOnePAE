package owlinone.pae.session;

/**
 * Created by rudy on 22/11/2017.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.GMailSender;
import owlinone.pae.configuration.HideKeyboard;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.configuration.SecretPassword;

import static owlinone.pae.configuration.SecretPassword.generateKey;


public class RegisterActivity extends AppCompatActivity {
    protected TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout rePassword;
    private TextInputLayout codeActivation;
    private TextInputLayout email;

    protected String enteredUsername;
    protected String enteredPassword;
    protected String enteredRePassword;
    protected String enteredEmail;
    protected String enteredPhoto;
    private String enteredCode;
    protected String response;
    protected String responseValidUserMail;

    private Bitmap bitmap;
    private ImageView imagePhoto;
    private int request_code = 1;


    private Button signUpButton;
    private Button validationCode;

    private int min = 1000;
    private int max = 9998;
    private int randomNum;

    private final String serverUrl = AddressUrl.strTriIndex;
    private final String urlActivationUserMail = AddressUrl.strActivValid;

    HttpHandler sh = new HttpHandler();
    SecretKey secret = null;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.setupUI(findViewById(R.id.layout_register));
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
        username = (TextInputLayout) findViewById(R.id.username_field_textInput);
        password = (TextInputLayout) findViewById(R.id.til_password_login_activity);
        rePassword = (TextInputLayout) findViewById(R.id.til_password_login_activity2);
        email = (TextInputLayout) findViewById(R.id.email_field_textInput);
        codeActivation = (TextInputLayout) findViewById(R.id.code_field_textInput);

        signUpButton = (Button) findViewById(R.id.sign_up);
        validationCode = (Button) findViewById(R.id.btn_activation);
        imagePhoto = (ImageView) findViewById(R.id.photo_user);

        codeActivation.setVisibility(View.INVISIBLE);
        validationCode.setVisibility(View.INVISIBLE);

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override

            public void onClick(View v) {
                enteredUsername = username.getEditText().getText().toString();
                enteredPassword = password.getEditText().getText().toString();
                enteredRePassword = rePassword.getEditText().getText().toString();
                enteredEmail = email.getEditText().getText().toString();
                enteredPhoto = convertirImgString(bitmap);
                randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

                new ValidUserMail().execute();
            }
        });
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                //verificacion de la version de plataforma
                if (Build.VERSION.SDK_INT < 19) {
                    //android 4.3  y anteriores
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    //android 4.4 y superior
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                }
                i.setType("image/*");
                startActivityForResult(i, request_code);
            }
        });

        validationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredCode = codeActivation.getEditText().getText().toString();
                Log.e("enteredCode", "enteredCode: " + enteredCode);
                Log.e("randomNum", "randomNum: " + String.valueOf(randomNum));


                if (!enteredCode.equals(String.valueOf(randomNum))) {
                    Toast.makeText(RegisterActivity.this, R.string.codeFaux, Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    new AsyncDataClass().execute();
                }
            }
        });

    }

    // Convertir image en string
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String convertirImgString(Bitmap bitmap) {
        String imagenString;
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        if(bitmap!=null){
            bitmap.compress(Bitmap.CompressFormat.JPEG,30,array);
            byte[] imagenByte=array.toByteArray();
            imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        }else{
            imagenString = "no image"; //se enviara este string en caso de no haber imagen
        }

        return imagenString;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == request_code){
            imagePhoto.setImageURI(data.getData());

            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imagePhoto.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class AsyncDataClass extends AsyncTask<Void, Void, Void> {
        Exception exception;
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                secret = generateKey();

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("username", enteredUsername);
                parameters.put("password", SecretPassword.encryptMsg(enteredPassword, secret));
                parameters.put("email", enteredEmail);
                parameters.put("photo", enteredPhoto);
                Log.e("username", "username: " + enteredUsername);
                Log.e("password", "password: " + SecretPassword.encryptMsg(enteredPassword, secret));
                Log.e("email", "email: " + enteredEmail);

                response = sh.performPostCall(serverUrl, parameters);

                return null;
            } catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + response);
            if(response.equals("") || response == null){
                Toast.makeText(RegisterActivity.this, R.string.problemeServeur, Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("USERNAME", enteredUsername);
            intent.putExtra("MESSAGE", "Inscription réussie");
            startActivity(intent);

        }
    }
    private class ValidUserMail extends AsyncTask<Void, Void, Void> {
        Exception exception;
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("USER", enteredUsername);
                parameters.put("MAIL", enteredEmail);
                responseValidUserMail = sh.performPostCall(urlActivationUserMail, parameters);

                return null;
            } catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (responseValidUserMail.contains("username")) {
                Toast.makeText(RegisterActivity.this, R.string.pseudoMdpUtilise, Toast.LENGTH_LONG).show();
                return;
            }
            Log.e("test", "test password: " + enteredPassword + " " + enteredRePassword);

            if (!enteredPassword.equals(enteredRePassword)) {
                Toast.makeText(RegisterActivity.this, R.string.mdpDifferent, Toast.LENGTH_LONG).show();
                return;
            }

            if (enteredUsername.equals("") || enteredPassword.equals("") || enteredEmail.equals("")) {
                Toast.makeText(RegisterActivity.this, R.string.pseudoMdpRequis, Toast.LENGTH_LONG).show();
                return;
            }

            if (enteredUsername.length() <= 3 || enteredPassword.length() <= 3) {
                Toast.makeText(RegisterActivity.this, R.string.pseudoMdpCaractere, Toast.LENGTH_LONG).show();
                return;
            }

            String expression = "^[\\w\\.-]+@esaip.org"; //Regex pour savoir si le mail est de bonne forme
            CharSequence inputStr = enteredEmail;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (!matcher.matches()) {
                Toast.makeText(RegisterActivity.this, R.string.mailFormat, Toast.LENGTH_LONG).show();
                return;
            }

            //Envoi mail par gmail
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender(GMailSender.mailOwlInOne,
                                GMailSender.mdpOwlInOne);
                        sender.sendMail("Owl In One - Code de vérification pour inscription", "Bonjour "+enteredUsername+",\n\nVous avez récemment fait une demande d'inscription sur notre application Owl In One.\nPour vérifier votre compte, merci de rentrer ce code dans l'application: "+ randomNum +".\n\nL'équipe Owl In One.",
                                "owlinone.esaip@gmail.com", enteredEmail);
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }

            }).start();
            Toast.makeText(RegisterActivity.this, R.string.codeNouveauMail, Toast.LENGTH_LONG).show();

            codeActivation.setVisibility(View.VISIBLE);
            validationCode.setVisibility(View.VISIBLE);

            password.setVisibility(View.INVISIBLE);
            username.setVisibility(View.INVISIBLE);
            password.setVisibility(View.INVISIBLE);
            rePassword.setVisibility(View.INVISIBLE);
            email.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
            imagePhoto.setEnabled(false);
        }
    }
}
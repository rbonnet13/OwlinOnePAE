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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.configuration.SecretPassword;

import static owlinone.pae.configuration.SecretPassword.generateKey;


public class RegisterActivity extends AppCompatActivity {
    protected EditText username;
    private EditText password;
    private EditText email;
    protected String enteredUsername;
    protected String enteredPassword;
    protected String enteredEmail;
    protected String enteredPhoto;
    protected String response;
    private Bitmap bitmap;
    private ImageView imagePhoto;
    private int request_code = 1;
    private final String serverUrl = AddressUrl.strTriIndex;
    HttpHandler sh = new HttpHandler();
    SecretKey secret = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        username = (EditText) findViewById(R.id.username_field);
        password = (EditText) findViewById(R.id.password_field);
        email = (EditText) findViewById(R.id.email_field);
        Button signUpButton = (Button) findViewById(R.id.sign_up);
        imagePhoto = (ImageView) findViewById(R.id.photo_user);


        signUpButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override

            public void onClick(View v) {
                enteredUsername = username.getText().toString();
                enteredPassword = password.getText().toString();
                enteredEmail = email.getText().toString();
                enteredPhoto = convertirImgString(bitmap);

                if (enteredUsername.equals("") || enteredPassword.equals("") || enteredEmail.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Pseudo et mot de passe requis", Toast.LENGTH_LONG).show();
                    return;
                }

                if (enteredUsername.length() <= 3 || enteredPassword.length() <= 3) {
                    Toast.makeText(RegisterActivity.this, "Le pseudo et le login doivent dépasser 3 caractères", Toast.LENGTH_LONG).show();
                    return;
                }

                String expression = "^[\\w\\.-]+@esaip.org"; //Regex pour savoir si le mail est de bonne forme
                CharSequence inputStr = enteredEmail;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);
                if (!matcher.matches()) {
                    Toast.makeText(RegisterActivity.this, "Votre email doit être sous la forme @esaip.org", Toast.LENGTH_LONG).show();
                    return;
                }

                // request authentication with remote server4
                new RegisterActivity.AsyncDataClass().execute();
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

        /*@Override
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
        }*/

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
                Toast.makeText(RegisterActivity.this, "Problème de connexion au serveur", Toast.LENGTH_LONG).show();
                return;
            }

            int jsonResult = sh.returnParsedJsonObject(response);
            if(jsonResult == 0){
                Toast.makeText(RegisterActivity.this, "Le pseudo ou l'email est déjà utilisé", Toast.LENGTH_LONG).show();
                Log.e("Username saisi:", enteredUsername);
                return;
            }

            if(jsonResult == 1){
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("USERNAME", enteredUsername);
                intent.putExtra("MESSAGE", "Inscription réussie");
                startActivity(intent);
            }
        }
    }

}
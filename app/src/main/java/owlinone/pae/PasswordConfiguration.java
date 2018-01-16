package owlinone.pae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Julian on 15/01/2018.
 */

public class PasswordConfiguration extends AppCompatActivity {

    private EditText newPassword;
    private EditText newPasswordConfirmation;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.password_conf);
       // Toolbar
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar7);
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

      /* newPassword = (EditText) findViewById(R.id.new_password);
       newPasswordConfirmation = (EditText) findViewById(R.id.new_password_confirmation);
       Button validation = (Button) findViewById(R.id.validation_password);

       validation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String newPasswordEntered = newPassword.getText().toString();
               String newPasswordConfirmationEntered = newPasswordConfirmation.getText().toString();
               if (newPasswordConfirmationEntered == newPasswordEntered){
                   Intent intent = new Intent(PasswordConfiguration.this, LoginActivity.class);
                   intent.putExtra("PASSWORD", newPasswordConfirmationEntered);
                   startActivity(intent);
               }
               else{
                   Toast.makeText(PasswordConfiguration.this, "Mot de passe incorrect", Toast.LENGTH_LONG).show();
                   return;
               }
           }

   });*/
   }
}

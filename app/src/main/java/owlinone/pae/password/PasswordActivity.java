package owlinone.pae.password;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import owlinone.pae.R;

public class PasswordActivity extends AppCompatActivity {
    String mailRecup     = null;
    String passwordRecup = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
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
                passwordRecup = null;
            } else {
                passwordRecup = extras.getString("password");
            }
        } else {
            passwordRecup = (String) savedInstanceState.getSerializable("password");
        }

        Log.e("mailRecup", "mailRecup: " + mailRecup);
        Log.e("passwordRecup", "passwordRecup: " + passwordRecup);

        Toast.makeText(PasswordActivity.this, "mail :"+ mailRecup, Toast.LENGTH_SHORT).show();
        Toast.makeText(PasswordActivity.this, "password :"+ passwordRecup, Toast.LENGTH_SHORT).show();
    }
}

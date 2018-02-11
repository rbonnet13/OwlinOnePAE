package owlinone.pae.session;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import owlinone.pae.R;
import owlinone.pae.configuration.RequestInterface;
import owlinone.pae.configuration.ServerRequest;
import owlinone.pae.configuration.ServerResponse;
import owlinone.pae.configuration.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static owlinone.pae.configuration.AddressUrl.strChemin;

/**
 * Created by Julian on 15/01/2018.
 */

public class PasswordModification extends AppCompatActivity{


    private EditText et_email,et_code,et_password;
    private TextView tv_timer;
    private ProgressBar progress;
    private boolean isResetInitiated = false;
    private String email;
    private CountDownTimer countDownTimer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_password_reset);

        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        tv_timer = (TextView)findViewById(R.id.cocotte).findViewById(R.id.tv_timer);
        et_code = (EditText)findViewById(R.id.cocotte).findViewById(R.id.et_code);
        et_email = (EditText)findViewById(R.id.cocotte).findViewById(R.id.et_email);
        et_password = (EditText)findViewById(R.id.cocotte).findViewById(R.id.et_password);
        et_password.setVisibility(View.GONE);
        et_code.setVisibility(View.GONE);
        tv_timer.setVisibility(View.GONE);
       // btn_reset.setOnClickListener(this);
        progress = (ProgressBar)findViewById(R.id.cocotte).findViewById(R.id.progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar16);
        setSupportActionBar(toolbar);

        //Affichage de la flèche de retour-----------------------------------
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


    }


    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_reset:

                if(!isResetInitiated) {
                    email = et_email.getText().toString();
                    if (!email.isEmpty()) {
                        initiateResetPasswordProcess(email);
                    } else {

                        Snackbar.make(findViewById(R.id.cocotte), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                    }
                } else {

                    String code = et_code.getText().toString();
                    String password = et_password.getText().toString();

                    if(!code.isEmpty() && !password.isEmpty()){

                        finishResetPasswordProcess(email,code,password);
                    } else {

                        Snackbar.make(findViewById(R.id.cocotte), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                    }

                }

                break;
        }
    }

    private void initiateResetPasswordProcess(String email){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(strChemin)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_INITIATE);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    //Button btn_reset = (Button) findViewById(R.id.btn_reset);
                    Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    et_email.setVisibility(View.GONE);
                    et_code.setVisibility(View.VISIBLE);
                    et_password.setVisibility(View.VISIBLE);
                    tv_timer.setVisibility(View.VISIBLE);
                    //btn_reset.setText("Change Password");
                    isResetInitiated = true;
                } else {

                    Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(findViewById(R.id.cocotte), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void finishResetPasswordProcess(String email,String code, String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(strChemin)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){

                    Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    countDownTimer.cancel();
                    isResetInitiated = false;
                    //goToLogin();

                } else {

                    Snackbar.make(findViewById(R.id.cocotte), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(findViewById(R.id.cocotte), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

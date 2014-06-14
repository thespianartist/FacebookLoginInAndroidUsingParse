package mx.gdgipn.parsetest.app.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import java.util.Arrays;
import java.util.List;

import mx.gdgipn.parsetest.app.ParseTestApplication;
import mx.gdgipn.parsetest.app.R;


public class MainActivity extends ActionBarActivity {

    private Dialog progressDialog;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        loginButton = (Button) findViewById(R.id.button_loggin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });



        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            startActivity(new Intent(getApplication(), UserDetailsActivity.class));
        }

        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }


    private void onLoginButtonClicked(){

        progressDialog = ProgressDialog.show(this, "Logueando con Facebook", "Espere un momento", true);

        List<String> permissions = Arrays.asList("basic_info", "user_about_me",
                                                 "user_relationships", "user_birthday", "user_location","email");

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
               progressDialog.dismiss();

                if (user == null) {
                    Log.d(ParseTestApplication.TAG,"El usuario cancelo el Loggin");
                } else if (user.isNew()) {

                   Log.d(ParseTestApplication.TAG,"Primer loggin del Usuario");
                   startActivity(new Intent(getApplication(), UserDetailsActivity.class));

                } else {
                    Log.d(ParseTestApplication.TAG, "El usuario ya estaba logueado");
                    startActivity(new Intent(getApplication(), UserDetailsActivity.class));
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }



}

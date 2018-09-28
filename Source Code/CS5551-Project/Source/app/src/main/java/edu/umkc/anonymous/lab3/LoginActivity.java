package edu.umkc.anonymous.lab3;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        Log.d("Timur2","T");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "edu.umkc.anonymous.lab3",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        setContentView(R.layout.activity_login);

        Button myFab = (Button) findViewById(R.id.fb);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent redirect = new Intent(LoginActivity.this, FacebookActivity.class);
                startActivity(redirect);
            }
        });
        Button log = (Button) findViewById(R.id.btnLogin);
        log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               checkCredentials(v);
            }
        });

        Button signup = (Button) findViewById(R.id.btnSignup);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signup(v);
            }
        });
    }

    public void checkCredentials(View v) {
        EditText usernameCtrl = (EditText)findViewById(R.id.editTextUser);
        EditText passwordCtrl = (EditText) findViewById(R.id.editTextPassword);
        TextView errorText = (TextView) findViewById(R.id.lbl_Error);
        String userName = usernameCtrl.getText().toString();
        String password = passwordCtrl.getText().toString();

        boolean validationFlag = false;


        // Verify username and password not empty
        if(!userName.isEmpty() && !password.isEmpty()) {
            if(userName.equals("Admin") && password.equals("admin")) {
                validationFlag = true;
            }
        }

        if(!validationFlag) {
            errorText.setVisibility(View.VISIBLE);
        }
        else {
            redirectToHomePage(v);
        }
    }

    public void signup (View v){
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }

    public void redirectToHomePage(View v) {
        Intent redirect = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(redirect);
    }

    public void gotofb() {
        Intent redirect = new Intent(LoginActivity.this, FacebookActivity.class);
        startActivity(redirect);
    }

}

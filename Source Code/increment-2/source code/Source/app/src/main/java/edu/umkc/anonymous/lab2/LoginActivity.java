package edu.umkc.anonymous.lab2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    boolean validationFlag = false;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = firebaseDatabase.getReference();
    View temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
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
        final String userName = usernameCtrl.getText().toString();
        final String password = passwordCtrl.getText().toString();
        temp = v;
        // Verify username and password not empty
        if(!userName.isEmpty() && !password.isEmpty()) {

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                       String name = snapshot.child("username").getValue().toString();
                       String pass = snapshot.child("password").getValue().toString();
                       if(name.equals(userName) && pass.equals(password)){
                           validationFlag = true;
                           break;
                       }
                    }
                    check(temp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }
    public void check(View v){
        if(!validationFlag) {
           // errorText.setVisibility(View.VISIBLE);
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

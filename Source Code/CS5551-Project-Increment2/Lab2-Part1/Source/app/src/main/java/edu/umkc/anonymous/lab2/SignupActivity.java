package edu.umkc.anonymous.lab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = firebaseDatabase.getReference();
EditText email;
EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.password);
    }

    public void save(View v) {
        String e = email.getText().toString();
        String p = pass.getText().toString();
       // Map<String,String> users = new HashMap<>();
        myRef.child("users").child("username").setValue(e);
        myRef.child("users").child("password").setValue(p);
       // users.put("username",e);
        //users.put("password",p);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Registration Completed")
                            .setMessage("You are successfully signed up!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent redirect = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(redirect);
                                }
                            }).show();
                }
            }
        });
    }
}

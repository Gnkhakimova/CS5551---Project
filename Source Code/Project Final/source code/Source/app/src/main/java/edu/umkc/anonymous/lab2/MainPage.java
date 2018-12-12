package edu.umkc.anonymous.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void beginclick (View v){
        Intent intent = new Intent(this,PhotoActivity.class);
        startActivity(intent);
    }

    public void stopclick (View v){
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }
}

package edu.umkc.anonymous.lab2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
    int TAKE_PHOTO_CODE = 0;
    ImageView userPhoto;
    Bitmap photo;
    static int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }
        Button takePhoto = (Button) findViewById(R.id.btn_take_photo);
//        Button analyze = (Button) findViewById(R.id.btn_photo_ok);
        userPhoto = (ImageView)  findViewById(R.id.user_photo);
//        if (userPhoto.getDrawable().equals(true)) {
//            analyze.setVisibility(View.VISIBLE);
//        }
        takePhoto.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                }

            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button analyze = (Button) findViewById(R.id.btn_photo_ok);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK ) {
            photo = (Bitmap) data.getExtras().get("data");
            userPhoto.setImageBitmap(photo);
        }
        if (userPhoto.getDrawable() != null) {
            analyze.setVisibility(View.VISIBLE);
        }
    }

    public void redirectToAnalyze(View v) {
        Intent redirect = new Intent(PhotoActivity.this, GetPlateNumber.class);
        redirect.putExtra("Image", photo);
        startActivity(redirect);
    }
}

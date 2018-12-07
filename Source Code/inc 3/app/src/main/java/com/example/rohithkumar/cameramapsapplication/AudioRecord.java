package com.example.rohithkumar.cameramapsapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class AudioRecord extends AppCompatActivity {

    ImageButton Record,RecordStop,PlayRecord,PlayStop;
    String pathsave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQQUEST_PERMISSION_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);


        if(!chechPremissionfromDrive())
            requestPermission();
        Record = (ImageButton)findViewById(R.id.play_record);
        RecordStop = (ImageButton)findViewById(R.id.stop_record);
        PlayRecord = (ImageButton)findViewById(R.id.record_play);
        PlayStop = (ImageButton)findViewById(R.id.play_stop);





            Record.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick (View view){
                        if(chechPremissionfromDrive())
                        {
                    pathsave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    RecordStop.setEnabled(false);
                    PlayRecord.setEnabled(false);

                    Toast.makeText(AudioRecord.this, "Recording Audio", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermission();
                }
                }

            });

            RecordStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaRecorder.stop();
                    RecordStop.setEnabled(false);
                    PlayRecord.setEnabled(true);
                    Record.setEnabled(true);
                    PlayStop.setEnabled(false);
                }
            });
            PlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayStop.setEnabled(false);
                    RecordStop.setEnabled(false);
                    Record.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathsave);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    Toast.makeText(AudioRecord.this,"Playing..",Toast.LENGTH_SHORT).show();
                }
            });

            PlayStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Record.setEnabled(false);
                    RecordStop.setEnabled(true);
                    PlayStop.setEnabled(false);
                    PlayRecord.setEnabled(true);

                    if(mediaPlayer!=null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });

    }
    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathsave);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean chechPremissionfromDrive(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioresult = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && recordAudioresult == PackageManager.PERMISSION_GRANTED;
    }
}

package edu.umkc.anonymous.lab2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import com.microsoft.projectoxford.face.rest.ClientException;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.ClarifaiURLImage;
import clarifai2.dto.input.Crop;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.model.output_info.ConceptOutputInfo;
import clarifai2.dto.prediction.Concept;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.google.common.base.Predicates.equalTo;

public class OfflineActivity extends Activity {
    int TAKE_PHOTO_CODE = 0;
    ImageView userPhoto;
    Bitmap photo;
    Bitmap ownerFace;
    int counter = 1;
    String userFaceID;
    String ownerFaceID;
    Boolean displayImage = true;
    Boolean isOwner;
    Boolean send = true;

    SendSMS sendSMS = new SendSMS();
    static int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private ProgressDialog detectionProgressDialog;
   private final String apiEndpointDetect = "https://westus.api.cognitive.microsoft.com/face/v1.0";
    private final String apiEndpointVerify = "https://westus.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "1bc3baf752654fb19231b01f4f609478";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpointDetect, subscriptionKey);

    private final FaceServiceClient verifyFace =
            new FaceServiceRestClient(apiEndpointVerify,subscriptionKey);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        displayImage = true;

        Button takePhoto = (Button) findViewById(R.id.btn_owner);

        userPhoto = (ImageView)  findViewById(R.id.owner_photo);
        // Comment out for tutorial

        new Thread(new Runnable() {
            public void run() {
                //Do whatever
                try {
                    Bundle extras = getIntent().getExtras();
                    String src = extras.getString("ownerPhoto");
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    ownerFace = BitmapFactory.decodeStream(input);

                } catch (IOException e) {
                    // Log exception

                }

            }
        }).start();

        takePhoto.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                displayImage = true;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }

        });
        detectionProgressDialog = new ProgressDialog(this);
    }


        // Detect faces by uploading a face image.
// Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            //publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                                /* new FaceServiceClient.FaceAttributeType[] {
                                    FaceServiceClient.FaceAttributeType.Age,
                                    FaceServiceClient.FaceAttributeType.Gender }
                                */
                            );
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            if(counter == 1){
                                userFaceID = result[0].faceId.toString();
                                counter = counter+1;
                            }
                            else {
                                ownerFaceID = result[0].faceId.toString();
                            }

                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                       detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                       detectionProgressDialog.dismiss();

                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;

                       if(displayImage) {
                           ImageView imageView = findViewById(R.id.owner_photo);
                           imageView.setImageBitmap(
                                   drawFaceRectanglesOnBitmap(imageBitmap, result));
                           displayImage = false;
                       }
                        //imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);

    }
        private static Bitmap drawFaceRectanglesOnBitmap(
                Bitmap originalBitmap, Face[] faces) {
            Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            if (faces != null) {
                for (Face face : faces) {
                    FaceRectangle faceRectangle = face.faceRectangle;
                    canvas.drawRect(
                            faceRectangle.left,
                            faceRectangle.top,
                            faceRectangle.left + faceRectangle.width,
                            faceRectangle.top + faceRectangle.height,
                            paint);
                }
            }
            return bitmap;
        }

        private void showError(String message) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .create().show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Button analyze = (Button) findViewById(R.id.btn_owner_ok);

            if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

                photo = (Bitmap) data.getExtras().get("data");
                if(photo!=null) {
                    detectAndFrame(photo);
                }
                if(ownerFace != null) {
                    detectAndFrame(ownerFace);
                }
                verifyFace();
                userPhoto.setImageBitmap(photo);

            }
            if (userPhoto.getDrawable() != null) {
                analyze.setVisibility(View.VISIBLE);
            }

        }

        public void redirectToFace(View v) {
            //Intent redirect = new Intent(MainActivity.this, NewsActivity.class);
            // startActivity(redirect);


        }


        private class myAsyncTask extends AsyncTask<String, Integer, String> {
            String mTAG = "myAsyncTask";
            ////EditText url = (EditText) findViewById(R.id.url);
            //String imageUrl = url.getText().toString();

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... arg) {

                final ClarifaiClient client = new ClarifaiBuilder("93d21301af8945f88c033198ca19e919").buildSync();
                //train model

                Model<Concept> generalModel = client.getDefaultModels().generalModel();
                File file;

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                file = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.flush();
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PredictRequest<Concept> request = generalModel.predict().withInputs(
                        ClarifaiInput.forImage(file)
                );
                List<ClarifaiOutput<Concept>> result = request.executeSync().get();
                final String results = "Results: " + result.get(0).data().get(0).name().toString() + ", " + result.get(0).data().get(1).name().toString() + ", " + result.get(0).data().get(2).name().toString();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // TextView TV = (TextView) findViewById(R.id.clarifai);
                        // TV.setText(results);

                    }
                });
                return "ok";
            }
        }

    private void verifyFace() {
        AsyncTask<InputStream, String, VerifyResult> verifyTask = new AsyncTask<InputStream, String, VerifyResult>() {
            String exceptionMessage = "";

            @Override
            protected VerifyResult doInBackground(InputStream... inputStreams) {
                try {

                    Bundle extras = getIntent().getExtras();
                    VerifyResult res = verifyFace.verify(UUID.fromString(ownerFaceID), UUID.fromString(userFaceID));
                    if (res == null) {
                        publishProgress(
                                "Detection Finished. Nothing detected");
                        return null;
                    }
                    else{
                        if(res.confidence < 0.50){
                            publishProgress("I am calling police!");


                            sendSMS.execute(extras.getString("mainPhone"), extras.getString("secondPhone"), "c");



                        }
                    }

                    return res;
                } catch (Exception e) {
                    String x = e.toString();
                    return null;
                }
            }
        };
        verifyTask.execute();
    }

    private class SendSMS extends AsyncTask<String, Integer, String> {
        String mTAG = "SendSMS";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String SmsBody = "Suspicious person is in your car. Call 911 if needed";
                Bundle extras = getIntent().getExtras();

                URL url = new URL("https://hackaroo2018.herokuapp.com/sendSMS?phone=+"+"1"+arg[0]+"&text="+SmsBody);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setDoOutput(true);

                int status_code = http.getResponseCode();
                if (status_code == 200) {
                    System.out.println(status_code);
                }
//((HttpURLConnection) con).disconnect();

                URL url1 = new URL("https://hackaroo2018.herokuapp.com/sendSMS?phone=+"+"1"+arg[1]+"&text="+SmsBody);
                URLConnection con1 = url1.openConnection();
                HttpURLConnection http1 = (HttpURLConnection) con1;
                http1.setRequestMethod("POST"); // PUT is another valid option
                http1.setDoOutput(true);

                int status_code1 = http1.getResponseCode();
                if (status_code1 == 200) {
                    System.out.println(status_code1);
                }
                //if(send){

                 //   send = false;
               // }

                OfflineActivity.myAsyncTask mTask = new OfflineActivity.myAsyncTask();
                mTask.execute("abc", "10", "Hello world");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            send = false;
            return "OK";
        }
    }


}


package edu.umkc.anonymous.lab2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Intent intent = getIntent();
        final Bitmap image = intent.getParcelableExtra("Image");

        ImageView userPhoto = (ImageView) findViewById(R.id.img_analyze);
        userPhoto.setImageBitmap(image);
        new GetResultsTask().execute(image);
    }

    public static List<EntityAnnotation> detectLabels(Bitmap bmp) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        // convert Bitmap to byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        stream.close();

        // convert byte array to Google Image object
        Image inputImage = new Image().encodeContent(imageBytes);

        // create request object (AnnotateImageRequest) with Feature extraction type and image
        Feature feat = new Feature().setType("LABEL_DETECTION");
        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage).setFeatures(Arrays.asList(feat));

        // create a batch request as expected by api
        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Arrays.asList(request));

        // get the google api client
        Vision vision = getVision();

        // execute request with the client and capture the response
        BatchAnnotateImagesResponse response =
                vision.images().annotate(batchRequest).execute();

        return response.getResponses().get(0).getLabelAnnotations();
    }

    public static Vision getVision() {
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null
        );
        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer(BuildConfig.GoogleAPIKey));

        return visionBuilder.build();
    }

    public void printResults(List<EntityAnnotation> labels) {
        TextView labelText = (TextView) findViewById(R.id.text_label_results);
        TextView scoreText = (TextView) findViewById(R.id.text_confidence_results);
        String labelResult = "";
        String scoreResult = "";
        for (int i = 0; i < 5; i++) {
            String description = labels.get(i).getDescription();
            String score = labels.get(i).getScore().toString();
            labelResult += description + "\n";
            scoreResult += score + "\n";
        }

        labelText.setText(labelResult);
        scoreText.setText(scoreResult);
    }

    private class GetResultsTask extends AsyncTask<Bitmap, Void, List<EntityAnnotation>> {
        private ProgressDialog progress = new ProgressDialog(AnalyzeActivity.this);

        @Override
        protected void onPreExecute() {
            this.progress.setMessage("Please wait");
            this.progress.show();
        }
        @Override
        protected List<EntityAnnotation> doInBackground(Bitmap... bmp) {
            List<EntityAnnotation> labels = new ArrayList<>();
            try {
                labels = detectLabels(bmp[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return labels;
        }

        protected void onPostExecute(List<EntityAnnotation> labels) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            printResults(labels);
        }
    }

    public void redirectToHomePage(View v) {
        Intent redirect = new Intent(AnalyzeActivity.this, MainActivity.class);
        startActivity(redirect);
    }
}

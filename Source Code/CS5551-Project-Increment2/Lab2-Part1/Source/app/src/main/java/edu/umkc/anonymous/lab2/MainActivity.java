package edu.umkc.anonymous.lab2;

import android.app.Activity;
import android.content.Intent;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void redirectToPhoto(View v) {
        Intent redirect = new Intent(MainActivity.this, PhotoActivity.class);
        startActivity(redirect);
    }

    public void logout(View v) {
        Intent redirect = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(redirect);
    }

    public void redirectToFace(View v) {
        //Intent redirect = new Intent(MainActivity.this, NewsActivity.class);
        // startActivity(redirect);
        MainActivity.myAsyncTask mTask = new MainActivity.myAsyncTask();
        mTask.execute("abc", "10", "Hello world");

    }

    private class myAsyncTask extends AsyncTask<String, Integer, String> {
        String mTAG = "myAsyncTask";
        EditText url = (EditText) findViewById(R.id.url);
        String imageUrl = url.getText().toString();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg) {

            final ClarifaiClient client = new ClarifaiBuilder("814464bfd2914a3b8ba9e6f7138ff131").buildSync();
            Model<Concept> generalModel = client.getDefaultModels().generalModel();

            PredictRequest<Concept> request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(imageUrl)
            );
            List<ClarifaiOutput<Concept>> result = request.executeSync().get();
final String results = "Results: "+result.get(0).data().get(0).name().toString() +", "+result.get(0).data().get(1).name().toString()+", "+result.get(0).data().get(2).name().toString() ;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    TextView TV = (TextView) findViewById(R.id.clarifai);
                    TV.setText(results);

                }
            });
            return "ok";
        }
    }
}



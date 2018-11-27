package edu.umkc.anonymous.lab2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NewsActivity extends Activity {
    ListView listView ;
    ArrayList<News> results = new ArrayList<>();

    String requestUrl1 = "http://content.guardianapis.com/search?from-date=2018-07-10&to-date=2018-07-10&order-by=newest&show-fields=all&page-size=10&api-key=eca30854-2ebc-45f6-894a-7b4e9ea0c50d";
String[] r = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExecutorService service = Executors.newFixedThreadPool(8);
        service.submit(new Runnable() {
            public void run() {
                callGuardian();
            }
        });

        while (r[0]== null) {
            Log.i("Sleep", "entered check");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("Sleep", "r[0] is null");
        }

        setContentView(R.layout.activity_news);

        listView = (ListView) findViewById(R.id.list);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, r);

        // Assign adapter to ListView
        listView.setAdapter(adapter);



        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(results.get(itemPosition).getUrl()));
                startActivity(browserIntent);
            }

        });
    }

        public void callGuardian() {
            URL url = null;
            ArrayList<News> newsList = new ArrayList<>();
            String requestUrl = "http://content.guardianapis.com/search?from-date=2018-07-10&to-date=2018-07-10&order-by=newest&show-fields=all&page-size=10&api-key=eca30854-2ebc-45f6-894a-7b4e9ea0c50d";

            // A string to store the response obtained from rest call in the form of string
            String jsonResponse = "";
            try {
                //TODO: 1. Create a URL from the requestUrl string and make a GET request to it;
                url = new URL(requestUrl);
                Log.i("here", "here");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("GET");
                Log.i("urlconnection", urlConnection.toString());
                urlConnection.connect();

                //TODO: 2. Read from the Url Connection and store it as a string(jsonResponse)
                StringBuilder response = new StringBuilder();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    response.append(line);
                    line = reader.readLine();
                }
                jsonResponse = response.toString();

                JSONObject json = new JSONObject(jsonResponse);
                JSONObject j = json.getJSONObject("response");
                JSONArray arr = j.getJSONArray("results");
                //JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    String title = jsonObject.getString("webTitle");
                    r[i]=title;
                    String datetime = jsonObject.getString("webPublicationDate");
                    String url1 = jsonObject.getString("webUrl");
                    News n = new News(title, datetime, url1);
                    newsList.add(n);
                }
            } catch (Exception e) {

            }
            results = newsList;

        }}


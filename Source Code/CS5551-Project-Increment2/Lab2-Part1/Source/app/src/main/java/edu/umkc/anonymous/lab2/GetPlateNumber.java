package edu.umkc.anonymous.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class GetPlateNumber extends AppCompatActivity {
    Bitmap img;
    Boolean flag = false;
    String vehicleInfo;
    String plate;
    String region;
    String VehicleYear;
    String VehicleColor;
    String VehicleMake;
    String stolen;
    String crash;
    String outStolen = "No";
    String outCrash = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_plate_number);
        Intent intent = getIntent();
        final Bitmap image = intent.getParcelableExtra("Image");
        GetPlateNumber(image);
    }

    public void GetPlateNumber(Bitmap bmp){

    img = bmp;
        myAsyncTask mTask = new myAsyncTask();
        mTask.execute("abc","10","Hello world");

    }

    public void outputResult(String result){
        try {
            JSONObject json = new JSONObject(result);

            plate = json.getJSONArray("results").getJSONObject(0).optString("plate");
            region = json.getJSONArray("results").getJSONObject(0).optString("region");
            VehicleColor= json.getJSONArray("results").getJSONObject(0).getJSONObject("vehicle").getJSONArray("color").getJSONObject(0).getString("name");
            VehicleMake= json.getJSONArray("results").getJSONObject(0).getJSONObject("vehicle").getJSONArray("make").getJSONObject(0).getString("name");
            VehicleYear= json.getJSONArray("results").getJSONObject(0).getJSONObject("vehicle").getJSONArray("year").getJSONObject(0).getString("name");
            getInfoTask iTask = new getInfoTask();
            iTask.execute("a","b","c");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    TextView TV = (TextView) findViewById(R.id.result);
                    TV.setText("Plate: "+plate+"\n"+
                               "State: "+region.toUpperCase()+"\n"+
                               "Make: "+VehicleMake.toUpperCase()+"\n"+
                               "Year: "+VehicleYear+"\n"+
                               "Color: "+VehicleColor.toUpperCase());

                }
            });

        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private class myAsyncTask extends AsyncTask<String, Integer, String> {
        String mTAG = "myAsyncTask";
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String...arg) {
            String result = "";

            try {
                String secret_key = "";
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();
                byte[] encoded = android.util.Base64.encode(data, android.util.Base64.DEFAULT);


                // Setup the HTTPS connection to api.openalpr.com
                URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=us&secret_key=" + secret_key);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setFixedLengthStreamingMode(encoded.length);
                http.setDoOutput(true);

                // Send our Base64 content over the stream
                try (OutputStream os = http.getOutputStream()) {
                    os.write(encoded);
                }

                int status_code = http.getResponseCode();
                if (status_code == 200) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            http.getInputStream()));
                    String json_content = "";
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        json_content += inputLine;
                    in.close();
                    result = json_content;
                    vehicleInfo = result;
                    outputResult(vehicleInfo);
                    System.out.println(json_content);
                } else {
                    System.out.println("Got non-200 response: " + status_code);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return result;
        }

        }
    private class getInfoTask extends AsyncTask<String, Integer, String> {
        String mTAG = "getInfoTask";
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String...arg) {
            String result = "";
            BufferedReader reader = null;
            StringBuilder stringBuilder;


            try {

                // Setup the HTTPS connection to api.openalpr.com
                URL url = new URL("https://quiet-brushlands-99331.herokuapp.com/search?plate="+plate);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("GET"); // PUT is another valid option
                con.setReadTimeout(15000);
                con.connect();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line + "\n");
                }
                String temp = stringBuilder.toString();
                temp.substring(2, temp.length()-2);
                HashMap<String, String> holder = new HashMap();
                String payload = temp;
                String[] keyVals = payload.split(",");
                for(String keyVal:keyVals)
                {
                    String[] parts = keyVal.split(":",2);
                    holder.put(parts[0],parts[1]);
                }
                stolen = holder.get("\"stolen\"");
                crash = holder.get("\"crash\"");

                if(stolen.contains("yes")){
                    outStolen = "Yes";
                }
                if(crash.contains("yes")){
                    outCrash = "Yes";
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        TextView TV = (TextView) findViewById(R.id.info);
                        TV.setText("Stolen: "+outStolen+"\n"+
                                "Car Accedent: "+outCrash+"\n");
                    }
                });
                System.out.println(stringBuilder.toString());
                return stringBuilder.toString();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return result;
        }

    }

}

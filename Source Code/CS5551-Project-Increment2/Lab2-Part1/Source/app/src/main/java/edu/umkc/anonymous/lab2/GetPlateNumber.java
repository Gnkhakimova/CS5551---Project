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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetPlateNumber extends AppCompatActivity {
    Bitmap img;
    Boolean flag = false;
    String vehicleInfo;
    String plate;
    String region;
    String VehicleYear;
    String VehicleColor;
    String VehicleMake;

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
                String secret_key = "sk_ae9a150802a7ee701116217f";
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

}

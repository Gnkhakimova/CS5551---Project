package edu.umkc.anonymous.lab2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.LatLng;

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
import java.util.List;
import java.util.Locale;

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
    String secondPhone = "";
    String firstPhone = "";
    String ownerPhoto = "";
    String familyPhoto = "";
    LatLng currentCoordinates = null;
    private double longitude;
    private double latitude;
    String address ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_plate_number);
        Intent intent = getIntent();
        final Bitmap image = intent.getParcelableExtra("Image");
        GetPlateNumber(image);


        LocationManager currentLocation = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try {
            currentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    currentLocationListener);
            latitude = currentLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    .getLatitude();
            longitude = currentLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    .getLongitude();
            //currentCoordinates = new LatLng(latitude, longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
try {
    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
    //String city = addresses.get(0).getLocality();
    //String state = addresses.get(0).getAdminArea();
    //String country = addresses.get(0).getCountryName();
    //String postalCode = addresses.get(0).getPostalCode();
    //String knownName = addresses.get(0).getFeatureName();
}
            catch (Exception e){

            }
        }
        catch (SecurityException e)
        {

        }


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
                String secret_key = "sk_c3a987b45d8ce610db8fbe12";
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
                stolen = holder.get("\"Stolen\"");
                secondPhone = holder.get("\"Crash\"");
                 firstPhone = holder.get("\"Color\"");
                 ownerPhoto = holder.get("\"owner\"");
                 familyPhoto = holder.get("\"family\"");
                secondPhone = secondPhone.substring(1,11);
                firstPhone = firstPhone.substring(1,11);
                ownerPhoto = ownerPhoto.replace("\"","");
                int index = familyPhoto.indexOf("jpg");
                familyPhoto = familyPhoto.substring(1,index+3);

                if(stolen.contains("Yes")){
                    outStolen = "Yes";



                    GetPlateNumber.SendSMS sendSMS = new GetPlateNumber.SendSMS();
                    sendSMS.execute("a", "b", "c");
                }
//                if(crash.contains("Yes")){
//                    outCrash = "Yes";
//                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        TextView TV = (TextView) findViewById(R.id.info);
                        TV.setText("Stolen: "+outStolen+"\n");
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

    public void btnback (View v){
        Intent intent = new Intent(this,MainActivity.class);
        //Intent intent = new Intent(GetPlateNumber.this, OfflineActivity.class);
        intent.putExtra("mainPhone",firstPhone);
        intent.putExtra("secondPhone",secondPhone);
        intent.putExtra("ownerPhoto",ownerPhoto);
        intent.putExtra("familyPhoto",familyPhoto);
        startActivity(intent);
    }

    private class SendSMS extends AsyncTask<String, Integer, String> {
        String mTAG = "SendSMS";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                while (address == ""){
                    Thread.sleep(1000);
                }
                String SmsBody = "Your car was noticed at following location: " + address;

                URL url = new URL("https://hackaroo2018.herokuapp.com/sendSMS?phone=+"+"1"+firstPhone+"&text="+SmsBody);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setDoOutput(true);

                int status_code = http.getResponseCode();
                if (status_code == 200) {
                    System.out.println(status_code);
                }

                URL url1 = new URL("https://hackaroo2018.herokuapp.com/sendSMS?phone=+"+"1"+secondPhone+"&text="+SmsBody);
                URLConnection con1 = url1.openConnection();
                HttpURLConnection http1 = (HttpURLConnection) con1;
                http1.setRequestMethod("POST"); // PUT is another valid option
                http1.setDoOutput(true);

                int status_code1 = http1.getResponseCode();
                if (status_code1 == 200) {
                    System.out.println(status_code1);
                }
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            return "OK";
        }
    }

}

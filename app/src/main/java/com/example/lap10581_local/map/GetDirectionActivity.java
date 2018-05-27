package com.example.lap10581_local.map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetDirectionActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private PlaceAutocompleteFragment autocompleteFragmentSoure;
    private PlaceAutocompleteFragment autocompleteFragmentDestination;
    LatLng sourcePlace = null;
    LatLng destinationPlace = null;
    Button btnBack;
    Button btnDirectionCar;
    Button btnDirectionRun;
    Button btnDirectionBike;
    Button btnSearch;
    Marker markerSource;
    Marker markerDestination;
    String addressSource;
    String addressDestination;
    String nameSource;
    String nameDestination;
    String placeIdSource;
    String placeIdDestination;
    private List<PlacePhotoMetadata> photosDataListSource;
    private GeoDataClient geoDataClient;
    static Direction direction = Direction.BIKE;
    enum Direction {
        CAR,
        RUN,
        BIKE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_direction);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDirection);
        mapFragment.getMapAsync(this);



        autocompleteFragmentSoure = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fragmentSearchSource);
        ImageView imageView1 = (ImageView)((LinearLayout)autocompleteFragmentSoure.getView()).getChildAt(0);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_source));

        autocompleteFragmentDestination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fragmentSearchDestination);
        ImageView imageView2 = (ImageView)((LinearLayout)autocompleteFragmentDestination.getView()).getChildAt(0);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_seach_destination));


        EditText editText1 = (EditText)((LinearLayout)autocompleteFragmentSoure.getView()).getChildAt(1);
        editText1.setBackgroundColor(Color.parseColor("#3B86FF"));
        editText1.setTextColor(Color.parseColor("#ffffff"));

        EditText editText2 = (EditText)((LinearLayout)autocompleteFragmentDestination.getView()).getChildAt(1);
        editText2.setBackgroundColor(Color.parseColor("#3B86FF"));
        editText2.setTextColor(Color.parseColor("#ffffff"));




        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //My fucking button
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnDirectionBike = (Button) findViewById(R.id.btnBike);
        btnDirectionCar = (Button) findViewById(R.id.btnCar);
        btnDirectionRun = (Button) findViewById(R.id.btnRun);

        //This is fragment boy
        PlaceAutocompleteFragment autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragmentSearchSource);
        PlaceAutocompleteFragment autocompleteFragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragmentSearchDestination);

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if(place.getLatLng()!=null){
                    sourcePlace = place.getLatLng();
                    addressSource = (String) place.getAddress();
                    nameSource = (String) place.getName();
                    if(markerSource!=null){
                        markerSource.remove();
                    }
                    markerSource = mMap.addMarker(new MarkerOptions().position(sourcePlace).title(nameSource+addressSource).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourcePlace,17));

                    placeIdSource = place.getId();
                }
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });

        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if(place.getLatLng()!=null){
                    destinationPlace = place.getLatLng();
                    addressDestination = (String) place.getAddress();
                    nameDestination = (String) place.getName();
                    if(markerDestination!=null){
                        markerDestination.remove();
                    }
                    markerDestination = mMap.addMarker(new MarkerOptions().position(destinationPlace).title(nameDestination+addressDestination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationPlace,15));

                    placeIdDestination = place.getId();
                }
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });

        btnDirectionBike.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                direction = Direction.BIKE;
                btnDirectionBike.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorWhite));
                btnDirectionCar.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                btnDirectionRun.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
            }
        });
        btnDirectionRun.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                direction = Direction.RUN;
                btnDirectionBike.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                btnDirectionCar.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                btnDirectionRun.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorWhite));
            }
        });
        btnDirectionCar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                direction = Direction.CAR;
                btnDirectionBike.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                btnDirectionCar.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorWhite));
                btnDirectionRun.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimaryDark));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sourcePlace !=null&&destinationPlace!=null){
                    String url = getRequestUrl(sourcePlace,destinationPlace);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myLatLag = new LatLng(10.762622, 106.660172);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLag,12));
        Intent intent = getIntent();
        if(intent.getAction()!=null){
            EditText editText1 = (EditText)((LinearLayout)autocompleteFragmentSoure.getView()).getChildAt(1);
            double lat = intent.getDoubleExtra("lat",10);
            double lon = intent.getDoubleExtra("lon",10);
            sourcePlace = new LatLng(lat,lon);
            nameSource = intent.getStringExtra("name");
            placeIdSource = intent.getStringExtra("placeId");
            addressSource = intent.getStringExtra("address");
            editText1.setText(nameSource);
            markerSource = mMap.addMarker(new MarkerOptions().position(sourcePlace).title(nameSource+addressSource).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourcePlace,12));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private String getRequestUrl(LatLng sourcePlace, LatLng destinationPlace) {
        String str_org = "origin=" + sourcePlace.latitude+","+sourcePlace.longitude;
        String str_des = "destination=" + destinationPlace.latitude+","+destinationPlace.longitude;
        String sensor = "sensor=false";
        String mode = "";
        switch (direction){
            case CAR:
                mode="mode=driving";
                break;
            case RUN:
                mode="mode=transit";
                break;
            case BIKE:
                mode="mode=cycling";
                break;
                default:
                    mode="mode=driving";
        }
        String param = str_org+"&"+str_des+"&"+sensor+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try{
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String,String>> path:lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for(HashMap<String,String> point:path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!=null){
                mMap.addPolyline(polylineOptions);
            }
            else{
                Toast.makeText( getApplicationContext(),"Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.lap10581_local.map;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NearbyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 1000;
    double latitude,longitude;
    Circle circle;
    String url;
    TypeLocation typeLocation = TypeLocation.HOSPITAL;
    private Button btnIncreaseRadius;
    private Button btnDecreaseRadius;
    private Button btnSignUp;
    private Button btnSignIn;

    enum TypeLocation{
        HOSPITAL,
        SCHOOL,
        RESTAURENT,
        FOOD,
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNearby);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        btnDecreaseRadius = (Button) findViewById(R.id.btnDecreaseRadius);
        btnIncreaseRadius = (Button) findViewById(R.id.btnIncreaseRadius);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnIncreaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                PROXIMITY_RADIUS+=100;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                Object dataTransfer[] = new Object[2];
                url = getUrl(latitude,longitude);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);

                if(circle != null){
                    circle.remove();
                }
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(new LatLng(latitude,longitude)).radius(PROXIMITY_RADIUS);

                circle = mMap.addCircle(circleOptions);
                circle.setFillColor(0x220000FF);
                circle.setStrokeColor(Color.WHITE);

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(NearbyActivity.this));
                Log.d("Set Info windows","success");
            }
        });

        btnDecreaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROXIMITY_RADIUS-=100;
                mMap.clear();
                url = getUrl(latitude,longitude);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);

                if(circle != null){
                    circle.remove();
                }
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(new LatLng(latitude,longitude)).radius(PROXIMITY_RADIUS);

                circle = mMap.addCircle(circleOptions);
                circle.setFillColor(0x220000FF);
                circle.setStrokeColor(Color.WHITE);

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(NearbyActivity.this));
                Log.d("Set Info windows","success");
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.search:
                Intent intent = new Intent(this,MapsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.762622,106.660172),15));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                latitude = cameraPosition.target.latitude;
                longitude = cameraPosition.target.longitude;
                Log.d("Camera change ",latitude+","+longitude);
                mMap.clear();
                url = getUrl(latitude,longitude);

                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);

                if(circle != null){
                    circle.remove();
                }
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(new LatLng(latitude,longitude)).radius(PROXIMITY_RADIUS);

                circle = mMap.addCircle(circleOptions);
                circle.setFillColor(0x220000FF);
                circle.setStrokeColor(Color.WHITE);

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(NearbyActivity.this));
                Log.d("Set Info windows","success");
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String snippetInfo = marker.getTitle();
                String[] arraySnippet = snippetInfo.split("\\:");
                String name = arraySnippet[0];
                String address="";
                if(arraySnippet.length>1)
                    address = arraySnippet[1];
                Intent intent = new Intent(NearbyActivity.this,GetDirectionActivity.class);
                intent.putExtra("lat",marker.getPosition().latitude);
                intent.putExtra("long",marker.getPosition().longitude);
                intent.putExtra("name",name);
                intent.putExtra("address",address);
                startActivity(intent);
            }
        });
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, (com.google.android.gms.location.LocationListener) this);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {

            case R.id.B_hospitals:
                mMap.clear();
                typeLocation = TypeLocation.HOSPITAL;
//                String hospital = "hospital";
//                url = getUrl(latitude, longitude, hospital);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                break;


            case R.id.B_schools:
                mMap.clear();
                typeLocation = TypeLocation.SCHOOL;
//                String school = "school";
//                url = getUrl(latitude, longitude, school);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_restaurants:
                mMap.clear();
                typeLocation = TypeLocation.RESTAURENT;
//                String resturant = "restaurant";
//                url = getUrl(latitude, longitude, resturant);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_food:
                mMap.clear();
                typeLocation = TypeLocation.RESTAURENT;
                break;
        }
    }


    private String getUrl(double latitude , double longitude)
    {
        String nearbyPlace="";
        switch (typeLocation){
            case HOSPITAL:
                nearbyPlace="hospital";
                break;
            case SCHOOL:
                nearbyPlace="school";
                break;
            case RESTAURENT:
                url = "restaurant";
                break;
            case FOOD:
                url = "food";
                break;
            default:
                url = "hospital";
        }
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


}


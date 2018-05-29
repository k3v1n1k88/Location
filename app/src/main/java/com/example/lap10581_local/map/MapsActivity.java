package com.example.lap10581_local.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Something" ;
    private GoogleMap mMap;
    private LatLng mLocation;
    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private FloatingActionButton floatingActionButton;
    int REQUEST_CODE = 123;
    Marker marker;
    String address;
    String name;
    GoogleApiClient gac;
    String placeId;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private LinearLayout listImg;
    @Override
    protected void onStart() {
        super.onStart();
        if(gac!=null)
            gac.connect();
    }

    @Override
    protected void onStop() {
        if(gac!=null&&gac.isConnected())
            gac.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //editText = (EditText) findViewById(R.id.editText2);

        geoDataClient = Places.getGeoDataClient(this,null);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        ImageView imageView = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_search));
        EditText editTextSearch = (EditText)((LinearLayout)autocompleteFragment.getView()).getChildAt(1);
        editTextSearch.setTextSize(15);


        imageView1 = (ImageView)findViewById(R.id.imgView1);
        imageView2 = (ImageView)findViewById(R.id.imgView2);
        imageView3 = (ImageView)findViewById(R.id.imgView3);
        listImg = (LinearLayout)findViewById(R.id.listImage);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton) ;

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, GetDirectionActivity.class);
                if(name!=null) {
                    intent.putExtra("lat", mLocation.latitude);
                    intent.putExtra("long", mLocation.longitude);
                    intent.putExtra("name", name);
                    intent.putExtra("address", address);
                    intent.putExtra("placeID", placeId);
                }
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "aalo", Toast.LENGTH_SHORT).show();
            }
        });
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if(place.getLatLng()!=null){
                    mLocation = place.getLatLng();
                    address = (String) place.getAddress();
                    name = (String) place.getName();
                    if(marker!=null){
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(mLocation).title(name+address).flat(false));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation,17));

                    placeId = place.getId();
                    final Task<PlacePhotoMetadataResponse> photoRespond = geoDataClient.getPlacePhotos(placeId);
                    photoRespond.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                            photosDataList = new ArrayList<>();
                            PlacePhotoMetadataResponse photos = task.getResult();
                            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                            for(PlacePhotoMetadata photoMetadata:photoMetadataBuffer){
                                photosDataList.add(photoMetadata.freeze());

                            }
                            photoMetadataBuffer.release();
                        }
                    });
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

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

        // Add a marker in Sydney and move the camera
        LatLng myLatLag = new LatLng(10.762622, 106.660172);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLag,12));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                listImg.setVisibility(View.INVISIBLE);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                viewPhoto();
                return true;
            }
        });
    }
    private void viewPhoto(){
        if(photosDataList.size()<3)
            return;
        Task<PlacePhotoResponse> photoResponse1 = geoDataClient.getPhoto(photosDataList.get(0));
        Task<PlacePhotoResponse> photoResponse2 = geoDataClient.getPhoto(photosDataList.get(1));
        Task<PlacePhotoResponse> photoResponse3 = geoDataClient.getPhoto(photosDataList.get(2));
        photoResponse1.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap1 = photo.getBitmap();
                imageView1.invalidate();
                imageView1.setImageBitmap(photoBitmap1);
            }
        });
        photoResponse2.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap2 = photo.getBitmap();
                imageView2.invalidate();
                imageView2.setImageBitmap(photoBitmap2);
            }
        });
        photoResponse3.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                imageView3.invalidate();
                imageView3.setImageBitmap(photoBitmap);
            }
        });

        listImg.setVisibility(View.VISIBLE);
    }
}

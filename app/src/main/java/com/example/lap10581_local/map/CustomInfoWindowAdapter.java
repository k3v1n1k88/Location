package com.example.lap10581_local.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/2/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private Place place;
    static List<PlacePhotoMetadata> photosDataList;
    GeoDataClient geoDataClient;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @SuppressLint("LongLogTag")
    private void rendowWindowText(final Marker marker, View view){

        String name="";
        String address="";
        String placeId = "";
        String rating = "";

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvRating = (TextView) view.findViewById(R.id.rating);
        TextView addressView = (TextView) view.findViewById(R.id.address);

        if(marker.getTitle()!=null){
            String[] arrayStringInfor = marker.getTitle().split("\\:");
            name = arrayStringInfor[0];
            if(arrayStringInfor.length>1){
                address = arrayStringInfor[1];
            }
        }

        if(marker.getSnippet()!=null){
            String[] arraySnippet = marker.getSnippet().split("\\:");
            placeId = arraySnippet[0];
            if(arraySnippet.length>1){
                rating = arraySnippet[1];
            }
        }
        Log.d("address",address);
        Log.d("rating",rating);
        Log.d("CustomInfo placeID:",placeId);
        Log.d("CustomInfo rating:",rating);


        if(!name.equals("")){

            Log.d("title",name);
            tvTitle.setText(name);
        }

        if(!address.equals("")){

            Log.d("address",address);
            addressView.setText(address);
        }
        if(!rating.equals("")){
            Log.d("rating",rating);
            tvRating.setText(rating);
        }

        //Add list photo
        final ImageView imageView = view.findViewById(R.id.imgView);

        geoDataClient = Places.getGeoDataClient(mContext,null);
        if(!placeId.equals("")){
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
                    if(photosDataList!=null) {
                        if(photosDataList.size()>0){
                            Log.d("size:", String.valueOf(photosDataList.size()));
                            Task<PlacePhotoResponse> listTask = geoDataClient.getPhoto(photosDataList.get(0));
                            listTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse photo = task.getResult();
                                    Bitmap photoBitmap = photo.getBitmap();
                                    imageView.invalidate();
                                    imageView.setImageBitmap(photoBitmap);
                                }
                            });
                        }
                    }
                }
            });
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}

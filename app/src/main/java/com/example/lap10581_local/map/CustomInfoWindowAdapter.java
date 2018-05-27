package com.example.lap10581_local.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by User on 10/2/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private Place place;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @SuppressLint("LongLogTag")
    private void rendowWindowText(Marker marker, View view){

        Log.d("Title:",marker.getTitle());
        System.out.println(marker.getSnippet());
        marker.getId();
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
//
        if(!title.equals("")){
            tvTitle.setText(title);
        }
//
//        String address = marker.getSnippet();
//        TextView addressView = (TextView) view.findViewById(R.id.address);
//
//        if(!address.equals("")){
//            addressView.setText(address);
//        }

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

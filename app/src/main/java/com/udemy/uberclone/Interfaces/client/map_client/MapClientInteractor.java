package com.udemy.uberclone.Interfaces.client.map_client;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public interface MapClientInteractor {

    void getActiveDrivers(LatLng currentLatLng, GoogleMap googleMap, List<Marker> driversMarkers);

    void onCameraListener(Context context, GoogleMap map);

    void setOnPlaceSelectListener();

}

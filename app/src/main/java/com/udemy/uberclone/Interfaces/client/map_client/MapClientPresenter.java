package com.udemy.uberclone.Interfaces.client.map_client;

import android.content.Context;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import java.util.List;

public interface MapClientPresenter {

    void getActiveDrivers(LatLng currentLatLng, GoogleMap googleMap, List<Marker> driversMarkers);

    void addActiveDrivers(GeoLocation geoLocation, DataSnapshot dataSnapshot);

    void onCameraListener(Context context, GoogleMap map);

    void setCameraListener(GoogleMap.OnCameraIdleListener onCameraIdleListener);

    void setAutoCompleteInfoOrigin(String adress, String city, LatLng latLng);

    void setAutoCompleteInfoDestination(String adress, String city, LatLng latLng);

    void setOnPlaceSelectListener();

    void setDestinationSelected(String place, LatLng latLng);

    void setOriginSelected(String place, LatLng latLng);

    void removeDriverMarkers(DataSnapshot dataSnapshot);

}

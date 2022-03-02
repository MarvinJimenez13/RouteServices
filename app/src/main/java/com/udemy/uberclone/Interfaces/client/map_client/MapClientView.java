package com.udemy.uberclone.Interfaces.client.map_client;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

public interface MapClientView {

    void addActiveDriverMarkers(GeoLocation location, DataSnapshot dataSnapshot);

    void removeDriverMarkers(DataSnapshot dataSnapshot);

    void setCameraIDListener(GoogleMap.OnCameraIdleListener onCameraIdleListener);

    void setAutoCompleteInfoOrigin(String adress, String city, LatLng latLng);

    void setAutoCompleteInfoDestination(String adress, String city, LatLng latLng);

    void setDestinationSelected(String place, LatLng latLng);

    void setOriginSelected(String place, LatLng latLng);

}

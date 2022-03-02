package com.udemy.uberclone.Presenters.client;

import android.content.Context;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.udemy.uberclone.Interactors.client.MapClientInteractorImpl;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientInteractor;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientPresenter;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientView;
import java.util.List;

public class MapClientPresenterImpl implements MapClientPresenter {

    private MapClientInteractor mapClientInteractor;
    private MapClientView mapClientView;

    public MapClientPresenterImpl(MapClientView mapClientView){
        mapClientInteractor = new MapClientInteractorImpl(this);
        this.mapClientView = mapClientView;
    }

    @Override
    public void getActiveDrivers(LatLng currentLatLng, GoogleMap map, List<Marker> driversMarkers) {
        mapClientInteractor.getActiveDrivers(currentLatLng, map, driversMarkers);
    }

    @Override
    public void addActiveDrivers(GeoLocation location, DataSnapshot dataSnapshot) {
        if(mapClientView != null)
            mapClientView.addActiveDriverMarkers(location, dataSnapshot);
    }

    @Override
    public void onCameraListener(Context context, GoogleMap map) {
        mapClientInteractor.onCameraListener(context, map);
    }

    @Override
    public void setCameraListener(GoogleMap.OnCameraIdleListener onCameraIdleListener) {
        if(mapClientView != null)
            mapClientView.setCameraIDListener(onCameraIdleListener);
    }

    @Override
    public void setAutoCompleteInfoOrigin(String adress, String city, LatLng latLng) {
        if(mapClientView != null)
            mapClientView.setAutoCompleteInfoOrigin(adress, city, latLng);
    }

    @Override
    public void setAutoCompleteInfoDestination(String adress, String city, LatLng latLng) {
        if(mapClientView != null)
            mapClientView.setAutoCompleteInfoDestination(adress, city, latLng);
    }

    @Override
    public void setOnPlaceSelectListener() {
        mapClientInteractor.setOnPlaceSelectListener();
    }

    @Override
    public void setDestinationSelected(String place, LatLng latLng) {
        if(mapClientView != null)
            mapClientView.setDestinationSelected(place, latLng);
    }

    @Override
    public void setOriginSelected(String place, LatLng latLng) {
        if(mapClientView != null)
            mapClientView.setOriginSelected(place, latLng);
    }

    @Override
    public void removeDriverMarkers(DataSnapshot dataSnapshot) {
        if(mapClientView != null)
            mapClientView.removeDriverMarkers(dataSnapshot);
    }

}

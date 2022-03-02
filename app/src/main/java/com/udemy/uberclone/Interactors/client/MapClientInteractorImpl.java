package com.udemy.uberclone.Interactors.client;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import androidx.annotation.NonNull;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientInteractor;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientPresenter;
import com.udemy.uberclone.Utils.providers.GeofireProvider;
import com.udemy.uberclone.Views.client.MapClientActivity;
import java.util.List;

public class MapClientInteractorImpl implements MapClientInteractor {

    private GeofireProvider geofireProvider;
    private MapClientPresenter mapClientPresenter;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    public MapClientInteractorImpl(MapClientPresenter mapClientPresenter){
        this.mapClientPresenter = mapClientPresenter;
        geofireProvider = new GeofireProvider("active_drivers");
    }

    @Override
    public void getActiveDrivers(LatLng currentLatLng, GoogleMap googleMap, List<Marker> driversMarkers) {
        geofireProvider.getActiveDrivers(currentLatLng, 10).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                //ANADIMOS MARCADORES DE LOS CONDUCTORES CONECTANDOSE
                mapClientPresenter.addActiveDrivers(location, dataSnapshot);
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                //QUITAR MARCADORES DE CONDUCTORES DESCONECTADOS
                mapClientPresenter.removeDriverMarkers(dataSnapshot);
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                //ACTUALIZANDO EN TIEMPO REAL A LOS CONDUCTORES
                for(Marker marker: driversMarkers){
                    if(marker.getTag() != null){
                        if(marker.getTag().equals(dataSnapshot.getKey()))
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onCameraListener(Context context, GoogleMap map) {
        onCameraIdleListener = () -> {
            try {
                Geocoder geocoder = new Geocoder(context);
                LatLng latLng = MapClientActivity.map.getCameraPosition().target;
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String city = addressList.get(0).getLocality();
                String country = addressList.get(0).getCountryName();
                String adress = addressList.get(0).getAddressLine(0);

                if(MapClientActivity.mOriginSelect)
                    mapClientPresenter.setAutoCompleteInfoOrigin(adress, city, latLng);
                else
                    mapClientPresenter.setAutoCompleteInfoDestination(adress, city, latLng);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        };

        mapClientPresenter.setCameraListener(onCameraIdleListener);
    }

    @Override
    public void setOnPlaceSelectListener() {
        MapClientActivity.autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mapClientPresenter.setOriginSelected(place.getName(), place.getLatLng());
                Log.d("PLACEEEEEEEEEEEEEEEE", MapClientActivity.origin + " " + MapClientActivity.originLatLng.latitude + " " + MapClientActivity.originLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        MapClientActivity.autocompleteSupportFragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mapClientPresenter.setDestinationSelected(place.getName(), place.getLatLng());
                Log.d("PLACEEEEEEEEEEEEEEEE",  MapClientActivity.destination + " " +  MapClientActivity.destinationLatLng.latitude + " " +  MapClientActivity.destinationLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

}

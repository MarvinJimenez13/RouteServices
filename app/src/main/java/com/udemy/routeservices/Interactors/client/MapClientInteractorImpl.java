package com.udemy.routeservices.Interactors.client;

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
import com.udemy.routeservices.Interfaces.client.map_client.MapClientInteractor;
import com.udemy.routeservices.Interfaces.client.map_client.MapClientPresenter;
import com.udemy.routeservices.Models.DriverLocation;
import com.udemy.routeservices.Utils.CarMoveAnim;
import com.udemy.routeservices.Utils.providers.GeofireProvider;
import com.udemy.routeservices.Views.client.MapClientActivity;

import java.util.ArrayList;
import java.util.List;

public class MapClientInteractorImpl implements MapClientInteractor {

    private GeofireProvider geofireProvider;
    private MapClientPresenter mapClientPresenter;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private ArrayList<DriverLocation> driversLocation = new ArrayList<>();

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
                DriverLocation driverLocation = new DriverLocation();
                driverLocation.setId(dataSnapshot.getKey());
                driversLocation.add(driverLocation);
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                //QUITAR MARCADORES DE CONDUCTORES DESCONECTADOS
                mapClientPresenter.removeDriverMarkers(dataSnapshot);
                //Eliminamos el registro del driver
                driversLocation.remove(getPositionDriver(dataSnapshot.getKey()));
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                //ACTUALIZANDO EN TIEMPO REAL A LOS CONDUCTORES
                for(Marker marker: driversMarkers){
                    LatLng start = new LatLng(location.latitude, location.longitude);
                    LatLng end = null;
                    int position = getPositionDriver(marker.getTag().toString());//devuelve el id del conductor
                    if(marker.getTag() != null){
                        if(marker.getTag().equals(dataSnapshot.getKey())){
                            if(driversLocation.get(position).getLatLng() != null)
                                end = driversLocation.get(position).getLatLng();

                            driversLocation.get(position).setLatLng(new LatLng(location.latitude, location.longitude));

                            if(end != null)
                                CarMoveAnim.carAnim(marker, end, start);
                            //marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
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

    private int getPositionDriver(String id){
        int position = 0;
        for (int i = 0; i < driversLocation.size(); i++){
            if(id.equals(driversLocation.get(i).getId())){
                position = i;
                break;
            }
        }

        return position;
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

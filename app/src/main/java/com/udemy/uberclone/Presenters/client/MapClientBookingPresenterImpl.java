package com.udemy.uberclone.Presenters.client;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.uberclone.Interactors.client.MapClientBookingInteractorImpl;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingInteractor;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingPresenter;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingView;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public class MapClientBookingPresenterImpl implements MapClientBookingPresenter {

    private MapClientBookingView mapClientBookingView;
    private MapClientBookingInteractor mapClientBookingInteractor;

    public MapClientBookingPresenterImpl(MapClientBookingView mapClientBookingView){
        this.mapClientBookingView = mapClientBookingView;
        mapClientBookingInteractor = new MapClientBookingInteractorImpl(this);
    }

    @Override
    public void getClientBooking(AuthProvider authProvider) {
        mapClientBookingInteractor.getClientBooking(authProvider);
    }

    @Override
    public void setLugarRecogida(String origin, String destination, double originLat, double originLng, double destinationLat, double destinationLng) {
        if(mapClientBookingView != null)
            mapClientBookingView.setLugarRecogida(origin, destination, originLat, originLng, destinationLat, destinationLng);
    }

    @Override
    public void setDatosConductor(String name, String email) {
        if(mapClientBookingView != null)
            mapClientBookingView.setDatosConductor(name, email);
    }

    @Override
    public void setDriverLocation(double lat, double lng) {
        if(mapClientBookingView != null)
            mapClientBookingView.setDriverLocation(lat, lng);
    }

    @Override
    public void getStatusBooking(AuthProvider authProvider, Context context) {
        mapClientBookingInteractor.getStatusBooking(authProvider, context);
    }

    @Override
    public void startBooking() {
        if(mapClientBookingView != null)
            mapClientBookingView.startBooking();
    }

    @Override
    public void finishBooking() {
        if(mapClientBookingView != null)
            mapClientBookingView.finishBooking();
    }

    @Override
    public void removeListeners(String idDriver, AuthProvider authProvider) {
        mapClientBookingInteractor.removeListeners(idDriver, authProvider);
    }

    @Override
    public void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context) {
        mapClientBookingInteractor.drawRoute(originLatLng, destinationLatLng, context);
    }

    @Override
    public void setPolylineRoute(PolylineOptions polylineOptions) {
        if(mapClientBookingView != null)
            mapClientBookingView.setPolylineRoute(polylineOptions);
    }

}

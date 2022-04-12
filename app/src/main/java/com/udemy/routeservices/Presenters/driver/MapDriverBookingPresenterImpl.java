package com.udemy.routeservices.Presenters.driver;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.routeservices.Interactors.driver.MapDriverBookingInteractorImpl;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingInteractor;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingPresenter;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingView;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public class MapDriverBookingPresenterImpl implements MapDriverBookingPresenter {

    private MapDriverBookingView mapDriverBookingView;
    private MapDriverBookingInteractor mapDriverBookingInteractor;

    public MapDriverBookingPresenterImpl(MapDriverBookingView mapDriverBookingView){
        this.mapDriverBookingView = mapDriverBookingView;
        mapDriverBookingInteractor = new MapDriverBookingInteractorImpl(this);
    }

    @Override
    public void getClient(String idClientBooking) {
        mapDriverBookingInteractor.getClient(idClientBooking);
    }

    @Override
    public void getClientBooking(String idClientBooking, LatLng currentLatLng, Context context) {
        mapDriverBookingInteractor.getClientBooking(idClientBooking, currentLatLng, context);
    }

    @Override
    public void setClientInfo(String nombre) {
        if(mapDriverBookingView != null)
            mapDriverBookingView.setClientInfo(nombre);
    }

    @Override
    public void setRouteInfo(String origin, String destination, double originLat, double originLng) {
        if(mapDriverBookingView != null)
            mapDriverBookingView.setRouteInfo(origin, destination, originLat, originLng);
    }

    @Override
    public void setPolylineRoute(PolylineOptions polylineOptions) {
        if(mapDriverBookingView != null)
            mapDriverBookingView.setPolylineRoute(polylineOptions);
    }

    @Override
    public void updateLocation(AuthProvider authProvider, LatLng currentLatLng) {
        mapDriverBookingInteractor.updateLocation(authProvider, currentLatLng);
    }

    @Override
    public void showMsg(String text) {
        if(mapDriverBookingView != null)
            mapDriverBookingView.showMsg(text);
    }

    @Override
    public void iniciarViaje(LatLng currentLatLng, String idClientBooking, Context context) {
        mapDriverBookingInteractor.iniciarViaje(currentLatLng, idClientBooking, context);
    }

    @Override
    public void setInfoInicioViaje(LatLng destinationLatLng) {
        if(mapDriverBookingView != null)
            mapDriverBookingView.setInfoInicioViaje(destinationLatLng);
    }

    @Override
    public void finalizarViaje(String idClientBooking, AuthProvider authProvider, Context context) {
        mapDriverBookingInteractor.finalizarViaje(idClientBooking, authProvider, context);
    }

    @Override
    public void toCalificationActivity() {
        if(mapDriverBookingView != null)
            mapDriverBookingView.toCalificationActivity();
    }

}

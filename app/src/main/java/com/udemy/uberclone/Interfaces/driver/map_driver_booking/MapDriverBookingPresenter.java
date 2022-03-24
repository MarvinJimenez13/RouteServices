package com.udemy.uberclone.Interfaces.driver.map_driver_booking;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public interface MapDriverBookingPresenter {

    void getClient(String idClientBooking);

    void getClientBooking(String idClientBooking, LatLng currentLatLng, Context context);

    void setClientInfo(String nombre);

    void setRouteInfo(String origin, String destination, double originLat, double originLng);

    void setPolylineRoute(PolylineOptions polylineOptions);

    void updateLocation(AuthProvider authProvider, LatLng currentLatLng);

    void showMsg(String text);

    void iniciarViaje(LatLng currentLatLng, String idClientBooking, Context context);

    void setInfoInicioViaje(LatLng destinationLatLng);

    void finalizarViaje(String idClientBooking, AuthProvider authProvider, Context context);

    void toCalificationActivity();

}

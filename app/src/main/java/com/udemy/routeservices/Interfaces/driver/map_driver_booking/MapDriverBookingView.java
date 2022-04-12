package com.udemy.routeservices.Interfaces.driver.map_driver_booking;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public interface MapDriverBookingView {

    void setClientInfo(String nombre);

    void setRouteInfo(String origin, String destination, double originLat, double originLng);

    void setPolylineRoute(PolylineOptions polylineOptions);

    void showMsg(String text);

    void setInfoInicioViaje(LatLng destinationLatLng);

    void toCalificationActivity();

}

package com.udemy.routeservices.Interfaces.client.map_client_booking;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public interface MapClientBookingPresenter {

    void getClientBooking(AuthProvider authProvider);

    void setLugarRecogida(String origin, String destination, double originLat, double originLng, double destinationLat, double destinationLng);

    void setDatosConductor(String name, String email);

    void setDriverLocation(double lat, double lng);

    void getStatusBooking(AuthProvider authProvider, Context context);

    void startBooking();

    void finishBooking();

    void removeListeners(String idDriver, AuthProvider authProvider);

    void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context);

    void setPolylineRoute(PolylineOptions polylineOptions);

}

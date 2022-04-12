package com.udemy.routeservices.Interfaces.client.map_client_booking;

import com.google.android.gms.maps.model.PolylineOptions;

public interface MapClientBookingView {

    void setLugarRecogida(String origin, String destination, double originLat, double originLng, double destinationLat, double destinationLng);

    void setDatosConductor(String name, String email);

    void setDriverLocation(double lat, double lng);

    void startBooking();

    void finishBooking();

    void setPolylineRoute(PolylineOptions polylineOptions);

}

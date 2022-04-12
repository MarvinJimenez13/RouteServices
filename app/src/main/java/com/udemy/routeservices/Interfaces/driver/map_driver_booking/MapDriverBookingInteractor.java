package com.udemy.routeservices.Interfaces.driver.map_driver_booking;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public interface MapDriverBookingInteractor {

    void getClient(String idClientBooking);

    void getClientBooking(String idClientBooking, LatLng currentLatLng, Context context);

    void updateLocation(AuthProvider authProvider, LatLng currentLatLng);

    void iniciarViaje(LatLng currentLatLng, String idClientBooking, Context context);

    void finalizarViaje(String idClientBooking, AuthProvider authProvider, Context context);

}

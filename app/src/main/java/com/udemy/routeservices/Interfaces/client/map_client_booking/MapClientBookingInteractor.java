package com.udemy.routeservices.Interfaces.client.map_client_booking;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public interface MapClientBookingInteractor {

    void getClientBooking(AuthProvider authProvider);

    void getStatusBooking(AuthProvider authProvider, Context context);

    void removeListeners(String idDriver, AuthProvider authProvider);

    void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context);

}

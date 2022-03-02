package com.udemy.uberclone.Interfaces.client.map_client_booking;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public interface MapClientBookingInteractor {

    void getClientBooking(AuthProvider authProvider);

    void getStatusBooking(AuthProvider authProvider);

    void removeListeners(String idDriver, AuthProvider authProvider);

    void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context);

}

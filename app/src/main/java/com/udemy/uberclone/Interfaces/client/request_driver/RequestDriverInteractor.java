package com.udemy.uberclone.Interfaces.client.request_driver;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.uberclone.Models.ClientBooking;
import com.udemy.uberclone.Models.FCMBody;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public interface RequestDriverInteractor {

    void getClosestDrivers(LatLng originLatLng, double radius);

    void createClientBooking(LatLng originLatLng, LatLng driverFoundLatLng, String driverFoundID, Context context);

    void sendNotification(FCMBody body, ClientBooking clientBooking, AuthProvider authProvider);

    void removeListener(AuthProvider authProvider);

}

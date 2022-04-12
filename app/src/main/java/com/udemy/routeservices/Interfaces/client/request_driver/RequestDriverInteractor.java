package com.udemy.routeservices.Interfaces.client.request_driver;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.routeservices.Models.ClientBooking;
import com.udemy.routeservices.Models.FCMBody;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public interface RequestDriverInteractor {

    void getClosestDrivers(LatLng originLatLng, double radius);

    void createClientBooking(LatLng originLatLng, LatLng driverFoundLatLng, String driverFoundID, Context context);

    void sendNotification(FCMBody body, ClientBooking clientBooking, AuthProvider authProvider);

    void removeListener(AuthProvider authProvider);

}

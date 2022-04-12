package com.udemy.routeservices.Interfaces.client.request_driver;

import com.firebase.geofire.GeoLocation;

public interface RequestDriverView {

    void driverFound(String key, GeoLocation location);

    void driverNotFound();

    void buildNotification(String token, String time, String km);

    void showError(String error);

    void success(String mensaje);

    void toMapClientBooking();

    void toMapClient();

}

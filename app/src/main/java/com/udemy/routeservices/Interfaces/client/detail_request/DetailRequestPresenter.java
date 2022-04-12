package com.udemy.routeservices.Interfaces.client.detail_request;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public interface DetailRequestPresenter {

    void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context);

    void setRoute(String distance, String tiempo, PolylineOptions polylineOptions);

}

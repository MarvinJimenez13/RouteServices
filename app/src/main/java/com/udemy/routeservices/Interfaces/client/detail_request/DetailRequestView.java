package com.udemy.routeservices.Interfaces.client.detail_request;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DetailRequestView {

    void setRoute(String distance, String tiempo, PolylineOptions polylineOptions);

}

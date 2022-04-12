package com.udemy.routeservices.Models;

import com.google.android.gms.maps.model.LatLng;

//CLASE AUXILIAR PARA ANIMAR EL MOVIMIENTO DE LOS CONDUCTORES EN EL MAPA DEL CLIENTE
public class DriverLocation {

    private String id;
    private LatLng latLng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}

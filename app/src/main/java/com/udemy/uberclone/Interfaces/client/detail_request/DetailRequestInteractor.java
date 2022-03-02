package com.udemy.uberclone.Interfaces.client.detail_request;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

public interface DetailRequestInteractor {

    void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context);

}

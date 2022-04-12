package com.udemy.routeservices.Presenters.client;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.routeservices.Interactors.client.DetailRequestInteractorImpl;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestInteractor;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestPresenter;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestView;

public class DetailRequestPresenterImpl implements DetailRequestPresenter {

    private DetailRequestView detailRequestView;
    private DetailRequestInteractor detailRequestInteractor;

    public DetailRequestPresenterImpl(DetailRequestView detailRequestView){
        this.detailRequestView = detailRequestView;
        detailRequestInteractor = new DetailRequestInteractorImpl(this);
    }

    @Override
    public void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context) {
        detailRequestInteractor.drawRoute(originLatLng, destinationLatLng, context);
    }

    @Override
    public void setRoute(String distance, String tiempo, PolylineOptions polylineOptions) {
        if(detailRequestView != null)
            detailRequestView.setRoute(distance, tiempo, polylineOptions);
    }

}

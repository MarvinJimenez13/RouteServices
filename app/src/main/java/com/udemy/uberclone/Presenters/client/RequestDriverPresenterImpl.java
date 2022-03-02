package com.udemy.uberclone.Presenters.client;

import android.content.Context;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.uberclone.Interactors.client.RequestDriverInteractorImpl;
import com.udemy.uberclone.Interfaces.client.request_driver.RequestDriverInteractor;
import com.udemy.uberclone.Interfaces.client.request_driver.RequestDriverPresenter;
import com.udemy.uberclone.Interfaces.client.request_driver.RequestDriverView;
import com.udemy.uberclone.Models.ClientBooking;
import com.udemy.uberclone.Models.FCMBody;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public class RequestDriverPresenterImpl implements RequestDriverPresenter {

    private RequestDriverView requestDriverView;
    private RequestDriverInteractor requestDriverInteractor;

    public RequestDriverPresenterImpl(RequestDriverView requestDriverView){
        this.requestDriverView = requestDriverView;
        requestDriverInteractor = new RequestDriverInteractorImpl(this);
    }

    @Override
    public void getClosestDrivers(LatLng originLatLng, double radius) {
        requestDriverInteractor.getClosestDrivers(originLatLng, radius);
    }

    @Override
    public void createClientBooking(LatLng originLatLng, LatLng driverFoundLatLng, String driverFoundID, Context context) {
        requestDriverInteractor.createClientBooking(originLatLng, driverFoundLatLng, driverFoundID, context);
    }

    @Override
    public void driverFound(String key, GeoLocation location) {
        if(requestDriverView != null)
            requestDriverView.driverFound(key, location);
    }

    @Override
    public void driverNotFound() {
        if(requestDriverView != null)
            requestDriverView.driverNotFound();
    }

    @Override
    public void buildNotification(String token, String time, String km) {
        if(requestDriverView != null)
            requestDriverView.buildNotification(token, time, km);
    }

    @Override
    public void sendNotification(FCMBody body, ClientBooking clientBooking, AuthProvider authProvider) {
        requestDriverInteractor.sendNotification(body, clientBooking, authProvider);
    }

    @Override
    public void showError(String error) {
        if(requestDriverView != null)
            requestDriverView.showError(error);
    }

    @Override
    public void success(String mensaje) {
        if(requestDriverView != null)
            requestDriverView.success(mensaje);
    }

    @Override
    public void toMapClientBooking() {
        if(requestDriverView != null)
            requestDriverView.toMapClientBooking();
    }

    @Override
    public void toMapClient() {
        if(requestDriverView != null)
            requestDriverView.toMapClient();
    }

    @Override
    public void removeListener(AuthProvider authProvider) {
        requestDriverInteractor.removeListener(authProvider);
    }

}

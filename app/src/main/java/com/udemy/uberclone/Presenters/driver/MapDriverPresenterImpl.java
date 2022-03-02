package com.udemy.uberclone.Presenters.driver;

import com.google.android.gms.maps.model.LatLng;
import com.udemy.uberclone.Interactors.driver.MapDriverInteractorImpl;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverInteractor;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverPresenter;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverView;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public class MapDriverPresenterImpl implements MapDriverPresenter {

    private MapDriverView mapDriverView;
    private MapDriverInteractor mapDriverInteractor;

    public MapDriverPresenterImpl(MapDriverView mapDriverView){
        this.mapDriverView = mapDriverView;
        mapDriverInteractor = new MapDriverInteractorImpl(this);
    }

    @Override
    public void isDriverWorking(AuthProvider authProvider) {
        mapDriverInteractor.isDriverWorking(authProvider);
    }

    @Override
    public void removeLocation(AuthProvider authProvider) {
        mapDriverInteractor.removeLocation(authProvider);
    }

    @Override
    public void saveLocation(AuthProvider authProvider, LatLng currentLatLng) {
        mapDriverInteractor.saveLocation(authProvider, currentLatLng);
    }

    @Override
    public void generateTokenNoti(AuthProvider authProvider) {
        mapDriverInteractor.generateTokenNoti(authProvider);
    }

    @Override
    public void removeEventListener(AuthProvider authProvider) {
        mapDriverInteractor.removeEventListener(authProvider);
    }

    @Override
    public void disconnect() {
        if(mapDriverView != null)
            mapDriverView.disconnect();
    }

}

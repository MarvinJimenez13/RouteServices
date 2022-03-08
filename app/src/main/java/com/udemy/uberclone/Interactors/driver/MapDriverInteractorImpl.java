package com.udemy.uberclone.Interactors.driver;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverInteractor;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverPresenter;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.GeofireProvider;
import com.udemy.uberclone.Utils.providers.TokenNotiProvider;

public class MapDriverInteractorImpl implements MapDriverInteractor {

    private MapDriverPresenter mapDriverPresenter;
    private ValueEventListener valueEventListener;
    private GeofireProvider geofireProvider;
    private TokenNotiProvider tokenNotiProvider;

    public MapDriverInteractorImpl(MapDriverPresenter mapDriverPresenter){
        this.mapDriverPresenter = mapDriverPresenter;
        geofireProvider = new GeofireProvider("active_drivers");
        tokenNotiProvider = new TokenNotiProvider();
    }

    @Override
    public void isDriverWorking(AuthProvider authProvider) {
        //addValueEvent.... echuchador en tiempo real, debemos pararlo.
        valueEventListener = geofireProvider.isDriverWorking(authProvider.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    mapDriverPresenter.disconnect();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void removeLocation(AuthProvider authProvider) {
        geofireProvider.removeLocation(authProvider.getID());
    }

    @Override
    public void saveLocation(AuthProvider authProvider, LatLng currentLatLng) {
        geofireProvider.saveLocation(authProvider.getID(), currentLatLng);
    }

    @Override
    public void generateTokenNoti(AuthProvider authProvider) {
        tokenNotiProvider.create(authProvider.getID());
    }

    @Override
    public void removeEventListener(AuthProvider authProvider) {
        if(valueEventListener != null && authProvider.getID() != null && authProvider.existSession())
            geofireProvider.isDriverWorking(authProvider.getID()).removeEventListener(valueEventListener);
    }

    @Override
    public void deleteDriverWorking(AuthProvider authProvider, boolean connect) {
        geofireProvider.deleteDriverWorking(authProvider.getID()).addOnSuccessListener(listener ->{
            isDriverWorking(authProvider);

            //VALIDAMOS SI VENIMOS DE CALIFICATIONCLIENTACTIVITY PARA VOLVER A CONECTARNOS
            if(connect)
                mapDriverPresenter.startLocation();
        });
    }

}

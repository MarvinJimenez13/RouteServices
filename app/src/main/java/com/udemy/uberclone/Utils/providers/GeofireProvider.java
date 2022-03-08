package com.udemy.uberclone.Utils.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {

    private DatabaseReference databaseReference;
    private GeoFire geoFire;

    public GeofireProvider(String reference){
        databaseReference = FirebaseDatabase.getInstance().getReference().child(reference);
        geoFire = new GeoFire(databaseReference);
    }

    public void saveLocation(String idDriver, LatLng latLng){
        geoFire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    public void removeLocation(String idDriver){
        geoFire.removeLocation(idDriver);
    }

    public GeoQuery getActiveDrivers(LatLng latLng, double radius){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
        geoQuery.removeAllListeners();

        return geoQuery;
    }

    //METODO PARA OBTENER LA UBICACION EN TIEMPO REAL DEL CONDUCTOR EN LA REFEENCIA DRIVERSWORKING
    public DatabaseReference getDriverLocation(String idDriver){
        if(databaseReference.child(idDriver) != null)
            return databaseReference.child(idDriver).child("l");
        else return null;
    }

    //METODO PARA SABER SI EL DRIVER ESTA TRABAJANDO Y QUITARLO DE ACTIVE DRIVERS
    public DatabaseReference isDriverWorking(String idDriver){
        return FirebaseDatabase.getInstance().getReference().child("drivers_working").child(idDriver);
    }

    //METODO PARA ELIMINAR LA REFERENCIA
    public Task<Void> deleteDriverWorking(String idDriver){
        return FirebaseDatabase.getInstance().getReference().child("drivers_working").child(idDriver).removeValue();
    }

}

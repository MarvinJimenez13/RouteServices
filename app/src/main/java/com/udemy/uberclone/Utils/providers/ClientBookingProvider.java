package com.udemy.uberclone.Utils.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udemy.uberclone.Models.ClientBooking;

import java.util.HashMap;
import java.util.Map;

/*
*
* CLASE PARA MANEJAR LOS NODOS DE FIREBASE CLIENTBOOKING
*
* */
public class ClientBookingProvider {

    private DatabaseReference databaseReference;

    public ClientBookingProvider(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }

    public Task<Void> create(ClientBooking clientBooking){
        return databaseReference.child(clientBooking.getIdClient()).setValue(clientBooking);
    }

    public Task<Void> updateStatus(String idClientBooking, String estatus){
        Map<String, Object> map = new HashMap<>();
        map.put("status", estatus);
        return databaseReference.child(idClientBooking).updateChildren(map);
    }

    //Metodo para crear id unico de HistoryBooking dentro de ClientBooking.
    public Task<Void> updateIDHistoryBooking(String idClientBooking){
        String idPush = databaseReference.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("idHistoryBooking", idPush);
        return databaseReference.child(idClientBooking).updateChildren(map);
    }

    //METODO PARA OBTENER EL ESTATUS DE LA NUTIFICACION Y VER SI EL CONDUCTOR ACEPTO EL VIAJE
    public DatabaseReference getStatus(String idClientBooking){
        return databaseReference.child(idClientBooking).child("status");
    }

    //OBTENER TODA LA REFERENCIA AL BOOKING
    public DatabaseReference getClientBooking(String idClientBooking){
        return databaseReference.child(idClientBooking);
    }

}

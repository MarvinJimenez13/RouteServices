package com.udemy.routeservices.Utils.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udemy.routeservices.Models.HistoryBooking;
import java.util.HashMap;
import java.util.Map;

/*
 *
 * CLASE PARA MANEJAR LOS NODOS DE FIREBASE HISTORYBOOKING
 *
 * */
public class HistoryBookingProvider {

    private DatabaseReference databaseReference;

    public HistoryBookingProvider(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("HistoryBooking");
    }

    public Task<Void> create(HistoryBooking historyBooking){
        return databaseReference.child(historyBooking.getIdHistoryBooking()).setValue(historyBooking);
    }

    public Task<Void> updateCalificationClient(String idHistoryBooking, float calificationClient){
        Map<String, Object> map = new HashMap<>();
        map.put("calificationClient", calificationClient);
        return databaseReference.child(idHistoryBooking).updateChildren(map);
    }

    public Task<Void> updateCalificationDriver(String idHistoryBooking, float calificationClient){
        Map<String, Object> map = new HashMap<>();
        map.put("calificationDriver", calificationClient);
        return databaseReference.child(idHistoryBooking).updateChildren(map);
    }

    public DatabaseReference getHistoryBooking(String idHistoryBooking){
        return databaseReference.child(idHistoryBooking);
    }

}
package com.udemy.uberclone.Interactors.client;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.uberclone.Interfaces.client.calification_driver.CalificationDriverInteractor;
import com.udemy.uberclone.Interfaces.client.calification_driver.CalificationDriverPresenter;
import com.udemy.uberclone.Models.ClientBooking;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.Utils.providers.ClientBookingProvider;
import com.udemy.uberclone.Utils.providers.HistoryBookingProvider;
import java.util.Date;

public class CalificationDriverInteractorImpl implements CalificationDriverInteractor {

    private CalificationDriverPresenter calificationDriverPresenter;
    private ClientBookingProvider clientBookingProvider;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;

    public CalificationDriverInteractorImpl(CalificationDriverPresenter calificationDriverPresenter){
        this.calificationDriverPresenter = calificationDriverPresenter;
        clientBookingProvider = new ClientBookingProvider();
        historyBookingProvider = new HistoryBookingProvider();
    }

    @Override
    public void getClientBooking(String idClient) {
        clientBookingProvider.getClientBooking(idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //El constructor debe estar vacio y los nombres iguales a la BD o dara error.
                    ClientBooking clientBooking = snapshot.getValue(ClientBooking.class);
                    calificationDriverPresenter.setRoute(clientBooking.getOrigin(), clientBooking.getDestination());
                    historyBooking = new HistoryBooking(
                            clientBooking.getIdHistoryBooking(),
                            clientBooking.getIdClient(),
                            clientBooking.getIdDriver(),
                            clientBooking.getDestination(),
                            clientBooking.getOrigin(),
                            clientBooking.getTime(),
                            clientBooking.getKm(),
                            clientBooking.getStatus(),
                            clientBooking.getOriginLat(),
                            clientBooking.getOriginLng(),
                            clientBooking.getDestinationLat(),
                            clientBooking.getDestinationLng()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void calificar(float calification) {
        if(calification > 0){
            historyBooking.setCalificationDriver(calification);
            historyBooking.setTimestamp(new Date().getTime());
            historyBookingProvider.getHistoryBooking(historyBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        historyBookingProvider.updateCalificationDriver(historyBooking.getIdHistoryBooking(), calification).addOnSuccessListener(response ->{
                            calificationDriverPresenter.successCalification("Conductor calificado!");
                        });
                    }else{
                        historyBookingProvider.create(historyBooking).addOnSuccessListener(unused -> {
                            calificationDriverPresenter.successCalification("Conductor calificado!");
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else
            calificationDriverPresenter.showError("Debes ingresar una calificacion");
    }

}

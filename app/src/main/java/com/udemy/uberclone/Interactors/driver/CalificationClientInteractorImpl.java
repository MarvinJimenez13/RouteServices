package com.udemy.uberclone.Interactors.driver;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.uberclone.Interfaces.driver.calification_client.CalificationClientInteractor;
import com.udemy.uberclone.Interfaces.driver.calification_client.CalificationClientPresenter;
import com.udemy.uberclone.Models.ClientBooking;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.Utils.providers.ClientBookingProvider;
import com.udemy.uberclone.Utils.providers.HistoryBookingProvider;
import java.util.Date;

public class CalificationClientInteractorImpl implements CalificationClientInteractor {

    private CalificationClientPresenter calificationClientPresenter;
    private ClientBookingProvider clientBookingProvider;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;

    public CalificationClientInteractorImpl(CalificationClientPresenter calificationClientPresenter){
        this.calificationClientPresenter = calificationClientPresenter;
        clientBookingProvider = new ClientBookingProvider();
        historyBookingProvider = new HistoryBookingProvider();
    }

    @Override
    public void getClientBooking(String idClientBooking) {
        calificationClientPresenter.showProgress();
        clientBookingProvider.getClientBooking(idClientBooking).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                calificationClientPresenter.hideProgress();
                if(snapshot.exists()){
                    //El constructor debe estar vacio y los nombres iguales a la BD o dara error.
                    ClientBooking clientBooking = snapshot.getValue(ClientBooking.class);
                    calificationClientPresenter.setInfoRoute(clientBooking.getOrigin(), clientBooking.getDestination());
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
                calificationClientPresenter.hideProgress();
            }
        });
    }

    @Override
    public void calificar(float califcacion) {
        if(califcacion > 0){
            historyBooking.setCalificationClient(califcacion);
            historyBooking.setTimestamp(new Date().getTime());
            calificationClientPresenter.showProgress();
            historyBookingProvider.getHistoryBooking(historyBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    calificationClientPresenter.hideProgress();
                    if(snapshot.exists()){
                        historyBookingProvider.updateCalificationClient(historyBooking.getIdHistoryBooking(), califcacion).addOnSuccessListener(LISTENER ->{
                            calificationClientPresenter.calificationSuccess();
                        });
                    }else{
                        historyBookingProvider.create(historyBooking).addOnSuccessListener(unused -> {
                            calificationClientPresenter.calificationSuccess();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    calificationClientPresenter.hideProgress();
                }
            });
        }else
            calificationClientPresenter.showError("Debes ingresar una calificacion");
    }

}

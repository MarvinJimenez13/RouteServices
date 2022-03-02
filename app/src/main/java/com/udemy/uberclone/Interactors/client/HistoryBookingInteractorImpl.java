package com.udemy.uberclone.Interactors.client;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.udemy.uberclone.Interfaces.client.history_booking.HistoryBookingInteractor;
import com.udemy.uberclone.Interfaces.client.history_booking.HistoryBookingPresenter;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public class HistoryBookingInteractorImpl implements HistoryBookingInteractor {

    private HistoryBookingPresenter historyBookingPresenter;

    public HistoryBookingInteractorImpl(HistoryBookingPresenter historyBookingPresenter){
        this.historyBookingPresenter = historyBookingPresenter;
    }

    @Override
    public void getHistory(AuthProvider authProvider) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("HistoryBooking")
                .orderByChild("idClient")
                .equalTo(authProvider.getID());

        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();

        historyBookingPresenter.setHistoryAdapter(options);
    }

}

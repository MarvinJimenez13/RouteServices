package com.udemy.uberclone.Presenters.client;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.udemy.uberclone.Interactors.client.HistoryBookingInteractorImpl;
import com.udemy.uberclone.Interfaces.client.history_booking.HistoryBookingInteractor;
import com.udemy.uberclone.Interfaces.client.history_booking.HistoryBookingPresenter;
import com.udemy.uberclone.Interfaces.client.history_booking.HistoryBookingView;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public class HistoryBookingPresenterImpl implements HistoryBookingPresenter {

    private HistoryBookingView historyBookingView;
    private HistoryBookingInteractor historyBookingInteractor;

    public HistoryBookingPresenterImpl(HistoryBookingView historyBookingView){
        this.historyBookingView = historyBookingView;
        this.historyBookingInteractor = new HistoryBookingInteractorImpl(this);
    }

    @Override
    public void getHistory(AuthProvider authProvider) {
        historyBookingInteractor.getHistory(authProvider);
    }

    @Override
    public void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options) {
        if(historyBookingView != null)
            historyBookingView.setHistoryAdapter(options);
    }

}

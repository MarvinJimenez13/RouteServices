package com.udemy.uberclone.Interfaces.client.history_booking;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.Utils.providers.AuthProvider;

public interface HistoryBookingPresenter {

    void getHistory(AuthProvider authProvider);

    void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options);

}

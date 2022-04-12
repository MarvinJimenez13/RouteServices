package com.udemy.routeservices.Interfaces.client.history_booking;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.udemy.routeservices.Models.HistoryBooking;
import com.udemy.routeservices.Utils.providers.AuthProvider;

public interface HistoryBookingPresenter {

    void getHistory(AuthProvider authProvider);

    void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options);

}

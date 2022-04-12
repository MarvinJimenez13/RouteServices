package com.udemy.routeservices.Interfaces.client.history_booking;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.udemy.routeservices.Models.HistoryBooking;

public interface HistoryBookingView {

    void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options);

}

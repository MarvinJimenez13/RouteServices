package com.udemy.uberclone.Interfaces.client.history_booking;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.udemy.uberclone.Models.HistoryBooking;

public interface HistoryBookingView {

    void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options);

}

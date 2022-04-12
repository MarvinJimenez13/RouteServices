package com.udemy.routeservices.Views.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.udemy.routeservices.Interfaces.client.history_booking.HistoryBookingPresenter;
import com.udemy.routeservices.Interfaces.client.history_booking.HistoryBookingView;
import com.udemy.routeservices.Models.HistoryBooking;
import com.udemy.routeservices.Presenters.client.HistoryBookingPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Views.adapters.HistoryBookingClientAdapter;
import com.udemy.routeservices.Utils.includes.MyToolbar;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryBookingClientActivity extends AppCompatActivity implements HistoryBookingView {

    private Unbinder mUnbinder;
    private HistoryBookingClientAdapter clientAdapter;
    private AuthProvider authProvider;
    private HistoryBookingPresenter historyBookingPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_client);
        mUnbinder = ButterKnife.bind(this);

        historyBookingPresenter = new HistoryBookingPresenterImpl(this);

        MyToolbar.show(HistoryBookingClientActivity.this, "Historial de Viajes", true, toolbar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        authProvider = new AuthProvider();
        historyBookingPresenter.getHistory(authProvider);
    }

    @Override
    protected void onStop() {
        super.onStop();
        clientAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void setHistoryAdapter(FirebaseRecyclerOptions<HistoryBooking> options) {
        clientAdapter = new HistoryBookingClientAdapter(options, HistoryBookingClientActivity.this);
        recyclerView.setAdapter(clientAdapter);
        clientAdapter.startListening();
    }

}
package com.udemy.routeservices.Views.client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.udemy.routeservices.Interfaces.client.calification_driver.CalificationDriverPresenter;
import com.udemy.routeservices.Interfaces.client.calification_driver.CalificationDriverView;
import com.udemy.routeservices.Presenters.client.CalificationDriverPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CalificationDriverActivity extends AppCompatActivity implements CalificationDriverView {

    private Unbinder mUnbinder;
    private AuthProvider authProvider;
    private CalificationDriverPresenter calificationDriverPresenter;

    @BindView(R.id.tvDesdeDesc)
    TextView tvOrigin;
    @BindView(R.id.tvHastaDesc)
    TextView tvDestination;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_driver);
        mUnbinder = ButterKnife.bind(this);

        calificationDriverPresenter = new CalificationDriverPresenterImpl(this);
        authProvider = new AuthProvider();

        calificationDriverPresenter.getClientBooking(authProvider.getID());
    }

    @OnClick(R.id.btnCalificar)
    public void onClick(){
        float calification = ratingBar.getRating();
        calificationDriverPresenter.calificar(calification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void setRoute(String origin, String destination) {
        tvOrigin.setText(origin);
        tvDestination.setText(destination);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void successCalification(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), MapClientActivity.class));
        finish();
    }

}
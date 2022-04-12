package com.udemy.routeservices.Views.driver;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.udemy.routeservices.Interfaces.driver.calification_client.CalificationClientPresenter;
import com.udemy.routeservices.Interfaces.driver.calification_client.CalificationClientView;
import com.udemy.routeservices.Presenters.driver.CalificationClientPresenterImpl;
import com.udemy.routeservices.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CalificationClientActivity extends AppCompatActivity implements CalificationClientView {

    private Unbinder mUnbinder;
    private CalificationClientPresenter calificationClientPresenter;
    private ProgressDialog progressDialog;

    @BindView(R.id.tvDesdeDesc)
    TextView tvOrigin;
    @BindView(R.id.tvHastaDesc)
    TextView tvDestination;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_client);
        mUnbinder = ButterKnife.bind(this);

        calificationClientPresenter = new CalificationClientPresenterImpl(this);
        calificationClientPresenter.getClientBooking(getIntent().getStringExtra("idClient"));
    }


    @OnClick(R.id.btnCalificar)
    public void onClick(){
        float calification = ratingBar.getRating();
        calificationClientPresenter.calificar(calification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(CalificationClientActivity.this, "Cargando", "Espere por favor...");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void calificationSuccess() {
        Toast.makeText(getApplicationContext(), "Cliente calificado!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MapDriverActivity.class);
        intent.putExtra("CONNECT", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void setInfoRoute(String origin, String destination) {
        tvOrigin.setText(origin);
        tvDestination.setText(destination);
    }

}
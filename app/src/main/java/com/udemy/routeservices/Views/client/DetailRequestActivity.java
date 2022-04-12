package com.udemy.routeservices.Views.client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestPresenter;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestView;
import com.udemy.routeservices.Presenters.client.DetailRequestPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.includes.MyToolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback, DetailRequestView {

    private Unbinder mUnbinder;
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private String extraOrigin, extraDestination;
    private double extraOriginLat, extraOriginLng, extraDestinoLat, extraDestinoLng;
    private LatLng originLatLng, destinationLatLng;
    private DetailRequestPresenter detailRequestPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.tvOrigenDesc)
    TextView tvOrigen;
    @BindView(R.id.tvDestinoDesc)
    TextView tvDestino;
    @BindView(R.id.tvDistanciaDesc)
    TextView tvDistancia;
    @BindView(R.id.tvTiempoDesc)
    TextView tvTiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);
        mUnbinder = ButterKnife.bind(this);
        MyToolbar.show(this, "Datos del Viaje", true, toolbar);

        detailRequestPresenter = new DetailRequestPresenterImpl(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapClient);
        supportMapFragment.getMapAsync(this);

        extraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        extraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        extraDestinoLat = getIntent().getDoubleExtra("destination_lat", 0);
        extraDestinoLng = getIntent().getDoubleExtra("destination_lng", 0);
        extraOrigin = getIntent().getStringExtra("origin");
        extraDestination = getIntent().getStringExtra("destination");

        tvOrigen.setText(extraOrigin);
        tvDestino.setText(extraDestination);

        originLatLng = new LatLng(extraOriginLat, extraOriginLng);
        destinationLatLng = new LatLng(extraDestinoLat, extraDestinoLng);
    }

    @OnClick(R.id.btnConfirmar)
    public void onClick(){
        Intent intent = new Intent(this, RequestDriverActivity.class);
        intent.putExtra("originLat", originLatLng.latitude);
        intent.putExtra("originLng", originLatLng.longitude);
        intent.putExtra("destinationLat", destinationLatLng.latitude);
        intent.putExtra("destinationLng", destinationLatLng.longitude);
        intent.putExtra("origin", extraOrigin);
        intent.putExtra("destination", extraDestination);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.addMarker(new MarkerOptions().position(destinationLatLng).title("DESTINO").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));
        map.addMarker(new MarkerOptions().position(originLatLng).title("ORIGEN").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                    .target(originLatLng)
                    .zoom(14f)
                    .build()
        ));

        detailRequestPresenter.drawRoute(originLatLng, destinationLatLng, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void setRoute(String distance, String tiempo, PolylineOptions polylineOptions) {
        if(tvDistancia != null && tvTiempo != null){
            tvDistancia.setText(distance);
            tvTiempo.setText(tiempo);
        }

        map.addPolyline(polylineOptions);
    }

}
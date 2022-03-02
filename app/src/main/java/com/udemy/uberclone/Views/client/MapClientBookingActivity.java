package com.udemy.uberclone.Views.client;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingPresenter;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingView;
import com.udemy.uberclone.Presenters.client.MapClientBookingPresenterImpl;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback, MapClientBookingView {

    public static GoogleMap map;
    private Marker markerDriver;
    public static LatLng driverLatLng;
    private boolean isFirstTime = true;
    private SupportMapFragment supportMapFragment;
    private Unbinder mUnbinder;
    private AuthProvider authProvider;
    public static String idDriver;
    public static LatLng originLatLng, destinationLatLng;
    private MapClientBookingPresenter mapClientBookingPresenter;

    @BindView(R.id.tvNombreConductor)
    TextView tvNombreConductor;
    @BindView(R.id.tvCorreoConductor)
    TextView tvCorreoConductor;
    @BindView(R.id.tvOrigin)
    TextView tvOrigin;
    @BindView(R.id.tvDestination)
    TextView tvDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);
        mUnbinder = ButterKnife.bind(this);

        mapClientBookingPresenter = new MapClientBookingPresenterImpl(this);
        authProvider = new AuthProvider();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapClientBooking);
        supportMapFragment.getMapAsync(this);

        mapClientBookingPresenter.getClientBooking(authProvider);
    }

    @Override
    public void startBooking(){
        map.clear();
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));
        mapClientBookingPresenter.drawRoute(driverLatLng, destinationLatLng, MapClientBookingActivity.this);
    }

    @Override
    public void finishBooking(){
        startActivity(new Intent(this, CalificationDriverActivity.class));
        finish();
    }

    @Override
    public void setPolylineRoute(PolylineOptions polylineOptions) {
        map.addPolyline(polylineOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void setLugarRecogida(String origin, String destination, double originLat, double originLng, double destinationLat, double destinationLng) {
        originLatLng = new LatLng(originLat, originLng);
        destinationLatLng = new LatLng(destinationLat, destinationLng);
        tvOrigin.setText("Lugar de Recogida: " + origin);
        tvDestination.setText("Destino: " + destination);
        //MARCADOR DE LA UBICACION DEL CLIENTE.
        map.addMarker(new MarkerOptions().position(new LatLng(originLat, originLng)).title("Recoger aqui ").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));
    }

    @Override
    public void setDatosConductor(String name, String email) {
        tvNombreConductor.setText(name);
        tvCorreoConductor.setText(email);
    }

    @Override
    public void setDriverLocation(double lat, double lng) {
        driverLatLng = new LatLng(lat, lng);
        //PARA QUE NO SE REDIBUJE POR TODO EL MAPA
        if(markerDriver != null)
            markerDriver.remove();

        markerDriver = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                .title("Tu conductor")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_vehicle)));

        if(isFirstTime){
            isFirstTime = false;
            //RUTA ENTRE UBICACION ACTUAL DEL CONDUCTOR Y LA UBICACION DEL CLIENTE.
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(driverLatLng)
                            .zoom(14f)
                            .build()
            ));

            mapClientBookingPresenter.drawRoute(driverLatLng, originLatLng, MapClientBookingActivity.this);
            mapClientBookingPresenter.getStatusBooking(authProvider);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mapClientBookingPresenter.removeListeners(idDriver, authProvider);
    }

}
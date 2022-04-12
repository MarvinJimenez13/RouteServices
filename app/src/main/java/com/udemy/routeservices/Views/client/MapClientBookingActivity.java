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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.udemy.routeservices.Interfaces.client.map_client_booking.MapClientBookingPresenter;
import com.udemy.routeservices.Interfaces.client.map_client_booking.MapClientBookingView;
import com.udemy.routeservices.Presenters.client.MapClientBookingPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.CarMoveAnim;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;
import com.udemy.routeservices.Utils.providers.AuthProvider;
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
    private LatLng startLatLng, endLatLng;

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

        idDriver = getIntent().getStringExtra("idDriver");

        mapClientBookingPresenter.getClientBooking(authProvider);
    }

    @Override
    public void startBooking(){
        SharedPreferencesUber.getInstance(MapClientBookingActivity.this).guardarStatusClientBooking("START", idDriver);
        map.clear();
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));

        if(driverLatLng != null){
            markerDriver = map.addMarker(new MarkerOptions().position(new LatLng(driverLatLng.latitude, driverLatLng.longitude))
                    .title("Tu conductor")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
        }

        mapClientBookingPresenter.drawRoute(driverLatLng, destinationLatLng, MapClientBookingActivity.this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finishBooking(){
        SharedPreferencesUber.getInstance(MapClientBookingActivity.this).guardarStatusClientBooking(null,null);
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

        if(isFirstTime){
            markerDriver = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title("Tu conductor")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
            isFirstTime = false;
            //RUTA ENTRE UBICACION ACTUAL DEL CONDUCTOR Y LA UBICACION DEL CLIENTE.
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(driverLatLng)
                            .zoom(14f)
                            .build()
            ));

            String status = SharedPreferencesUber.getInstance(MapClientBookingActivity.this).getStatusClientBooking();
            if(status.equals("START"))
                mapClientBookingPresenter.startBooking();
            else{
                mapClientBookingPresenter.drawRoute(driverLatLng, originLatLng, MapClientBookingActivity.this);
                SharedPreferencesUber.getInstance(MapClientBookingActivity.this).guardarStatusClientBooking("RIDE", idDriver);
            }

            mapClientBookingPresenter.getStatusBooking(authProvider, MapClientBookingActivity.this);
        }

        if(startLatLng != null)
            endLatLng = startLatLng;

        startLatLng = new LatLng(lat, lng);
        if(endLatLng != null)
            CarMoveAnim.carAnim(markerDriver, endLatLng, startLatLng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mapClientBookingPresenter.removeListeners(idDriver, authProvider);
    }

}
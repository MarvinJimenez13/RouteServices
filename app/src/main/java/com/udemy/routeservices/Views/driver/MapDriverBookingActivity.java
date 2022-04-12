package com.udemy.routeservices.Views.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.material.button.MaterialButton;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingPresenter;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingView;
import com.udemy.routeservices.Presenters.driver.MapDriverBookingPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.CarMoveAnim;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.PermissionsProvider;
import com.udemy.routeservices.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback, MapDriverBookingView {

    private GoogleMap map;
    private Marker marker;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLatLng;
    private SupportMapFragment supportMapFragment;
    private Unbinder mUnbinder;
    private AuthProvider authProvider;
    private String idClientBooking;
    //PRIMERA VEZ PARA SABER SI ENTRO AL LOCATIONCALLBACK
    private boolean primeraVez = true;
    private MapDriverBookingPresenter mapDriverBookingPresenter;

    @BindView(R.id.tvNombreCliente)
    TextView tvNombreCliente;
    //@BindView(R.id.tvCorreoCliente)
    //TextView tvCorreoCliente;
    @BindView(R.id.tvOrigin)
    TextView tvOrigin;
    @BindView(R.id.tvDestination)
    TextView tvDestination;
    @BindView(R.id.btnIniciarViaje)
    MaterialButton btnIniciarViaje;
    @BindView(R.id.btnFinalizarViaje)
    MaterialButton btnFinalizarViaje;


    private boolean mIsStartLocation = false;
    private LatLng mStartLatLng, mEndLatLng;
    private LocationManager locationManager;

    //ANIMACION DE ICONO UBER
    private LocationListener locationListenerGPS = location -> {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mStartLatLng != null)
            mEndLatLng = mStartLatLng;

        mStartLatLng = new LatLng(currentLatLng.latitude, currentLatLng.longitude);

        if (mEndLatLng != null)
            CarMoveAnim.carAnim(marker, mEndLatLng, mStartLatLng);

        mapDriverBookingPresenter.updateLocation(authProvider, currentLatLng);
    };

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (!mIsStartLocation) {
                        map.clear();

                        marker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("Ubicacion actual")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                        //OBTENER LOCALIZACION DEL USUARIO EN TIEMPO REAL
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15f)
                                        .build()
                        ));

                        mapDriverBookingPresenter.updateLocation(authProvider, currentLatLng);

                        if (primeraVez) {
                            primeraVez = false;
                            //OBTENEMOS BOOKING PARA TRAZAR RUTA
                            mapDriverBookingPresenter.getClientBooking(idClientBooking, currentLatLng, MapDriverBookingActivity.this);
                        }

                        if (ActivityCompat.checkSelfPermission(MapDriverBookingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(MapDriverBookingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);
                        mIsStartLocation = true;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);
        mUnbinder = ButterKnife.bind(this);

        mapDriverBookingPresenter = new MapDriverBookingPresenterImpl(this);

        authProvider = new AuthProvider();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //ID QUE LLEGA DESDE ACCEPTRECEIVER.
        idClientBooking = getIntent().getStringExtra("idClient");
        mapDriverBookingPresenter.getClient(idClientBooking);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDriverBooking);
        supportMapFragment.getMapAsync(this);
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (PermissionsProvider.gpsActived(this)){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }else
                    PermissionsProvider.showAlertDialogGPS(this, this);
            } else
                PermissionsProvider.checkLocationPermissions(this);
        } else {
            if (PermissionsProvider.gpsActived(this))
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            else
                PermissionsProvider.showAlertDialogGPS(this, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(false);
        startLocation();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SETTINGS_REQUEST_CODE && PermissionsProvider.gpsActived(this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } else
            PermissionsProvider.showAlertDialogGPS(this, this);
    }

    @OnClick({R.id.btnIniciarViaje, R.id.btnFinalizarViaje})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnIniciarViaje:
                mapDriverBookingPresenter.iniciarViaje(currentLatLng, idClientBooking, MapDriverBookingActivity.this);
                break;
            case R.id.btnFinalizarViaje:
                if(locationListenerGPS != null)
                    locationManager.removeUpdates(locationListenerGPS);
                mapDriverBookingPresenter.finalizarViaje(idClientBooking, authProvider, MapDriverBookingActivity.this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsProvider.gpsActived(this))
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    else
                        PermissionsProvider.showAlertDialogGPS(this, this);
                } else
                    PermissionsProvider.checkLocationPermissions(this);
            } else
                PermissionsProvider.checkLocationPermissions(this);
        }
    }

    @Override
    public void setClientInfo(String nombre) {
        tvNombreCliente.setText(nombre);
    }

    @Override
    public void setRouteInfo(String origin, String destination, double originLat, double originLng) {
        tvOrigin.setText("Lugar de Recogida: " + origin);
        tvDestination.setText("Destino: " + destination);
        //MARCADOR DE LA UBICACION DEL CLIENTE.
        map.addMarker(new MarkerOptions().position(new LatLng(originLat, originLng)).title("Recoger aqui ").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));
    }

    @Override
    public void setPolylineRoute(PolylineOptions polylineOptions) {
        map.addPolyline(polylineOptions);
    }

    @Override
    public void showMsg(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setInfoInicioViaje(LatLng destinationLatLng) {
        btnIniciarViaje.setVisibility(View.GONE);
        btnFinalizarViaje.setVisibility(View.VISIBLE);
        //ELIMINA EL MARCADOR Y LA RUTA TRAZADA.
        map.clear();
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmorado)));
        if(currentLatLng != null){//EVITAMOS QUE EL CLEAR BORRE EL AUTO
            marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLatLng.latitude, currentLatLng.longitude))
                    .title("Ubicacion actual")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
        }
    }

    @Override
    public void toCalificationActivity() {
        if(fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        Intent intent = new Intent(this, CalificationClientActivity.class);
        intent.putExtra("idClient", idClientBooking);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

        if(locationListenerGPS != null)
            locationManager.removeUpdates(locationListenerGPS);
    }

}
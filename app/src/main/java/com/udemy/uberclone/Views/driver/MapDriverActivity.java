package com.udemy.uberclone.Views.driver;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverPresenter;
import com.udemy.uberclone.Interfaces.driver.map_driver.MapDriverView;
import com.udemy.uberclone.Presenters.driver.MapDriverPresenterImpl;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Utils.CarMoveAnim;
import com.udemy.uberclone.Utils.includes.MyToolbar;
import com.udemy.uberclone.Utils.preferences.SharedPreferencesUber;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.PermissionsProvider;
import com.udemy.uberclone.Views.MainActivity;
import com.udemy.uberclone.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback, MapDriverView {

    private boolean isConnect = false;
    private GoogleMap map;
    private Marker marker;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLatLng;
    private SupportMapFragment supportMapFragment;
    private Unbinder mUnbinder;
    private AuthProvider authProvider;
    private MapDriverPresenter mapDriverPresenter;
    private boolean mIsStartLocation = false;
    private LatLng mStartLatLng, mEndLatLng;
    private LocationManager locationManager;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.btnConectarse)
    MaterialButton btnConectarse;

    //ANIMACION DE ICONO UBER
    private LocationListener locationListenerGPS = location -> {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mStartLatLng != null)
            mEndLatLng = mStartLatLng;

        mStartLatLng = new LatLng(currentLatLng.latitude, currentLatLng.longitude);

        if (mEndLatLng != null)
            CarMoveAnim.carAnim(marker, mEndLatLng, mStartLatLng);

        updateLocation();
    };

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    if (!mIsStartLocation) {//SIGNIFICA QUE YA RECONOCIO LA UBICACION POR PRIMERA VEZ
                        mIsStartLocation = true;

                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        marker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("Ubicacion actual")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                        //mueve la camara al centro del icono
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(18f)
                                        .build()
                        ));

                        //DETENEMOS SERVICIO
                        updateLocation();
                        if (ActivityCompat.checkSelfPermission(MapDriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapDriverActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);//ESTO INICIALIZA EL ESCUCHADOR DE GPS EN TIEMPO REAL.
                        stopLocation();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);
        mUnbinder = ButterKnife.bind(this);
        MyToolbar.show(this, "Conductor", false, toolbar);

        mapDriverPresenter = new MapDriverPresenterImpl(this);

        authProvider = new AuthProvider();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDriver);
        supportMapFragment.getMapAsync(this);

        //INICIAMOS EL SHARED DEL VIAJE CONSULTANDO EL RIDE

        String status = SharedPreferencesUber.getInstance(MapDriverActivity.this).getStatusDriverBooking();
        String idClientBooking = SharedPreferencesUber.getInstance(MapDriverActivity.this).getIDClientBookingDriver();
        Log.d("TAGSHARED", status);
        if(status.equals("START") || status.equals("RIDE"))
            goToMapDriverBooking(idClientBooking);
        else{
            mapDriverPresenter.generateTokenNoti(authProvider);
            /*
            Eliminamos la referencia de conductores trabajando para evitar problemas,
            esto nos mantiene conectados y en workings
             */
            mapDriverPresenter.deleteDriverWorking(authProvider, getIntent().getBooleanExtra("CONNECT", false));
        }
    }

    private void goToMapDriverBooking(String idClientBooking){
        Intent intent = new Intent(MapDriverActivity.this, MapDriverBookingActivity.class);
        intent.putExtra("idClient", idClientBooking);
        startActivity(intent);
        finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout)
            logout();
        return super.onOptionsItemSelected(item);
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

        //METODO PAARA SABER SI EL DRIVER ESTA TRABAJANDO Y QUITARLO DE ACTIVE DRIVERS DESPUES DE QUE EL MAPA CARGO
        mapDriverPresenter.isDriverWorking(authProvider);
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

    @OnClick(R.id.btnConectarse)
    public void onClick(){
        if(isConnect)
            disconnect();
        else
            startLocation();
    }

    @Override
    public void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (PermissionsProvider.gpsActived(this)){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    btnConectarse.setText("Desconectarse");
                    isConnect = true;
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

    private void logout(){
        authProvider.logout();
        startActivity(new Intent(this, MainActivity.class));
        finish();
        disconnect();
    }

    @Override
    public void disconnect(){
        if(fusedLocationProviderClient != null){
            btnConectarse.setText("Conectarse");
            isConnect = false;
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            if(authProvider.existSession())
                mapDriverPresenter.removeLocation(authProvider);
        }else
            Toast.makeText(this, "No te puedes desconectar.", Toast.LENGTH_SHORT).show();
    }

    private void updateLocation(){
        if(authProvider.existSession() && currentLatLng != null)
            mapDriverPresenter.saveLocation(authProvider, currentLatLng);
    }

    private void stopLocation(){
        if(locationCallback != null && fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        stopLocation();

        mapDriverPresenter.removeEventListener(authProvider);
    }

}
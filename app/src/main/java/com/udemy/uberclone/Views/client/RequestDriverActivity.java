package com.udemy.uberclone.Views.client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.udemy.uberclone.Interfaces.client.request_driver.RequestDriverPresenter;
import com.udemy.uberclone.Interfaces.client.request_driver.RequestDriverView;
import com.udemy.uberclone.Presenters.client.RequestDriverPresenterImpl;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Models.ClientBooking;
import com.udemy.uberclone.Models.FCMBody;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RequestDriverActivity extends AppCompatActivity implements RequestDriverView {

    private double originLat, originLng, RADIUS = 0.1, destinationLat, destinationLng;
    private LatLng originLatLng, destinationLatLng, driverFoundLatLng;
    private boolean driverFound = false;
    private String driverFoundID = "", origin, destination;
    private static AuthProvider authProvider;
    private RequestDriverPresenter requestDriverPresenter;
    private Unbinder mUnbinder;

    @BindView(R.id.tvBuscando)
    TextView tvBuscando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);
        mUnbinder = ButterKnife.bind(this);

        authProvider = new AuthProvider();

        origin = getIntent().getStringExtra("origin");
        destination = getIntent().getStringExtra("destination");
        originLat = getIntent().getDoubleExtra("originLat", 0.0);
        originLng = getIntent().getDoubleExtra("originLng", 0.0);
        destinationLat = getIntent().getDoubleExtra("destinationLat", 0.0);
        destinationLng = getIntent().getDoubleExtra("destinationLng", 0.0);
        originLatLng = new LatLng(originLat, originLng);
        destinationLatLng = new LatLng(destinationLat, destinationLng);

        requestDriverPresenter = new RequestDriverPresenterImpl(this);
        requestDriverPresenter.getClosestDrivers(originLatLng, RADIUS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        //Quitar el escucha al final, ya que se queda escuchando y puede duplicarse si no lo finalizamos.
        requestDriverPresenter.removeListener(authProvider);
    }

    @Override
    public void driverFound(String key, GeoLocation location) {
        if(!driverFound){
            driverFound = true;
            driverFoundID = key;
            driverFoundLatLng = new LatLng(location.latitude, location.longitude);
            tvBuscando.setText("CONDUCTOR ENCONTRADO, ESPERANDO RESPUESTA... " + driverFoundID);
            requestDriverPresenter.createClientBooking(originLatLng, driverFoundLatLng, driverFoundID, RequestDriverActivity.this);
        }
    }

    @Override
    public void driverNotFound() {
        if(!driverFound){
            RADIUS = RADIUS + 0.1f;
            //NO ENCONTRÓ NINGÚN CONDUCTOR
            if(RADIUS > 5){
                Toast.makeText(getApplicationContext(), "No se econtró conductores.", Toast.LENGTH_LONG).show();
                return;
            }else
                requestDriverPresenter.getClosestDrivers(originLatLng, RADIUS);
        }
    }

    @Override
    public void buildNotification(String token, String time, String km) {
        Map<String, String> map = new HashMap<>();
        map.put("title", "SOLICITUD DE SERVICIO A " + time + " DE TU UBICACION");
        map.put("body", "Un Cliente esta solicitando un servicio a " + km + "\n" + "Recoger en: " + origin +
                "\n" + "Destino: " + destination);
        map.put("idClient", authProvider.getID());
        FCMBody fcmBody = new FCMBody(token, "high", map, "4500s");//4500s viene desde la doc de firebase.

        //Booking
        ClientBooking clientBooking = new ClientBooking(
                authProvider.getID(),
                driverFoundID,
                destination,
                origin,
                time,
                km,
                "CREATE",
                originLat,
                originLng,
                destinationLat,
                destinationLng);

        requestDriverPresenter.sendNotification(fcmBody, clientBooking, authProvider);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(RequestDriverActivity.this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void success(String mensaje) {
        Toast.makeText(RequestDriverActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toMapClientBooking() {
        Intent intent = new Intent(new Intent(RequestDriverActivity.this, MapClientBookingActivity.class));
        //Borramos el historial de actividades anterior
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void toMapClient() {
        startActivity(new Intent(RequestDriverActivity.this, MapClientActivity.class));
        finish();
    }

}
package com.udemy.routeservices.Interactors.driver;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingInteractor;
import com.udemy.routeservices.Interfaces.driver.map_driver_booking.MapDriverBookingPresenter;
import com.udemy.routeservices.Models.FCMBody;
import com.udemy.routeservices.Models.FCMResponse;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.ClientBookingProvider;
import com.udemy.routeservices.Utils.providers.ClientProvider;
import com.udemy.routeservices.Utils.providers.GeofireProvider;
import com.udemy.routeservices.Utils.providers.GoogleAPIProvider;
import com.udemy.routeservices.Utils.providers.TokenNotiProvider;
import com.udemy.routeservices.WebServices.APIServices;
import com.udemy.routeservices.WebServices.NotificationServiceAPI;
import com.udemy.routeservices.WebServices.WebServiceAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapDriverBookingInteractorImpl implements MapDriverBookingInteractor {

    private MapDriverBookingPresenter mapDriverBookingPresenter;
    private ClientProvider clientProvider;
    private ClientBookingProvider clientBookingProvider;
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions;
    private LatLng originLatLng, destinationLatLng;
    private GeofireProvider geofireProvider;
    private TokenNotiProvider tokenNotiProvider;
    private boolean isCloseToClient = false;

    public MapDriverBookingInteractorImpl(MapDriverBookingPresenter mapDriverBookingPresenter){
        this.mapDriverBookingPresenter = mapDriverBookingPresenter;
        clientProvider = new ClientProvider();
        clientBookingProvider = new ClientBookingProvider();
        geofireProvider = new GeofireProvider("drivers_working");
        tokenNotiProvider = new TokenNotiProvider();
    }

    @Override
    public void getClient(String idClientBooking) {
        //Obtener la info 1 sola vez con este metodo.
        clientProvider.getCLient(idClientBooking).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String email = snapshot.child("email").getValue().toString();
                    String nombre = snapshot.child("name").getValue().toString();
                    //tvCorreoCliente.setText(email);
                    mapDriverBookingPresenter.setClientInfo(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getClientBooking(String idClientBooking, LatLng currentLatLng, Context context) {
        clientBookingProvider.getClientBooking(idClientBooking).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String destination = snapshot.child("destination").getValue().toString();
                    String origin = snapshot.child("origin").getValue().toString();
                    //ORIGEN DEL CLIENTE
                    double originLat = Double.parseDouble(snapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(snapshot.child("originLng").getValue().toString());

                    double destinationLat = Double.parseDouble(snapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(snapshot.child("destinationLng").getValue().toString());
                    originLatLng = new LatLng(originLat, originLng);
                    destinationLatLng = new LatLng(destinationLat, destinationLng);

                    //OBTENER EL ULTIMO ESTADO ALMACENADO EN SHAREDPREF
                    if(SharedPreferencesUber.getInstance(context).getStatusDriverBooking() != null){
                        if(SharedPreferencesUber.getInstance(context).getStatusDriverBooking().equals("START"))
                            iniciarViaje(currentLatLng, idClientBooking, context);
                        else{
                            //ESTE VALOR SE ALMACENA CUANDO EL CONDUCTOR INICIA EL VIAJE POR PRIMERA VEZ
                            SharedPreferencesUber.getInstance(context).guardarStatusDriverBooking("RIDE", idClientBooking);
                            //RUTA ENTRE UBICACION ACTUAL DEL CONDUCTOR Y LA UBICACION DEL CLIENTE.
                            drawRoute(currentLatLng, new LatLng(originLat, originLng), context);
                            mapDriverBookingPresenter.setRouteInfo(origin, destination, originLat, originLng);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void updateLocation(AuthProvider authProvider, LatLng currentLatLng) {
        if(authProvider.existSession() && currentLatLng != null){
            geofireProvider.saveLocation(authProvider.getID(), currentLatLng);
            Log.d("TAG1", "CERCA");
            if(!isCloseToClient && originLatLng != null){
                double distance = getDistanceBetween(originLatLng, currentLatLng);//RETORNA EN METROS
                if(distance <= 200){
                    isCloseToClient = true;
                    mapDriverBookingPresenter.showMsg("Estas cerca del cliente.");
                }
            }
        }
    }

    @Override
    public void iniciarViaje(LatLng currentLatLng, String idClientBooking, Context context) {
        if(isCloseToClient){
            SharedPreferencesUber.getInstance(context).guardarStatusDriverBooking("START", idClientBooking);
            clientBookingProvider.updateStatus(idClientBooking, "START");
            mapDriverBookingPresenter.setInfoInicioViaje(destinationLatLng);
            drawRoute(currentLatLng, destinationLatLng, context);
            sendNotification("Viaje Iniciado", idClientBooking);
        }else
            mapDriverBookingPresenter.showMsg("Necesitas estar mas cerca");
    }

    @Override
    public void finalizarViaje(String idClientBooking, AuthProvider authProvider, Context context) {
        clientBookingProvider.updateStatus(idClientBooking, "FINISH");
        clientBookingProvider.updateIDHistoryBooking(idClientBooking);
        SharedPreferencesUber.getInstance(context).guardarStatusDriverBooking("", "");
        sendNotification("Viaje Finalizado", idClientBooking);
        geofireProvider.removeLocation(authProvider.getID());
        mapDriverBookingPresenter.toCalificationActivity();
    }

    public void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context){
        String origin = originLatLng.latitude + "," + originLatLng.longitude;
        String destination = destinationLatLng.latitude + "," + destinationLatLng.longitude;
        String key = context.getResources().getString(R.string.google_maps_key);
        String url = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=" + origin + "&destination=" + destination + "&key=" + key;
        Call<String> call = WebServiceAPI.getInstance().getAPI(APIServices.class).getDirections(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polyLines = route.getJSONObject("overview_polyline");
                    String points = polyLines.getString("points");
                    Log.d("POLY", points);
                    polyLineList = GoogleAPIProvider.decodePoly(points);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(polyLineList);
                    polylineOptions.color(Color.DKGRAY);
                    polylineOptions.width(10);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    mapDriverBookingPresenter.setPolylineRoute(polylineOptions);
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void sendNotification(String status, String idClientBooking){
        /*
         * El metodo addListenerForSingl...... sirve para obtener el valor de la DB pero solo 1 vez
         * AL PARECER EN TIEMPO REAL.
         *
         * */
        tokenNotiProvider.getToken(idClientBooking).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String token = snapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "ESTADO DE TU VIAJE");
                    map.put("body", "Tu estado del viaje es: " + status);
                    FCMBody fcmBody = new FCMBody(token, "high", map, "4500s");
                    sendNotificationStatus(fcmBody);
                }else
                    mapDriverBookingPresenter.showMsg("No se pudo enviar la notificacion, error en la sesion.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendNotificationStatus(FCMBody body){
        Call<FCMResponse> call = NotificationServiceAPI.getInstance().getAPI(APIServices.class).sendFCM(body);
        call.enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body() != null){
                    if(response.body().getSuccess() != 1)
                        mapDriverBookingPresenter.showMsg("Error al enviar a conductor.");
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("TAG1Error", t.getMessage());
            }
        });
    }

    //METODOD PARA SABER LA DISTANCIA ENTRE EL CONDUCTOR Y EL CLIENTE
    private double getDistanceBetween(LatLng clientLatLng, LatLng driverLatLng){
        double distance = 0.0;
        Location clientLocation = new Location("");
        Location driverLocation = new Location("");
        clientLocation.setLatitude(clientLatLng.latitude);
        clientLocation.setLongitude(clientLatLng.longitude);
        driverLocation.setLatitude(driverLatLng.latitude);
        driverLocation.setLongitude(driverLatLng.longitude);

        distance = clientLocation.distanceTo(driverLocation);

        return distance;
    }

}

package com.udemy.uberclone.Utils.providers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.udemy.uberclone.R;
import com.udemy.uberclone.WebServices.APIServices;
import com.udemy.uberclone.WebServices.WebServiceAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleAPIProvider {

    private static List<LatLng> polyLineList;
    private static PolylineOptions polylineOptions;

    public GoogleAPIProvider(){

    }

    public static List decodePoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public static void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context, GoogleMap map, TextView tvDistancia, TextView tvTiempo){
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

                    if(tvDistancia != null && tvTiempo != null){
                        tvDistancia.setText(distanceText);
                        tvTiempo.setText(durationText);
                    }

                    map.addPolyline(polylineOptions);
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}

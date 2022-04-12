package com.udemy.routeservices.Utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUber {

    private static SharedPreferencesUber instance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private final String SHARED_PREFERENCES = "SHARED_UBER";
    private final String SHARED_BOOKING_DRIVER = "STATUS_DRIVER";
    private final String SHARED_BOOKING_CLIENT = "STATUS_CLIENT";
    private final String SHARED_BOOKING_DRIVER_IDCLIENT = "IDCLIENT_BOOKING";
    private final String SHARED_BOOKING_CLIENT_IDDRIVER = "IDDRIVER_BOOKING";
    private final String SHARED_TYPE_USER = "TYPE_USER";
    private final String SHARED_SAVE_CONNECTION_DRIVER = "CONNECTION_DRIVER";

    private SharedPreferencesUber(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesUber getInstance(Context context){
        if(instance == null)
            instance = new SharedPreferencesUber(context);

        return instance;
    }

    public void guardarTipoUsuario(String type){
        editor.putString(SHARED_TYPE_USER, type);
        editor.apply();
    }

    public void guardarStatusDriverBooking(String status, String idClientBooking){
        editor.putString(SHARED_BOOKING_DRIVER, status);
        editor.putString(SHARED_BOOKING_DRIVER_IDCLIENT, idClientBooking);
        editor.apply();
    }

    public void guardarStatusClientBooking(String status, String idDriver){
        editor.putString(SHARED_BOOKING_CLIENT, status);
        editor.putString(SHARED_BOOKING_CLIENT_IDDRIVER, idDriver);
        editor.apply();
    }

    public String getTipoUsuario(){
        return sharedPreferences.getString(SHARED_TYPE_USER, null);
    }

    public String getStatusClientBooking(){
        return sharedPreferences.getString(SHARED_BOOKING_CLIENT, "");
    }

    public String getStatusDriverBooking(){
        return sharedPreferences.getString(SHARED_BOOKING_DRIVER, "");
    }

    public String getIDClientBookingDriver(){
        return sharedPreferences.getString(SHARED_BOOKING_DRIVER_IDCLIENT, "");
    }

    public String getIDDriverBookingClient(){
        return sharedPreferences.getString(SHARED_BOOKING_CLIENT_IDDRIVER, "");
    }

    public void saveConnectionDriver(boolean connection){
        editor.putBoolean(SHARED_SAVE_CONNECTION_DRIVER, connection);
        editor.apply();
    }

    public boolean getConnectionStatusDriver(){
        return sharedPreferences.getBoolean(SHARED_SAVE_CONNECTION_DRIVER, false);
    }

}

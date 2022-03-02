package com.udemy.uberclone.Utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUber {

    private static SharedPreferencesUber instance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private final String SHARED_PREFERENCES = "SHARED_UBER";
    private final String SHARED_TYPE_USER = "TYPE_USER";

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

    public String getTipoUsuario(){
        return sharedPreferences.getString(SHARED_TYPE_USER, null);
    }

}

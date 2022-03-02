package com.udemy.uberclone.Utils.providers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.udemy.uberclone.Utils.Constants;

public class PermissionsProvider {

    public static void showAlertDialogGPS(Context context, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Activa tu GPS para continuar.")
                .setPositiveButton("Configuraciones", (DialogInterface, i) -> {
                    activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.SETTINGS_REQUEST_CODE);
                })
                .create()
                .show();
    }

    public static void checkLocationPermissions(Activity activity){
        if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(activity.getApplicationContext())
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere los permisos.")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
                        })
                        .create()
                        .show();
            }else
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        }
    }

    public static boolean gpsActived(Context context) {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            isActive = true;

        return isActive;
    }

}

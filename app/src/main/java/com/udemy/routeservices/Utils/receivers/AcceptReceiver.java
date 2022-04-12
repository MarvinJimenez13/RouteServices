package com.udemy.routeservices.Utils.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.ClientBookingProvider;
import com.udemy.routeservices.Utils.providers.GeofireProvider;
import com.udemy.routeservices.Views.driver.MapDriverBookingActivity;

/*
*
* CLASE PARA MANEJAR EL EVENTO DE LAS OPCIONES DE ACEPTAR DENTRO DE LA NOTIFICACION.
*
* */
public class AcceptReceiver extends BroadcastReceiver {

    private ClientBookingProvider clientBookingProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;

    /* RECIBIMOS DEL BOTON ACEPTAR */
    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        * Hacemos instancia a active_drivers para eliminar al conductor de conductores activos y
        * pasarlo a conductores TRABAJANDO.
        * */
        authProvider = new AuthProvider();
        geofireProvider = new GeofireProvider("active_drivers");
        geofireProvider.removeLocation(authProvider.getID());

        String idClient = intent.getExtras().getString("idClient");
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(idClient, "ACCEPT");//CAMBIAMOS DE ESTADO EN EL NODO DE FIREBASE.

        //CERRAMOS LA NOTIFICACION AL DAR CLICK EN ACEPTAR, ES EL ID 2.
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        //INTENT PARA IR A LA ACTIVIDAD DEL VIAJE CONDUCTOR AL ACEPTAR LA NOTIFICACION
        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idClient", idClient);
        context.startActivity(intent1);
    }

}

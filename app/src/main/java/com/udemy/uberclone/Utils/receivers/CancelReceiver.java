package com.udemy.uberclone.Utils.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.udemy.uberclone.Utils.providers.ClientBookingProvider;

/*
 *
 * CLASE PARA MANEJAR EL EVENTO DE LAS OPCIONES DE CANCELAR DENTRO DE LA NOTIFICACION.
 *
 * */
public class CancelReceiver extends BroadcastReceiver {

    private ClientBookingProvider clientBookingProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        String idClient = intent.getExtras().getString("idClient");
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(idClient, "CANCEL");//CAMBIAMOS DE ESTADO EN EL NODO DE FIREBASE.

        //CERRAMOS LA NOTIFICACION AL DAR CLICK EN ACEPTAR, ES EL ID 2.
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }

}

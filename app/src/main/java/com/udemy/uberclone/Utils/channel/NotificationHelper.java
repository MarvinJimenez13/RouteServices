package com.udemy.uberclone.Utils.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.udemy.uberclone.R;


/*
*
* CLASE ENCARGADA DE LA NOTIFICACIONES PUSH, CON ACTIONS Y SIN ACTIONS (BOTONES DE ACCION)
*
*
* */
public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.udemy.cloneuber";
    private static final String CHANNEL_NAME = "CloneUber";
    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent intent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car)
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));//PARA VER EL TEXTO COMPLETO EN LAS NOTIFICACIONES
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationActions(String title, String body, Uri soundUri, Notification.Action acceptAction, Notification.Action cancelAction){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_car)
                .addAction(acceptAction)//AGREGAMOS BOTONES DE ACCION EN LA NOTIFICACION
                .addAction(cancelAction)//BOTON DE CANCELAR
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));//PARA VER EL TEXTO COMPLETO EN LAS NOTIFICACIONES
    }

    public NotificationCompat.Builder getNotificationOldAPIActions(String title, String body, Uri soundUri, NotificationCompat.Action acceptAction, NotificationCompat.Action cancelAction){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_car)
                .addAction(acceptAction)//AGREGAMOS BOTONES DE ACCION EN LA NOTIFICACION
                .addAction(cancelAction)//BOTON DE CANCELAR
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));//PARA VER EL TEXTO COMPLETO EN LAS NOTIFICACIONES EN VERSIONES ANTERIORES A OREO.
    }

    public NotificationCompat.Builder getNotificationOldAPI(String title, String body, PendingIntent intent, Uri soundUri){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));//PARA VER EL TEXTO COMPLETO EN LAS NOTIFICACIONES EN VERSIONES ANTERIORES A OREO.
    }

    public NotificationManager getManager(){
        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return notificationManager;
    }
}

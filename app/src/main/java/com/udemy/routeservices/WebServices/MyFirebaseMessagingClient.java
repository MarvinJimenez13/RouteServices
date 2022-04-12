package com.udemy.routeservices.WebServices;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.channel.NotificationHelper;
import com.udemy.routeservices.Utils.receivers.AcceptReceiver;
import com.udemy.routeservices.Utils.receivers.CancelReceiver;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private PendingIntent pendingIntent;
    private NotificationHelper notificationHelper;
    private static final int NOTIFICATION_CODE = 100;//CUALQUIER VALOR

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if(title != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if(title.contains("SOLICITUD DE SERVICIO")){
                    String idClient = data.get("idClient");
                    showNotificationAPIOreoActions(title, body, idClient);
                }else
                    showNotificationAPIOreo(title, body);
            }else{
                if(title.contains("SOLICITUD DE SERVICIO")){
                    String idClient = data.get("idClient");
                    showNotificationActions(title, body, idClient);
                }else
                    showNotification(title, body);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationAPIOreo(String title, String body){
        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotification(title, body, pendingIntent, sound);
        notificationHelper.getManager().notify(1,builder.build());
    }

    private void showNotification(String title, String body){
        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPI(title, body, pendingIntent, sound);
        notificationHelper.getManager().notify(1,builder.build());
    }

    //PARA MANEJAR LAS OPCIONES DENTRO DE LA NOTIFICACION
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationAPIOreoActions(String title, String body, String idClient){
        //CONFIGURAMOS CON LA CLASE ACCEPTRECEIVER PARA EL BOTON DE ACEPTAR
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        //CONFIGURAMOS CON LA CLASE CANCELRECEIVER PARA EL BOTON DE CANCELAR
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action cancelAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();

        notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotificationActions(title, body, sound, acceptAction, cancelAction);
        notificationHelper.getManager().notify(2,builder.build());//EL NUMERO 2 O 1 ES PARA DIFERENCIAR LAS NOTIFICACIONES Y QUE NO SE REEMPLACEN
    }

    private void showNotificationActions(String title, String body, String idClient){
        //CONFIGURAMOS CON LA CLASE ACCEPTRECEIVER PARA EL BOTON DE ACEPTAR
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        //CONFIGURAMOS CON LA CLASE CANCELRECEIVER PARA EL BOTON DE CANCELAR
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();

        notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPIActions(title, body, sound, acceptAction, cancelAction);
        notificationHelper.getManager().notify(2,builder.build());
    }

}

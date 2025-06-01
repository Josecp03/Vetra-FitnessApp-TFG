package com.example.vetra_fitnessapp_tfg;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;

/**
 * Servicio de Firebase Cloud Messaging para manejar notificaciones push.
 * Gestiona la recepción de mensajes remotos y la generación de notificaciones locales
 * basándose en las preferencias del usuario.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Método llamado cuando se recibe un mensaje remoto de Firebase.
     * Filtra las notificaciones según las preferencias del usuario y
     * muestra notificaciones locales cuando corresponde.
     *
     * @param message Mensaje remoto recibido de Firebase
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (!Prefs.isNotificationsEnabled(this)) {
            return;
        }
        RemoteMessage.Notification notif = message.getNotification();
        if (notif == null) return;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "mi_canal_por_defecto")
                        .setSmallIcon(R.drawable.logo_vetra_white)
                        .setContentTitle(notif.getTitle())
                        .setContentText(notif.getBody())
                        .setAutoCancel(true);
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(),
                        builder.build());
    }

}

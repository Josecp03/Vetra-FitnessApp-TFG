package com.example.vetra_fitnessapp_tfg;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;        // ← nuevo
import androidx.core.app.NotificationManagerCompat; // ← nuevo

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // Si viene payload de "notification", lo mostramos:
        RemoteMessage.Notification notif = message.getNotification();
        if (notif != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mi_canal_por_defecto")
                    .setSmallIcon(R.drawable.logo_vetra_white)   // pon aquí tu icono de notificación
                    .setContentTitle(notif.getTitle())
                    .setContentText(notif.getBody())
                    .setAutoCancel(true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            NotificationManagerCompat.from(this)
                    .notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Aquí puedes enviar el token a tu servidor si lo necesitas
    }
}

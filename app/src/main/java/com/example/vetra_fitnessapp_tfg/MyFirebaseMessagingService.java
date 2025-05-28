package com.example.vetra_fitnessapp_tfg;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // 1) Filtrar según Prefs
        if (!Prefs.isNotificationsEnabled(this)) {
            return;
        }

        // 2) Mostrar sólo si viene con payload Notification
        RemoteMessage.Notification notif = message.getNotification();
        if (notif == null) return;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "mi_canal_por_defecto")
                        .setSmallIcon(R.drawable.logo_vetra_white)
                        .setContentTitle(notif.getTitle())
                        .setContentText(notif.getBody())
                        .setAutoCancel(true);

        // 3) Permiso POST_NOTIFICATIONS (Android 13+)
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

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Aquí podrías enviar el token a tu servidor si lo necesitas
    }
}

package com.example.vetra_fitnessapp_tfg.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class Permission {

    private String[] permissions;
    private int requestCode;
    private Activity activity;

    // Constructor con parámetros
    public Permission(Activity activity, String[] permission, int requestCode) {
        this.activity = activity;
        this.permissions = permission;
        this.requestCode = requestCode;
    }

    // Solicitar permiso
    public void requestPermission() {

        // Lista de permisos no concedidos
        List<String> permisosNoConcedidos = new ArrayList<>();

        // Iterar sobre cada uno de los permisos
        for (String permission : permissions) {

            // Comprobar que no se hayan pedido ya los permisos
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

                // Añadir el permiso a la lista
                permisosNoConcedidos.add(permission);

            }
        }

        // Comprobar si hay permisos pendientes
        if (!permisosNoConcedidos.isEmpty()) {

            // Pedir los permisos
            ActivityCompat.requestPermissions(activity, permisosNoConcedidos.toArray(new String[0]), requestCode);

        }

    }

    // Manejar los resultados de la solicitud de permisos
    public static void handlePermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {

        // Variable booleana para controlar que se otorgen todos los permisos
        boolean allGranted = true;

        // Iterar sobre cada uno de los permisos
        for (int i = 0; i < grantResults.length; i++) {

            // Comprobar si el pemriso ha sido concedido
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                // Actualizar variable booleana
                allGranted = false;

                // Comprobar si el usuario ha negado lo permisos anteriormente
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    openAppConfiguration(activity);
                }

            }

        }

    }

    // Abrir la configuración de la aplicación si el usuario niega el permiso
    private static void openAppConfiguration(Activity activity) {

        // Mostrar mensaje de ayuda al usuario indicando que debe activar los permisos manualmente
        Toast.makeText(activity, "Activa el permiso desde la configuración", Toast.LENGTH_SHORT).show();

        // Crear un Intent para abrir la configuración de la aplicación en los ajustes del sistema
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        // Obtener la URI del paquete de la aplicación actual, para indicar qué app abrir en la configuración
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);

        // Establecer la URI como dato del Intent, para que abra los ajustes específicos de esta aplicación
        intent.setData(uri);

        // Iniciar la actividad con el Intent, lo que llevará al usuario a la configuración de la app
        activity.startActivity(intent);

    }
}

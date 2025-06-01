package com.example.vetra_fitnessapp_tfg.utils;

import android.content.Context;

/**
 * Utilidad para gestionar preferencias compartidas de la aplicación.
 * Proporciona métodos estáticos para guardar y recuperar configuraciones
 * del usuario como estado de rutinas y preferencias de notificaciones.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class Prefs {

    /** Nombre del archivo de preferencias compartidas */
    private static final String TFG = "TFG_PREFS";

    /** Clave para el estado de rutina en progreso */
    private static final String KEY_IN_PROGRESS       = "routine_in_progress";

    /** Clave para el ID de la rutina actual */
    private static final String KEY_CURRENT_ROUTINE_ID = "current_routine_id";

    /** Clave para el estado de notificaciones habilitadas */
    private static final String KEY_NOTIFICATIONS_ENABLED   = "notifications_enabled";

    /**
     * Establece si hay una rutina en progreso.
     *
     * @param ctx Contexto de la aplicación
     * @param inProgress true si hay una rutina en progreso, false en caso contrario
     */
    public static void setRoutineInProgress(Context ctx, boolean inProgress) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_IN_PROGRESS, inProgress)
                .apply();
    }

    /**
     * Verifica si hay una rutina en progreso.
     *
     * @param ctx Contexto de la aplicación
     * @return true si hay una rutina en progreso, false en caso contrario
     */
    public static boolean isRoutineInProgress(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getBoolean(KEY_IN_PROGRESS, false);
    }

    /**
     * Establece el ID de la rutina actualmente en progreso.
     *
     * @param ctx Contexto de la aplicación
     * @param id ID de la rutina actual
     */
    public static void setCurrentRoutineId(Context ctx, String id) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CURRENT_ROUTINE_ID, id)
                .apply();
    }

    /**
     * Obtiene el ID de la rutina actualmente en progreso.
     *
     * @param ctx Contexto de la aplicación
     * @return ID de la rutina actual, o null si no hay ninguna
     */
    public static String getCurrentRoutineId(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_ROUTINE_ID, null);
    }

    /**
     * Establece si las notificaciones están habilitadas.
     *
     * @param ctx Contexto de la aplicación
     * @param enabled true para habilitar notificaciones, false para deshabilitarlas
     */
    public static void setNotificationsEnabled(Context ctx, boolean enabled) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
                .apply();
    }

    /**
     * Verifica si las notificaciones están habilitadas.
     *
     * @param ctx Contexto de la aplicación
     * @return true si las notificaciones están habilitadas, false en caso contrario (por defecto true)
     */
    public static boolean isNotificationsEnabled(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }
}

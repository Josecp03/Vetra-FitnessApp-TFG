package com.example.vetra_fitnessapp_tfg.utils;

import android.content.Context;

public class Prefs {
    private static final String TFG = "TFG_PREFS";
    private static final String KEY_IN_PROGRESS       = "routine_in_progress";
    private static final String KEY_CURRENT_ROUTINE_ID = "current_routine_id";
    private static final String KEY_NOTIFICATIONS_ENABLED   = "notifications_enabled";  // ← Añadido


    public static void setRoutineInProgress(Context ctx, boolean inProgress) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_IN_PROGRESS, inProgress)
                .apply();
    }
    public static boolean isRoutineInProgress(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getBoolean(KEY_IN_PROGRESS, false);
    }

    public static void setCurrentRoutineId(Context ctx, String id) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CURRENT_ROUTINE_ID, id)
                .apply();
    }
    public static String getCurrentRoutineId(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_ROUTINE_ID, null);
    }

    public static void setNotificationsEnabled(Context ctx, boolean enabled) {
        ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
                .apply();
    }

    public static boolean isNotificationsEnabled(Context ctx) {
        return ctx.getSharedPreferences(TFG, Context.MODE_PRIVATE)
                .getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

}

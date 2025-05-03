package com.example.vetra_fitnessapp_tfg.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

public class Prefs {
    private static final String TFG = "TFG_PREFS";
    private static final String KEY_IN_PROGRESS = "routine_in_progress";

    public static void setRoutineInProgress(Context ctx, boolean inProgress) {
        ctx.getSharedPreferences(TFG, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_IN_PROGRESS, inProgress)
                .apply();
    }

    public static boolean isRoutineInProgress(Context ctx) {
        return ctx.getSharedPreferences(TFG, MODE_PRIVATE)
                .getBoolean(KEY_IN_PROGRESS, false);
    }
}

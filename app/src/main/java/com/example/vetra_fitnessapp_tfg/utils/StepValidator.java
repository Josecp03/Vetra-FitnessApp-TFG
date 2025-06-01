package com.example.vetra_fitnessapp_tfg.utils;

/**
 * Interfaz para validar campos en fragmentos de configuración paso a paso.
 * Implementada por fragmentos que requieren validación de datos antes
 * de proceder al siguiente paso en el proceso de configuración del usuario.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public interface StepValidator {

    /**
     * Valida los campos del fragmento actual.
     * Debe verificar que todos los datos requeridos estén presentes
     * y sean válidos antes de permitir continuar al siguiente paso.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    boolean validateFields();
}

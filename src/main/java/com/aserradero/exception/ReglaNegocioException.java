package com.aserradero.exception;

/** Se lanza cuando se viola una regla de negocio (ej. vender mas de lo disponible). Devuelve HTTP 400. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

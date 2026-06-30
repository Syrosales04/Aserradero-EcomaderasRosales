package com.aserradero.exception;

/** Se lanza cuando no se encuentra un registro por su id. Devuelve HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

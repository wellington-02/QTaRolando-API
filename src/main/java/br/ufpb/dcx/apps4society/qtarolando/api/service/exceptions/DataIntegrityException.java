package br.ufpb.dcx.apps4society.qtarolando.api.service.exceptions;

public class DataIntegrityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataIntegrityException(String msg) {
        super(msg);
    }

    public DataIntegrityException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
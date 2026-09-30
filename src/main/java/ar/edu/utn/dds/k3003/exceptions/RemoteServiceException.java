package ar.edu.utn.dds.k3003.exceptions;

/**
 * Excepción runtime que envuelve problemas al llamar servicios remotos.
 */
public class RemoteServiceException extends RuntimeException {

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
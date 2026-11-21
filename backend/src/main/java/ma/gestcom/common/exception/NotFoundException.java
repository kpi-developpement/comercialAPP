package ma.gestcom.common.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super(resource + " introuvable (id=" + id + ")");
    }

    public NotFoundException(String message) {
        super(message);
    }
}

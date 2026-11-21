package ma.gestcom.common.exception;

import lombok.Getter;

/**
 * Violation of a business rule. The {@code code} is stable and can be interpreted by the frontend.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}

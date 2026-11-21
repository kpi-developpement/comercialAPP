package ma.gestcom.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error body returned by {@code GlobalExceptionHandler}.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldErrorDetail> errors) {

    public record FieldErrorDetail(String field, String message) {
    }
}

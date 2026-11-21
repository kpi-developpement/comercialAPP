package ma.gestcom.common.exception;

/**
 * Catalogue of business error codes returned in {@code ApiError.code}.
 */
public final class ErrorCodes {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String DUPLICATE = "DUPLICATE";
    public static final String CONCURRENT_MODIFICATION = "CONCURRENT_MODIFICATION";
    public static final String DATA_INTEGRITY = "DATA_INTEGRITY";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    public static final String DOCUMENT_NOT_EDITABLE = "DOCUMENT_NOT_EDITABLE";
    public static final String DOCUMENT_ALREADY_VALIDATED = "DOCUMENT_ALREADY_VALIDATED";
    public static final String DOCUMENT_EMPTY = "DOCUMENT_EMPTY";
    public static final String DOCUMENT_INVALID_STATE = "DOCUMENT_INVALID_STATE";
    public static final String TRANSFORMATION_NOT_ALLOWED = "TRANSFORMATION_NOT_ALLOWED";
    public static final String QUANTITY_EXCEEDS_REMAINING = "QUANTITY_EXCEEDS_REMAINING";
    public static final String GROUPING_INVALID = "GROUPING_INVALID";
    public static final String STOCK_INSUFFICIENT = "STOCK_INSUFFICIENT";
    public static final String ARTICLE_INACTIVE = "ARTICLE_INACTIVE";
    public static final String CLIENT_BLOQUE = "CLIENT_BLOQUE";
    public static final String CLIENT_PROSPECT = "CLIENT_PROSPECT";
    public static final String PAYMENT_ALLOCATION_INVALID = "PAYMENT_ALLOCATION_INVALID";
    public static final String PAYMENT_STATUS_INVALID = "PAYMENT_STATUS_INVALID";
    public static final String OPERATION_NOT_EDITABLE = "OPERATION_NOT_EDITABLE";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String SELF_MODIFICATION = "SELF_MODIFICATION";

    private ErrorCodes() {
    }
}

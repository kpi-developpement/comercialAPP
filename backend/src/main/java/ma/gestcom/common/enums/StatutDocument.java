package ma.gestcom.common.enums;

/**
 * Lifecycle of a commercial document: BROUILLON -> VALIDE -> (TRANSFORME_PARTIEL | TRANSFORME) | ANNULE.
 */
public enum StatutDocument {
    BROUILLON,
    VALIDE,
    TRANSFORME_PARTIEL,
    TRANSFORME,
    ANNULE;

    public boolean isEditable() {
        return this == BROUILLON;
    }

    /** A validated document whose lines may still be transformed into a next document. */
    public boolean isTransformable() {
        return this == VALIDE || this == TRANSFORME_PARTIEL;
    }
}

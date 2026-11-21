package ma.gestcom.modules.numerotation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.TypeDocument;

/**
 * Yearly counter of a document type. Always read with a pessimistic lock before being incremented.
 */
@Getter
@Setter
@Entity
@Table(name = "sequence_document")
public class SequenceDocument extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false, length = 30)
    private TypeDocument typeDocument;

    @Column(nullable = false)
    private Integer annee;

    @Column(nullable = false)
    private Long compteur;
}

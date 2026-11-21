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
 * Prefix and counter width of a document type, e.g. FA + 5 -> FA-2026-00001.
 */
@Getter
@Setter
@Entity
@Table(name = "numerotation_config")
public class NumerotationConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false, unique = true, length = 30)
    private TypeDocument typeDocument;

    @Column(nullable = false, length = 10)
    private String prefixe;

    @Column(nullable = false)
    private Integer longueur;
}

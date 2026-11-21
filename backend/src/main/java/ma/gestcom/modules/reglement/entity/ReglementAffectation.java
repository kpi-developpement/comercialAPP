package ma.gestcom.modules.reglement.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

/**
 * Part of a payment allocated to one invoice (client or supplier invoice depending on the payment's sens).
 */
@Getter
@Setter
@Entity
@Table(name = "reglement_affectation")
public class ReglementAffectation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reglement_id")
    private Reglement reglement;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_numero", length = 30)
    private String documentNumero;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;
}

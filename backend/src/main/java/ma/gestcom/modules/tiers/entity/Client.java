package ma.gestcom.modules.tiers.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.enums.RisqueClient;
import ma.gestcom.common.enums.TypeClient;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client extends AbstractTiers {

    /** Maximum outstanding amount ("découvert"); 0 means no limit. Exceeding it only produces a warning. */
    @Column(name = "plafond_credit", nullable = false, precision = 15, scale = 2)
    private BigDecimal plafondCredit = BigDecimal.ZERO;

    /** A prospect can only receive quotes (devis). */
    @Column(nullable = false)
    private boolean prospect;

    /** Blocked account: no new sales document can be created or validated. */
    @Column(nullable = false)
    private boolean bloque;

    @Column(nullable = false)
    private boolean arbitrage;

    @Column(name = "groupe_societes", nullable = false)
    private boolean groupeSocietes;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_client", nullable = false, length = 20)
    private TypeClient typeClient = TypeClient.A_TERME;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RisqueClient risque = RisqueClient.NORMAL;

    @Column(length = 80)
    private String representant;

    /** Default delivery depot of the client. */
    @Column(name = "depot_id")
    private Long depotId;

    @Column(length = 20)
    private String cin;

    @Column(length = 30)
    private String journal;

    @Column(name = "compte_comptable", length = 30)
    private String compteComptable;

    @Column(name = "email_relance", length = 120)
    private String emailRelance;

    @Column(name = "date_debut_prospect")
    private LocalDate dateDebutProspect;

    @Column(name = "date_fin_societe")
    private LocalDate dateFinSociete;
}

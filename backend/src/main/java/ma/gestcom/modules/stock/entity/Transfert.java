package ma.gestcom.modules.stock.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.StatutOperation;

/**
 * Move of goods between two depots. Does not change the PUMP.
 */
@Getter
@Setter
@Entity
@Table(name = "transfert")
public class Transfert extends BaseEntity {

    @Column(unique = true, length = 30)
    private String numero;

    @Column(name = "depot_source_id", nullable = false)
    private Long depotSourceId;

    @Column(name = "depot_destination_id", nullable = false)
    private Long depotDestinationId;

    @Column(name = "date_transfert", nullable = false)
    private LocalDate dateTransfert;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutOperation statut = StatutOperation.BROUILLON;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "transfert", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<TransfertLigne> lignes = new ArrayList<>();

    public void addLigne(TransfertLigne ligne) {
        ligne.setTransfert(this);
        lignes.add(ligne);
    }
}

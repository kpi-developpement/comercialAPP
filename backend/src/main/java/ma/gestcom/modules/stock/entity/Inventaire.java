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
 * Physical count session of one depot. Validation turns gaps into AJUSTEMENT_INVENTAIRE movements.
 */
@Getter
@Setter
@Entity
@Table(name = "inventaire")
public class Inventaire extends BaseEntity {

    @Column(unique = true, length = 30)
    private String numero;

    @Column(name = "depot_id", nullable = false)
    private Long depotId;

    @Column(name = "date_inventaire", nullable = false)
    private LocalDate dateInventaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutOperation statut = StatutOperation.BROUILLON;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("articleCode ASC")
    private List<InventaireLigne> lignes = new ArrayList<>();

    public void addLigne(InventaireLigne ligne) {
        ligne.setInventaire(this);
        lignes.add(ligne);
    }
}

package ma.gestcom.modules.tiers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

/** A contact person at a supplier ("interlocuteur"). */
@Getter
@Setter
@Entity
@Table(name = "fournisseur_contact")
public class FournisseurContact extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(length = 80)
    private String prenom;

    @Column(length = 80)
    private String fonction;

    @Column(length = 30)
    private String telephone;

    @Column(length = 120)
    private String email;
}

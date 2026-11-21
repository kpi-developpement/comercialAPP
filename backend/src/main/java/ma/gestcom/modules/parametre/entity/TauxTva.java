package ma.gestcom.modules.parametre.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "taux_tva")
public class TauxTva extends BaseEntity {

    @Column(nullable = false, precision = 5, scale = 2, unique = true)
    private BigDecimal taux;

    @Column(nullable = false, length = 50)
    private String libelle;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "par_defaut", nullable = false)
    private boolean parDefaut;
}

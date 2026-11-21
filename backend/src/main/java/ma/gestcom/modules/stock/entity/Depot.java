package ma.gestcom.modules.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "depot")
public class Depot extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String libelle;

    private String adresse;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "par_defaut", nullable = false)
    private boolean parDefaut;
}

package ma.gestcom.modules.parametre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.TypeReglement;

/**
 * Configurable label / activation of each payment instrument.
 */
@Getter
@Setter
@Entity
@Table(name = "mode_reglement")
public class ModeReglement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private TypeReglement code;

    @Column(nullable = false, length = 50)
    private String libelle;

    @Column(nullable = false)
    private boolean actif = true;
}

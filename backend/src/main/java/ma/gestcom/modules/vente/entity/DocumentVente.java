package ma.gestcom.modules.vente.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.modules.document.entity.AbstractDocument;

/**
 * Sales document: devis, bon de commande, bon de livraison, facture or avoir (see {@code type}).
 */
@Getter
@Setter
@Entity
@Table(name = "document_vente")
public class DocumentVente extends AbstractDocument<DocumentVenteLigne> {

    /** Devis only: offer validity date. */
    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<DocumentVenteLigne> lignes = new ArrayList<>();

    @Override
    public void addLigne(DocumentVenteLigne ligne) {
        ligne.setDocument(this);
        lignes.add(ligne);
    }
}

package ma.gestcom.modules.achat.entity;

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
 * Purchase document: BC fournisseur, réception, facture fournisseur or avoir fournisseur (see {@code type}).
 */
@Getter
@Setter
@Entity
@Table(name = "document_achat")
public class DocumentAchat extends AbstractDocument<DocumentAchatLigne> {

    /** Supplier's own number (its invoice or delivery note number). */
    @Column(name = "reference_fournisseur", length = 50)
    private String referenceFournisseur;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<DocumentAchatLigne> lignes = new ArrayList<>();

    @Override
    public void addLigne(DocumentAchatLigne ligne) {
        ligne.setDocument(this);
        lignes.add(ligne);
    }
}

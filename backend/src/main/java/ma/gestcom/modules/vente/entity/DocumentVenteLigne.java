package ma.gestcom.modules.vente.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.modules.document.entity.AbstractDocumentLigne;

@Getter
@Setter
@Entity
@Table(name = "document_vente_ligne")
public class DocumentVenteLigne extends AbstractDocumentLigne {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentVente document;
}

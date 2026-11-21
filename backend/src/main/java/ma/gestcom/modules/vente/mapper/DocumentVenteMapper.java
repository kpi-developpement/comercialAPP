package ma.gestcom.modules.vente.mapper;

import java.util.List;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentLigneResponse;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.document.dto.TvaRecapResponse;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DocumentVenteMapper {

    DocumentSummaryResponse toSummary(DocumentVente document);

    DocumentLigneResponse toLigne(DocumentVenteLigne ligne);

    @Mapping(target = "referenceFournisseur", ignore = true)
    @Mapping(target = "depotLibelle", source = "depotLibelle")
    @Mapping(target = "montantEnLettres", source = "montantEnLettres")
    @Mapping(target = "recapTva", source = "recapTva")
    @Mapping(target = "transformationsPossibles", source = "transformationsPossibles")
    @Mapping(target = "warnings", source = "warnings")
    DocumentResponse toResponse(DocumentVente document, String depotLibelle, String montantEnLettres,
                                List<TvaRecapResponse> recapTva, List<TypeDocument> transformationsPossibles,
                                List<String> warnings);

    @Mapping(target = "valide", expression = "java(document.isValide())")
    FactureInfo toFactureInfo(DocumentVente document);
}

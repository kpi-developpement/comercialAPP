package ma.gestcom.modules.achat.mapper;

import java.util.List;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.achat.entity.DocumentAchat;
import ma.gestcom.modules.achat.entity.DocumentAchatLigne;
import ma.gestcom.modules.document.dto.DocumentLigneResponse;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.document.dto.TvaRecapResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DocumentAchatMapper {

    DocumentSummaryResponse toSummary(DocumentAchat document);

    DocumentLigneResponse toLigne(DocumentAchatLigne ligne);

    @Mapping(target = "dateValidite", ignore = true)
    @Mapping(target = "depotLibelle", source = "depotLibelle")
    @Mapping(target = "montantEnLettres", source = "montantEnLettres")
    @Mapping(target = "recapTva", source = "recapTva")
    @Mapping(target = "transformationsPossibles", source = "transformationsPossibles")
    @Mapping(target = "warnings", source = "warnings")
    DocumentResponse toResponse(DocumentAchat document, String depotLibelle, String montantEnLettres,
                                List<TvaRecapResponse> recapTva, List<TypeDocument> transformationsPossibles,
                                List<String> warnings);

    @Mapping(target = "valide", expression = "java(document.isValide())")
    FactureInfo toFactureInfo(DocumentAchat document);
}

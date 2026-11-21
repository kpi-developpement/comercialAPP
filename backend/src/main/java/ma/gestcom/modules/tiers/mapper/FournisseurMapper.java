package ma.gestcom.modules.tiers.mapper;

import ma.gestcom.modules.tiers.dto.FournisseurContactDto;
import ma.gestcom.modules.tiers.dto.FournisseurRequest;
import ma.gestcom.modules.tiers.dto.FournisseurResponse;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import ma.gestcom.modules.tiers.entity.Fournisseur;
import ma.gestcom.modules.tiers.entity.FournisseurContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface FournisseurMapper {

    FournisseurResponse toResponse(Fournisseur entity);

    FournisseurContactDto toContactDto(FournisseurContact contact);

    TiersSummaryResponse toSummary(Fournisseur entity);

    @Mapping(target = "plafondCredit", expression = "java(java.math.BigDecimal.ZERO)")
    TiersInfo toInfo(Fournisseur entity);

    @Mapping(target = "contacts", ignore = true)
    Fournisseur toEntity(FournisseurRequest request);

    @Mapping(target = "contacts", ignore = true)
    void update(@MappingTarget Fournisseur entity, FournisseurRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fournisseur", ignore = true)
    FournisseurContact toContact(FournisseurContactDto dto);
}

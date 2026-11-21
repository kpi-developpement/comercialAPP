package ma.gestcom.modules.utilisateur.mapper;

import ma.gestcom.modules.utilisateur.dto.AuthUser;
import ma.gestcom.modules.utilisateur.dto.UtilisateurCreateRequest;
import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;
import ma.gestcom.modules.utilisateur.dto.UtilisateurUpdateRequest;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UtilisateurMapper {

    UtilisateurResponse toResponse(Utilisateur entity);

    AuthUser toAuthUser(Utilisateur entity);

    @Mapping(target = "passwordHash", ignore = true)
    Utilisateur toEntity(UtilisateurCreateRequest request);

    void update(@MappingTarget Utilisateur entity, UtilisateurUpdateRequest request);
}

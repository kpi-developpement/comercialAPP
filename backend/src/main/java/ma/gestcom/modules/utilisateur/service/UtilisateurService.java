package ma.gestcom.modules.utilisateur.service;

import java.util.Optional;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.utilisateur.dto.AuthUser;
import ma.gestcom.modules.utilisateur.dto.UtilisateurCreateRequest;
import ma.gestcom.modules.utilisateur.dto.UtilisateurFilter;
import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;
import ma.gestcom.modules.utilisateur.dto.UtilisateurUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface UtilisateurService {

    PageResponse<UtilisateurResponse> search(UtilisateurFilter filter, Pageable pageable);

    UtilisateurResponse get(Long id);

    UtilisateurResponse getByUsername(String username);

    Optional<AuthUser> findAuthUser(String username);

    UtilisateurResponse create(UtilisateurCreateRequest request);

    UtilisateurResponse update(Long id, UtilisateurUpdateRequest request, String currentUsername);

    void resetPassword(Long id, String nouveauMotDePasse);

    void changeOwnPassword(String username, String ancienMotDePasse, String nouveauMotDePasse);
}

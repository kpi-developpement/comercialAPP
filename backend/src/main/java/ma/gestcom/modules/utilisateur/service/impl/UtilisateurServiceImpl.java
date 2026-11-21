package ma.gestcom.modules.utilisateur.service.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.enums.Role;
import ma.gestcom.modules.utilisateur.dto.AuthUser;
import ma.gestcom.modules.utilisateur.dto.UtilisateurCreateRequest;
import ma.gestcom.modules.utilisateur.dto.UtilisateurFilter;
import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;
import ma.gestcom.modules.utilisateur.dto.UtilisateurUpdateRequest;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import ma.gestcom.modules.utilisateur.mapper.UtilisateurMapper;
import ma.gestcom.modules.utilisateur.repository.UtilisateurRepository;
import ma.gestcom.modules.utilisateur.repository.UtilisateurSpecifications;
import ma.gestcom.modules.utilisateur.service.UtilisateurService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository repository;
    private final UtilisateurMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<UtilisateurResponse> search(UtilisateurFilter filter, Pageable pageable) {
        return PageResponse.from(repository.findAll(UtilisateurSpecifications.fromFilter(filter), pageable),
                mapper::toResponse);
    }

    @Override
    public UtilisateurResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    public UtilisateurResponse getByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + username));
    }

    @Override
    public Optional<AuthUser> findAuthUser(String username) {
        return repository.findByUsernameIgnoreCase(username).map(mapper::toAuthUser);
    }

    @Override
    @Transactional
    public UtilisateurResponse create(UtilisateurCreateRequest request) {
        if (repository.existsByUsernameIgnoreCase(request.username())) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "L'identifiant « " + request.username() + " » existe déjà");
        }
        Utilisateur entity = mapper.toEntity(request);
        entity.setPasswordHash(passwordEncoder.encode(request.motDePasse()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public UtilisateurResponse update(Long id, UtilisateurUpdateRequest request, String currentUsername) {
        Utilisateur entity = find(id);
        boolean self = entity.getUsername().equalsIgnoreCase(currentUsername);
        if (self && (!request.actif() || request.role() != Role.ADMIN)) {
            throw new BusinessException(ErrorCodes.SELF_MODIFICATION,
                    "Vous ne pouvez pas désactiver votre compte ni retirer votre propre rôle administrateur");
        }
        mapper.update(entity, request);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String nouveauMotDePasse) {
        find(id).setPasswordHash(passwordEncoder.encode(nouveauMotDePasse));
    }

    @Override
    @Transactional
    public void changeOwnPassword(String username, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur entity = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + username));
        if (!passwordEncoder.matches(ancienMotDePasse, entity.getPasswordHash())) {
            throw new BusinessException(ErrorCodes.INVALID_PASSWORD, "L'ancien mot de passe est incorrect");
        }
        entity.setPasswordHash(passwordEncoder.encode(nouveauMotDePasse));
    }

    private Utilisateur find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Utilisateur", id));
    }
}

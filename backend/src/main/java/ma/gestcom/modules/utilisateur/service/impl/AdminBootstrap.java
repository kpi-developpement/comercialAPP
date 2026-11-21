package ma.gestcom.modules.utilisateur.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.gestcom.common.enums.Role;
import ma.gestcom.config.GestcomProperties;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import ma.gestcom.modules.utilisateur.repository.UtilisateurRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the first administrator on an empty database, using {@code gestcom.admin.*} settings.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final GestcomProperties properties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        GestcomProperties.Admin admin = properties.admin();
        if (admin == null || admin.password() == null || admin.password().isBlank()) {
            log.warn("Aucun utilisateur en base et GESTCOM_ADMIN_PASSWORD non défini : aucun administrateur créé");
            return;
        }
        Utilisateur user = new Utilisateur();
        user.setUsername(admin.username());
        user.setNomComplet("Administrateur");
        user.setRole(Role.ADMIN);
        user.setActif(true);
        user.setPasswordHash(passwordEncoder.encode(admin.password()));
        repository.save(user);
        log.info("Administrateur initial « {} » créé", admin.username());
    }
}

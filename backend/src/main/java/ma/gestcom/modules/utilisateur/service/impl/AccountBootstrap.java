package ma.gestcom.modules.utilisateur.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.gestcom.common.enums.Role;
import ma.gestcom.config.GestcomProperties;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import ma.gestcom.modules.utilisateur.repository.UtilisateurRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guarantees that every role has at least one active account at startup, in every profile.
 * <p>
 * For each role without an active user, a default account is created from {@code gestcom.admin.*} (ADMIN)
 * or {@code gestcom.accounts.*} (other roles). Existing accounts are never modified; if the default username
 * is already taken (e.g. by a deactivated user), a numbered username is used instead.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class AccountBootstrap implements ApplicationRunner {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final GestcomProperties properties;

    private record Account(String username, String password, String nomComplet) {
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        defaults().forEach((role, account) -> {
            if (repository.existsByRoleAndActifTrue(role)) {
                return;
            }
            if (account.password() == null || account.password().isBlank()) {
                log.warn("Aucun compte {} actif et aucun mot de passe par défaut configuré : compte non créé", role);
                return;
            }
            String username = freeUsername(account.username());
            Utilisateur user = new Utilisateur();
            user.setUsername(username);
            user.setNomComplet(account.nomComplet());
            user.setRole(role);
            user.setActif(true);
            user.setPasswordHash(passwordEncoder.encode(account.password()));
            repository.save(user);
            log.warn("Compte {} « {} » créé avec le mot de passe par défaut : changez-le après la première connexion",
                    role, username);
        });
    }

    private Map<Role, Account> defaults() {
        GestcomProperties.Admin admin = properties.admin();
        GestcomProperties.Accounts accounts = properties.accounts();
        String password = accounts == null ? null : accounts.password();
        Map<Role, Account> map = new LinkedHashMap<>();
        map.put(Role.ADMIN, new Account(orDefault(admin == null ? null : admin.username(), "admin"),
                admin == null ? null : admin.password(), "Administrateur"));
        map.put(Role.COMMERCIAL, new Account(orDefault(accounts == null ? null : accounts.commercial(), "commercial"),
                password, "Commercial"));
        map.put(Role.MAGASINIER, new Account(orDefault(accounts == null ? null : accounts.magasinier(), "magasinier"),
                password, "Magasinier"));
        map.put(Role.COMPTABLE, new Account(orDefault(accounts == null ? null : accounts.comptable(), "comptable"),
                password, "Comptable"));
        return map;
    }

    private String freeUsername(String base) {
        String candidate = base;
        for (int i = 2; repository.existsByUsernameIgnoreCase(candidate); i++) {
            candidate = base + i;
        }
        return candidate;
    }

    private static String orDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

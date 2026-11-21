package ma.gestcom.security;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.utilisateur.dto.AuthUser;
import ma.gestcom.modules.utilisateur.service.UtilisateurService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges Spring Security with the utilisateur module. Roles are exposed as {@code ROLE_<ROLE>} authorities.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurService utilisateurService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AuthUser user = utilisateurService.findAuthUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(user.username())
                .password(user.passwordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name())))
                .disabled(!user.actif())
                .build();
    }
}

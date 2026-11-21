package ma.gestcom.security;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Per document type write rules, used in SpEL: {@code @PreAuthorize("@documentAccess.peutModifierVente(#type)")}.
 * Bons de livraison and réceptions may also be handled by the warehouse keeper.
 */
@Component("documentAccess")
public class DocumentAccess {

    private static final Set<String> VENTE = Set.of("ROLE_ADMIN", "ROLE_COMMERCIAL");
    private static final Set<String> LOGISTIQUE = Set.of("ROLE_ADMIN", "ROLE_COMMERCIAL", "ROLE_MAGASINIER");

    public boolean peutModifierVente(String type) {
        return aUnRole("livraisons".equals(type) ? LOGISTIQUE : VENTE);
    }

    public boolean peutModifierAchat(String type) {
        return aUnRole("receptions".equals(type) || "commandes".equals(type) ? LOGISTIQUE : VENTE);
    }

    private static boolean aUnRole(Set<String> roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(roles::contains);
    }
}

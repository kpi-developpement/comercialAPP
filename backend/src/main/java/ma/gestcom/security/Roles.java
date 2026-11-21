package ma.gestcom.security;

/**
 * SpEL expressions used in {@code @PreAuthorize}. Centralised so the role matrix lives in one place
 * (mirrored by frontend/src/lib/permissions.ts).
 */
public final class Roles {

    public static final String ADMIN = "hasRole('ADMIN')";
    public static final String AUTHENTICATED = "isAuthenticated()";

    public static final String ARTICLE_WRITE = "hasAnyRole('ADMIN','COMMERCIAL','MAGASINIER')";
    public static final String TIERS_WRITE = "hasAnyRole('ADMIN','COMMERCIAL','COMPTABLE')";
    public static final String FOURNISSEUR_WRITE = "hasAnyRole('ADMIN','COMMERCIAL','MAGASINIER','COMPTABLE')";

    public static final String VENTE_READ = "hasAnyRole('ADMIN','COMMERCIAL','COMPTABLE','MAGASINIER')";
    public static final String VENTE_WRITE = "hasAnyRole('ADMIN','COMMERCIAL')";
    public static final String LIVRAISON_WRITE = "hasAnyRole('ADMIN','COMMERCIAL','MAGASINIER')";

    public static final String ACHAT_READ = "hasAnyRole('ADMIN','COMMERCIAL','COMPTABLE','MAGASINIER')";
    public static final String ACHAT_WRITE = "hasAnyRole('ADMIN','COMMERCIAL','MAGASINIER')";

    public static final String STOCK_READ = "hasAnyRole('ADMIN','COMMERCIAL','MAGASINIER','COMPTABLE')";
    public static final String STOCK_WRITE = "hasAnyRole('ADMIN','MAGASINIER')";

    public static final String REGLEMENT_READ = "hasAnyRole('ADMIN','COMPTABLE','COMMERCIAL')";
    public static final String REGLEMENT_WRITE = "hasAnyRole('ADMIN','COMPTABLE')";

    public static final String DASHBOARD = "hasAnyRole('ADMIN','COMMERCIAL','COMPTABLE')";

    private Roles() {
    }
}

package ma.gestcom.modules.tiers.service;

import java.util.List;

import ma.gestcom.modules.tiers.dto.ArticleSuggereResponse;

/**
 * "Catalogue des produits suggérés" of a client: products the sales team proposes to this client.
 */
public interface ClientCatalogueService {

    List<ArticleSuggereResponse> list(Long clientId);

    ArticleSuggereResponse add(Long clientId, Long articleId);

    void remove(Long clientId, Long suggestionId);
}

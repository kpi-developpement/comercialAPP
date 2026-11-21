package ma.gestcom.modules.tiers.service.impl;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.tiers.dto.ArticleSuggereResponse;
import ma.gestcom.modules.tiers.entity.ClientArticleSuggere;
import ma.gestcom.modules.tiers.repository.ClientArticleSuggereRepository;
import ma.gestcom.modules.tiers.service.ClientCatalogueService;
import ma.gestcom.modules.tiers.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientCatalogueServiceImpl implements ClientCatalogueService {

    private final ClientArticleSuggereRepository repository;
    private final ClientService clientService;
    private final ArticleService articleService;

    @Override
    public List<ArticleSuggereResponse> list(Long clientId) {
        clientService.getInfo(clientId);
        List<ClientArticleSuggere> suggestions = repository.findByClientIdOrderByIdAsc(clientId);
        Map<Long, ArticleInfo> articles = articleService.getInfos(
                suggestions.stream().map(ClientArticleSuggere::getArticleId).toList());
        return suggestions.stream()
                .filter(s -> articles.containsKey(s.getArticleId()))
                .map(s -> toResponse(s, articles.get(s.getArticleId())))
                .toList();
    }

    @Override
    @Transactional
    public ArticleSuggereResponse add(Long clientId, Long articleId) {
        clientService.getInfo(clientId);
        ArticleInfo article = articleService.getInfo(articleId);
        if (repository.existsByClientIdAndArticleId(clientId, articleId)) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "L'article " + article.code() + " est déjà suggéré à ce client");
        }
        ClientArticleSuggere suggestion = new ClientArticleSuggere();
        suggestion.setClientId(clientId);
        suggestion.setArticleId(articleId);
        return toResponse(repository.save(suggestion), article);
    }

    @Override
    @Transactional
    public void remove(Long clientId, Long suggestionId) {
        repository.delete(repository.findByIdAndClientId(suggestionId, clientId)
                .orElseThrow(() -> new NotFoundException("Suggestion", suggestionId)));
    }

    private static ArticleSuggereResponse toResponse(ClientArticleSuggere s, ArticleInfo a) {
        return new ArticleSuggereResponse(s.getId(), a.id(), a.code(), a.designation(), a.prixVenteHt());
    }
}

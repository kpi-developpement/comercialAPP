package ma.gestcom.modules.article.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.ArticleType;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.article.dto.ArticleFilter;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.dto.ArticleRequest;
import ma.gestcom.modules.article.dto.ArticleResponse;
import ma.gestcom.modules.article.dto.ArticleSummaryResponse;
import ma.gestcom.modules.article.entity.Article;
import ma.gestcom.modules.article.entity.Famille;
import ma.gestcom.modules.article.mapper.ArticleMapper;
import ma.gestcom.modules.article.repository.ArticleRepository;
import ma.gestcom.modules.article.repository.ArticleSpecifications;
import ma.gestcom.modules.article.repository.FamilleRepository;
import ma.gestcom.modules.article.service.ArticleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;
    private final FamilleRepository familleRepository;
    private final ArticleMapper mapper;

    @Override
    public PageResponse<ArticleSummaryResponse> search(ArticleFilter filter, Pageable pageable) {
        return PageResponse.from(repository.findAll(ArticleSpecifications.fromFilter(filter), pageable),
                mapper::toSummary);
    }

    @Override
    public ArticleResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    public ArticleResponse findByScan(String value) {
        String term = value.trim();
        return repository.findByCodeBarresAndSupprimeFalse(term)
                .or(() -> repository.findByCodeIgnoreCaseAndSupprimeFalse(term))
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Aucun article pour « " + term + " »"));
    }

    @Override
    @Transactional
    public ArticleResponse create(ArticleRequest request) {
        String codeBarres = normalizeBarcode(request.codeBarres());
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Le code article « " + request.code() + " » existe déjà");
        }
        if (codeBarres != null && repository.existsByCodeBarres(codeBarres)) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Ce code-barres est déjà attribué à un autre article");
        }
        Article entity = mapper.toEntity(request);
        entity.setCodeBarres(codeBarres);
        entity.setFamille(resolveFamille(request.familleId()));
        entity.setPump(MoneyUtil.qty(request.prixAchatHt()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public ArticleResponse update(Long id, ArticleRequest request) {
        String codeBarres = normalizeBarcode(request.codeBarres());
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Le code article « " + request.code() + " » existe déjà");
        }
        if (codeBarres != null && repository.existsByCodeBarresAndIdNot(codeBarres, id)) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Ce code-barres est déjà attribué à un autre article");
        }
        Article entity = find(id);
        mapper.update(entity, request);
        entity.setCodeBarres(codeBarres);
        entity.setFamille(resolveFamille(request.familleId()));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Article entity = find(id);
        entity.setSupprime(true);
        entity.setActif(false);
    }

    @Override
    public ArticleInfo getInfo(Long id) {
        return mapper.toInfo(find(id));
    }

    @Override
    public Map<Long, ArticleInfo> getInfos(Collection<Long> ids) {
        return repository.findAllById(ids).stream()
                .map(mapper::toInfo)
                .collect(Collectors.toMap(ArticleInfo::id, Function.identity()));
    }

    @Override
    public List<ArticleInfo> findStockables(Long familleId) {
        ArticleFilter filter = new ArticleFilter(null, familleId, ArticleType.STOCKE, true);
        return repository.findAll(ArticleSpecifications.fromFilter(filter), Sort.by("code")).stream()
                .map(mapper::toInfo)
                .toList();
    }

    @Override
    @Transactional
    public void updatePump(Long id, BigDecimal pump) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Article", id))
                .setPump(MoneyUtil.qty(pump));
    }

    private Famille resolveFamille(Long familleId) {
        if (familleId == null) {
            return null;
        }
        return familleRepository.findById(familleId).orElseThrow(() -> new NotFoundException("Famille", familleId));
    }

    private static String normalizeBarcode(String codeBarres) {
        return codeBarres == null || codeBarres.isBlank() ? null : codeBarres.trim();
    }

    private Article find(Long id) {
        return repository.findByIdAndSupprimeFalse(id).orElseThrow(() -> new NotFoundException("Article", id));
    }
}

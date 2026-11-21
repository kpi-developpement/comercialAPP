package ma.gestcom.modules.article.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.article.dto.FamilleFilter;
import ma.gestcom.modules.article.dto.FamilleRequest;
import ma.gestcom.modules.article.dto.FamilleResponse;
import ma.gestcom.modules.article.entity.Famille;
import ma.gestcom.modules.article.mapper.FamilleMapper;
import ma.gestcom.modules.article.repository.ArticleRepository;
import ma.gestcom.modules.article.repository.FamilleRepository;
import ma.gestcom.modules.article.service.FamilleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilleServiceImpl implements FamilleService {

    private final FamilleRepository repository;
    private final ArticleRepository articleRepository;
    private final FamilleMapper mapper;

    @Override
    public PageResponse<FamilleResponse> search(FamilleFilter filter, Pageable pageable) {
        Specification<Famille> spec = Specification.allOf(
                SpecificationUtil.containsAny(filter.search(), "code", "libelle"),
                SpecificationUtil.equalTo("parent.id", filter.parentId()));
        return PageResponse.from(repository.findAll(spec, pageable), mapper::toResponse);
    }

    @Override
    public List<FamilleResponse> findAll() {
        return repository.findAllByOrderByCodeAsc().stream().map(mapper::toResponse).toList();
    }

    @Override
    public FamilleResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public FamilleResponse create(FamilleRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw duplicate(request.code());
        }
        Famille entity = mapper.toEntity(request);
        entity.setParent(resolveParent(request.parentId(), null));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public FamilleResponse update(Long id, FamilleRequest request) {
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw duplicate(request.code());
        }
        Famille entity = find(id);
        mapper.update(entity, request);
        entity.setParent(resolveParent(request.parentId(), id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Famille entity = find(id);
        if (repository.existsByParentId(id)) {
            throw new BusinessException(ErrorCodes.DATA_INTEGRITY, "Cette famille contient des sous-familles");
        }
        if (articleRepository.existsByFamilleIdAndSupprimeFalse(id)) {
            throw new BusinessException(ErrorCodes.DATA_INTEGRITY, "Cette famille est utilisée par des articles");
        }
        repository.delete(entity);
    }

    /** Only two levels are allowed: a parent must itself be a root family. */
    private Famille resolveParent(Long parentId, Long selfId) {
        if (parentId == null) {
            return null;
        }
        if (parentId.equals(selfId)) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "Une famille ne peut pas être son propre parent");
        }
        Famille parent = find(parentId);
        if (parent.getParent() != null) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                    "Une sous-famille ne peut pas contenir d'autres sous-familles");
        }
        if (selfId != null && repository.existsByParentId(selfId)) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                    "Une famille ayant des sous-familles ne peut pas devenir sous-famille");
        }
        return parent;
    }

    private BusinessException duplicate(String code) {
        return new BusinessException(ErrorCodes.DUPLICATE, "Le code famille « " + code + " » existe déjà");
    }

    private Famille find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Famille", id));
    }
}

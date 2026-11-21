package ma.gestcom.modules.stock.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.stock.dto.DepotRequest;
import ma.gestcom.modules.stock.dto.DepotResponse;
import ma.gestcom.modules.stock.entity.Depot;
import ma.gestcom.modules.stock.mapper.DepotMapper;
import ma.gestcom.modules.stock.repository.DepotRepository;
import ma.gestcom.modules.stock.service.DepotService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepotServiceImpl implements DepotService {

    private final DepotRepository repository;
    private final DepotMapper mapper;

    @Override
    public PageResponse<DepotResponse> search(String search, Boolean actif, Pageable pageable) {
        Specification<Depot> spec = Specification.allOf(
                SpecificationUtil.containsAny(search, "code", "libelle"),
                SpecificationUtil.equalTo("actif", actif));
        return PageResponse.from(repository.findAll(spec, pageable), mapper::toResponse);
    }

    @Override
    public List<DepotResponse> findAll() {
        return repository.findAllByOrderByCodeAsc().stream().map(mapper::toResponse).toList();
    }

    @Override
    public DepotResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public DepotResponse create(DepotRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw duplicate(request.code());
        }
        Depot saved = repository.save(mapper.toEntity(request));
        applyDefault(saved);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DepotResponse update(Long id, DepotRequest request) {
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw duplicate(request.code());
        }
        Depot entity = find(id);
        mapper.update(entity, request);
        applyDefault(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Depot entity = find(id);
        repository.delete(entity);
        repository.flush();
    }

    @Override
    public DepotResponse requireActif(Long id) {
        Depot depot = find(id);
        if (!depot.isActif()) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "Le dépôt " + depot.getCode() + " est inactif");
        }
        return mapper.toResponse(depot);
    }

    @Override
    public Map<Long, String> getLibelles() {
        return repository.findAll().stream().collect(Collectors.toMap(Depot::getId, Depot::getLibelle));
    }

    private void applyDefault(Depot depot) {
        if (depot.isParDefaut()) {
            repository.flush();
            repository.clearDefaultExcept(depot.getId());
        }
    }

    private BusinessException duplicate(String code) {
        return new BusinessException(ErrorCodes.DUPLICATE, "Le code dépôt « " + code + " » existe déjà");
    }

    private Depot find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Dépôt", id));
    }
}

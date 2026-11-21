package ma.gestcom.modules.parametre.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.parametre.dto.TauxTvaRequest;
import ma.gestcom.modules.parametre.dto.TauxTvaResponse;
import ma.gestcom.modules.parametre.entity.TauxTva;
import ma.gestcom.modules.parametre.mapper.ParametreMapper;
import ma.gestcom.modules.parametre.repository.TauxTvaRepository;
import ma.gestcom.modules.parametre.service.TauxTvaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TauxTvaServiceImpl implements TauxTvaService {

    private final TauxTvaRepository repository;
    private final ParametreMapper mapper;

    @Override
    public List<TauxTvaResponse> findAll() {
        return repository.findAllByOrderByTauxDesc().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public TauxTvaResponse create(TauxTvaRequest request) {
        if (repository.existsByTaux(request.taux())) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Ce taux de TVA existe déjà");
        }
        TauxTva saved = repository.save(mapper.toEntity(request));
        applyDefault(saved);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TauxTvaResponse update(Long id, TauxTvaRequest request) {
        if (repository.existsByTauxAndIdNot(request.taux(), id)) {
            throw new BusinessException(ErrorCodes.DUPLICATE, "Ce taux de TVA existe déjà");
        }
        TauxTva entity = find(id);
        mapper.update(entity, request);
        applyDefault(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TauxTva entity = find(id);
        if (entity.isParDefaut()) {
            throw new BusinessException(ErrorCodes.DATA_INTEGRITY, "Impossible de supprimer le taux par défaut");
        }
        repository.delete(entity);
    }

    private void applyDefault(TauxTva entity) {
        if (entity.isParDefaut()) {
            repository.flush();
            repository.clearDefaultExcept(entity.getId());
        }
    }

    private TauxTva find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Taux de TVA", id));
    }
}

package ma.gestcom.modules.parametre.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.parametre.dto.ModeReglementRequest;
import ma.gestcom.modules.parametre.dto.ModeReglementResponse;
import ma.gestcom.modules.parametre.entity.ModeReglement;
import ma.gestcom.modules.parametre.mapper.ParametreMapper;
import ma.gestcom.modules.parametre.repository.ModeReglementRepository;
import ma.gestcom.modules.parametre.service.ModeReglementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModeReglementServiceImpl implements ModeReglementService {

    private final ModeReglementRepository repository;
    private final ParametreMapper mapper;

    @Override
    public List<ModeReglementResponse> findAll() {
        return repository.findAllByOrderByIdAsc().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ModeReglementResponse update(Long id, ModeReglementRequest request) {
        ModeReglement entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mode de règlement", id));
        mapper.update(entity, request);
        return mapper.toResponse(entity);
    }
}

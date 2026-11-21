package ma.gestcom.modules.parametre.service.impl;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.parametre.dto.SocieteRequest;
import ma.gestcom.modules.parametre.dto.SocieteResponse;
import ma.gestcom.modules.parametre.entity.Societe;
import ma.gestcom.modules.parametre.mapper.ParametreMapper;
import ma.gestcom.modules.parametre.repository.SocieteRepository;
import ma.gestcom.modules.parametre.service.SocieteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocieteServiceImpl implements SocieteService {

    private final SocieteRepository repository;
    private final ParametreMapper mapper;

    @Override
    public SocieteResponse get() {
        return mapper.toResponse(find());
    }

    @Override
    @Transactional
    public SocieteResponse update(SocieteRequest request) {
        Societe societe = find();
        mapper.update(societe, request);
        return mapper.toResponse(societe);
    }

    @Override
    public boolean isStockNegatifAutorise() {
        return find().isAutoriserStockNegatif();
    }

    private Societe find() {
        return repository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new NotFoundException("Paramètres société non initialisés"));
    }
}

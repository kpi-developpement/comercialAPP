package ma.gestcom.modules.numerotation.service.impl;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigRequest;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigResponse;
import ma.gestcom.modules.numerotation.entity.NumerotationConfig;
import ma.gestcom.modules.numerotation.entity.SequenceDocument;
import ma.gestcom.modules.numerotation.mapper.NumerotationMapper;
import ma.gestcom.modules.numerotation.repository.NumerotationConfigRepository;
import ma.gestcom.modules.numerotation.repository.SequenceDocumentRepository;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NumerotationServiceImpl implements NumerotationService {

    private final NumerotationConfigRepository configRepository;
    private final SequenceDocumentRepository sequenceRepository;
    private final NumerotationMapper mapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public String nextNumero(TypeDocument type, LocalDate date) {
        NumerotationConfig config = findConfig(type);
        int annee = date.getYear();
        SequenceDocument sequence = sequenceRepository.findForUpdate(type, annee).orElseGet(() -> {
            sequenceRepository.insertIfAbsent(type.name(), annee);
            return sequenceRepository.findForUpdate(type, annee).orElseThrow();
        });
        long next = sequence.getCompteur() + 1;
        sequence.setCompteur(next);
        return format(config, annee, next);
    }

    @Override
    public List<NumerotationConfigResponse> findAllConfigs() {
        return configRepository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public NumerotationConfigResponse updateConfig(TypeDocument type, NumerotationConfigRequest request) {
        NumerotationConfig config = findConfig(type);
        mapper.update(config, request);
        return toResponse(config);
    }

    static String format(NumerotationConfig config, int annee, long compteur) {
        String counter = String.format("%0" + config.getLongueur() + "d", compteur);
        return config.getPrefixe() + "-" + annee + "-" + counter;
    }

    private NumerotationConfigResponse toResponse(NumerotationConfig config) {
        int annee = LocalDate.now().getYear();
        long current = sequenceRepository.findCurrent(config.getTypeDocument(), annee)
                .map(SequenceDocument::getCompteur).orElse(0L);
        return mapper.toResponse(config, format(config, annee, current + 1));
    }

    private NumerotationConfig findConfig(TypeDocument type) {
        return configRepository.findByTypeDocument(type)
                .orElseThrow(() -> new NotFoundException("Configuration de numérotation absente pour " + type));
    }
}

package ma.gestcom.modules.tiers.service.impl;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.tiers.dto.FournisseurRequest;
import ma.gestcom.modules.tiers.dto.FournisseurResponse;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import ma.gestcom.modules.tiers.dto.FournisseurContactDto;
import ma.gestcom.modules.tiers.entity.Fournisseur;
import ma.gestcom.modules.tiers.entity.FournisseurContact;
import ma.gestcom.modules.tiers.mapper.FournisseurMapper;
import ma.gestcom.modules.tiers.repository.FournisseurRepository;
import ma.gestcom.modules.tiers.repository.TiersSpecifications;
import ma.gestcom.modules.tiers.service.FournisseurService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository repository;
    private final FournisseurMapper mapper;

    @Override
    public PageResponse<TiersSummaryResponse> search(TiersFilter filter, Pageable pageable) {
        return PageResponse.from(repository.findAll(TiersSpecifications.<Fournisseur>fromFilter(filter), pageable),
                mapper::toSummary);
    }

    @Override
    public FournisseurResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public FournisseurResponse create(FournisseurRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw duplicate(request.code());
        }
        Fournisseur entity = mapper.toEntity(request);
        remplacerContacts(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public FournisseurResponse update(Long id, FournisseurRequest request) {
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw duplicate(request.code());
        }
        Fournisseur entity = find(id);
        mapper.update(entity, request);
        remplacerContacts(entity, request);
        return mapper.toResponse(entity);
    }

    /** The contacts table of the form is the full list: replace the existing contacts. */
    private void remplacerContacts(Fournisseur entity, FournisseurRequest request) {
        entity.getContacts().clear();
        if (request.contacts() == null) {
            return;
        }
        for (FournisseurContactDto dto : request.contacts()) {
            FournisseurContact contact = mapper.toContact(dto);
            contact.setFournisseur(entity);
            entity.getContacts().add(contact);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Fournisseur entity = find(id);
        entity.setSupprime(true);
        entity.setActif(false);
    }

    @Override
    public TiersInfo getInfo(Long id) {
        return mapper.toInfo(find(id));
    }

    private BusinessException duplicate(String code) {
        return new BusinessException(ErrorCodes.DUPLICATE, "Le code fournisseur « " + code + " » existe déjà");
    }

    private Fournisseur find(Long id) {
        return repository.findByIdAndSupprimeFalse(id).orElseThrow(() -> new NotFoundException("Fournisseur", id));
    }
}

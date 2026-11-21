package ma.gestcom.modules.tiers.service.impl;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.tiers.dto.ClientRequest;
import ma.gestcom.modules.tiers.dto.ClientResponse;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import ma.gestcom.modules.tiers.entity.Client;
import ma.gestcom.modules.tiers.mapper.ClientMapper;
import ma.gestcom.modules.tiers.repository.ClientRepository;
import ma.gestcom.modules.tiers.repository.TiersSpecifications;
import ma.gestcom.modules.tiers.service.ClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public PageResponse<TiersSummaryResponse> search(TiersFilter filter, Pageable pageable) {
        return PageResponse.from(repository.findAll(TiersSpecifications.<Client>fromFilter(filter), pageable),
                mapper::toSummary);
    }

    @Override
    public ClientResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public ClientResponse create(ClientRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw duplicate(request.code());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw duplicate(request.code());
        }
        Client entity = find(id);
        mapper.update(entity, request);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Client entity = find(id);
        entity.setSupprime(true);
        entity.setActif(false);
    }

    @Override
    public TiersInfo getInfo(Long id) {
        return mapper.toInfo(find(id));
    }

    private BusinessException duplicate(String code) {
        return new BusinessException(ErrorCodes.DUPLICATE, "Le code client « " + code + " » existe déjà");
    }

    private Client find(Long id) {
        return repository.findByIdAndSupprimeFalse(id).orElseThrow(() -> new NotFoundException("Client", id));
    }
}

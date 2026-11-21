package ma.gestcom.modules.article.service;

import java.util.List;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.article.dto.FamilleFilter;
import ma.gestcom.modules.article.dto.FamilleRequest;
import ma.gestcom.modules.article.dto.FamilleResponse;
import org.springframework.data.domain.Pageable;

public interface FamilleService {

    PageResponse<FamilleResponse> search(FamilleFilter filter, Pageable pageable);

    List<FamilleResponse> findAll();

    FamilleResponse get(Long id);

    FamilleResponse create(FamilleRequest request);

    FamilleResponse update(Long id, FamilleRequest request);

    void delete(Long id);
}

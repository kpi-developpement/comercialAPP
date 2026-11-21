package ma.gestcom.modules.parametre.service;

import java.util.List;

import ma.gestcom.modules.parametre.dto.TauxTvaRequest;
import ma.gestcom.modules.parametre.dto.TauxTvaResponse;

public interface TauxTvaService {

    List<TauxTvaResponse> findAll();

    TauxTvaResponse create(TauxTvaRequest request);

    TauxTvaResponse update(Long id, TauxTvaRequest request);

    void delete(Long id);
}

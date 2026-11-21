package ma.gestcom.modules.parametre.service;

import java.util.List;

import ma.gestcom.modules.parametre.dto.ModeReglementRequest;
import ma.gestcom.modules.parametre.dto.ModeReglementResponse;

public interface ModeReglementService {

    List<ModeReglementResponse> findAll();

    ModeReglementResponse update(Long id, ModeReglementRequest request);
}

package ma.gestcom.modules.parametre.service;

import ma.gestcom.modules.parametre.dto.SocieteRequest;
import ma.gestcom.modules.parametre.dto.SocieteResponse;

public interface SocieteService {

    SocieteResponse get();

    SocieteResponse update(SocieteRequest request);

    boolean isStockNegatifAutorise();
}

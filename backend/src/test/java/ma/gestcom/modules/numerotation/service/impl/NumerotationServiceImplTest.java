package ma.gestcom.modules.numerotation.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.numerotation.entity.NumerotationConfig;
import ma.gestcom.modules.numerotation.entity.SequenceDocument;
import ma.gestcom.modules.numerotation.mapper.NumerotationMapper;
import ma.gestcom.modules.numerotation.repository.NumerotationConfigRepository;
import ma.gestcom.modules.numerotation.repository.SequenceDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NumerotationServiceImplTest {

    @Mock
    private NumerotationConfigRepository configRepository;
    @Mock
    private SequenceDocumentRepository sequenceRepository;
    @Mock
    private NumerotationMapper mapper;
    @InjectMocks
    private NumerotationServiceImpl service;

    @BeforeEach
    void setUp() {
        NumerotationConfig config = new NumerotationConfig();
        config.setTypeDocument(TypeDocument.FACTURE);
        config.setPrefixe("FA");
        config.setLongueur(5);
        when(configRepository.findByTypeDocument(TypeDocument.FACTURE)).thenReturn(Optional.of(config));
    }

    @Test
    void numerosConsecutifsSansTrou() {
        SequenceDocument seq = sequence(41);
        when(sequenceRepository.findForUpdate(TypeDocument.FACTURE, 2026)).thenReturn(Optional.of(seq));

        assertThat(service.nextNumero(TypeDocument.FACTURE, LocalDate.of(2026, 3, 1))).isEqualTo("FA-2026-00042");
        assertThat(service.nextNumero(TypeDocument.FACTURE, LocalDate.of(2026, 3, 2))).isEqualTo("FA-2026-00043");
        assertThat(seq.getCompteur()).isEqualTo(43L);
    }

    @Test
    void premierNumeroDeLAnneeCreeLeCompteur() {
        SequenceDocument seq = sequence(0);
        when(sequenceRepository.findForUpdate(TypeDocument.FACTURE, 2027))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(seq));

        assertThat(service.nextNumero(TypeDocument.FACTURE, LocalDate.of(2027, 1, 2))).isEqualTo("FA-2027-00001");
        verify(sequenceRepository).insertIfAbsent("FACTURE", 2027);
    }

    private static SequenceDocument sequence(long compteur) {
        SequenceDocument s = new SequenceDocument();
        s.setTypeDocument(TypeDocument.FACTURE);
        s.setCompteur(compteur);
        return s;
    }
}

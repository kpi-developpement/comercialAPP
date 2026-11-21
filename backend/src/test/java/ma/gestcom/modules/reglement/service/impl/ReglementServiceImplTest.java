package ma.gestcom.modules.reglement.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeReglement;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.reglement.dto.AffectationRequest;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.entity.Reglement;
import ma.gestcom.modules.reglement.mapper.ReglementMapper;
import ma.gestcom.modules.reglement.repository.ReglementRepository;
import ma.gestcom.modules.reglement.service.FactureGateway;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReglementServiceImplTest {

    @Mock
    private ReglementRepository repository;
    @Mock
    private ReglementMapper mapper;
    @Mock
    private FactureGateway factures;
    @Mock
    private NumerotationService numerotationService;
    @InjectMocks
    private ReglementServiceImpl service;

    @BeforeEach
    void setUp() {
        when(factures.tiers(SensTiers.CLIENT, 7L)).thenReturn(new TiersInfo(7L, "CL-1", "Client", null, null, 30,
                BigDecimal.ZERO, BigDecimal.ZERO, TypeReglement.VIREMENT, true, false, false));
        when(numerotationService.nextNumero(any(), any())).thenReturn("RGC-2026-00001");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void affecteUnReglementSurPlusieursFactures() {
        facture(1L, "1000.00", "0");
        facture(2L, "500.00", "200.00");

        service.create(SensTiers.CLIENT, requete("1300", TypeReglement.VIREMENT,
                new AffectationRequest(1L, new BigDecimal("1000")), new AffectationRequest(2L, new BigDecimal("300"))));

        verify(factures).appliquer(SensTiers.CLIENT, 1L, new BigDecimal("1000.00"));
        verify(factures).appliquer(SensTiers.CLIENT, 2L, new BigDecimal("300.00"));
        Reglement saved = saved();
        assertThat(saved.getMontantAffecte()).isEqualByComparingTo("1300");
        assertThat(saved.getMontantNonAffecte()).isEqualByComparingTo("0");
        assertThat(saved.getStatut()).isEqualTo(StatutReglement.ENCAISSE);
    }

    @Test
    void refuseUneAffectationSuperieureAuResteAPayer() {
        facture(1L, "1000.00", "900.00");
        assertThatThrownBy(() -> service.create(SensTiers.CLIENT, requete("200", TypeReglement.ESPECES,
                new AffectationRequest(1L, new BigDecimal("200")))))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.PAYMENT_ALLOCATION_INVALID);
        verify(factures, never()).appliquer(any(), anyLong(), any());
    }

    @Test
    void refuseUnTotalAffecteSuperieurAuReglement() {
        facture(1L, "1000.00", "0");
        assertThatThrownBy(() -> service.create(SensTiers.CLIENT, requete("100", TypeReglement.ESPECES,
                new AffectationRequest(1L, new BigDecimal("150")))))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.PAYMENT_ALLOCATION_INVALID);
    }

    @Test
    void chequeImpayeRendLeMontantALaFacture() {
        facture(1L, "1000.00", "0");
        service.create(SensTiers.CLIENT, requete("600", TypeReglement.CHEQUE,
                new AffectationRequest(1L, new BigDecimal("600"))));
        Reglement cheque = saved();
        assertThat(cheque.getStatut()).isEqualTo(StatutReglement.EN_PORTEFEUILLE);
        when(repository.findById(5L)).thenReturn(Optional.of(cheque));

        service.changerStatut(SensTiers.CLIENT, 5L, StatutReglement.REMIS_EN_BANQUE);
        service.changerStatut(SensTiers.CLIENT, 5L, StatutReglement.IMPAYE);

        verify(factures).appliquer(SensTiers.CLIENT, 1L, new BigDecimal("-600.00"));
        assertThat(cheque.getStatut()).isEqualTo(StatutReglement.IMPAYE);
        assertThatThrownBy(() -> service.changerStatut(SensTiers.CLIENT, 5L, StatutReglement.ENCAISSE))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.PAYMENT_STATUS_INVALID);
    }

    @Test
    void chequeSansNumeroRefuse() {
        assertThatThrownBy(() -> service.create(SensTiers.CLIENT, new ReglementRequest(7L, LocalDate.now(),
                TypeReglement.CHEQUE, BigDecimal.TEN, null, null, null, null, List.of())))
                .isInstanceOf(BusinessException.class);
    }

    private Reglement saved() {
        ArgumentCaptor<Reglement> captor = ArgumentCaptor.forClass(Reglement.class);
        verify(repository).save(captor.capture());
        Reglement r = captor.getValue();
        r.setId(5L);
        return r;
    }

    private void facture(Long id, String ttc, String regle) {
        BigDecimal total = new BigDecimal(ttc);
        BigDecimal paye = new BigDecimal(regle);
        when(factures.facture(eq(SensTiers.CLIENT), eq(id))).thenReturn(new FactureInfo(id, TypeDocument.FACTURE,
                "FA-" + id, 7L, true, LocalDate.now(), total, paye, total.subtract(paye)));
    }

    private static ReglementRequest requete(String montant, TypeReglement mode, AffectationRequest... affectations) {
        return new ReglementRequest(7L, LocalDate.now(), mode, new BigDecimal(montant),
                mode.isTitre() ? "1234567" : null, mode.isTitre() ? "BMCE" : null, null, null, List.of(affectations));
    }
}

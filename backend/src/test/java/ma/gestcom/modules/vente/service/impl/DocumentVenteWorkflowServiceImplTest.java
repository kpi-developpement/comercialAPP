package ma.gestcom.modules.vente.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import ma.gestcom.common.enums.ArticleType;
import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeReglement;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.document.dto.DocumentTypeRef;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.StockService;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import ma.gestcom.modules.vente.repository.DocumentVenteRepository;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Stock is decreased once: by the BL, never again by the invoice issued from it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentVenteWorkflowServiceImplTest {

    @Mock
    private DocumentVenteRepository repository;
    @Mock
    private DocumentVenteHelper helper;
    @Mock
    private DocumentVenteService documentService;
    @Mock
    private NumerotationService numerotationService;
    @Mock
    private StockService stockService;
    @Mock
    private DepotService depotService;
    @Mock
    private ArticleService articleService;
    @InjectMocks
    private DocumentVenteWorkflowServiceImpl service;

    @BeforeEach
    void setUp() {
        when(helper.clientActif(eq(7L), any())).thenReturn(new TiersInfo(7L, "CL", "Client", null, null, 30,
                BigDecimal.ZERO, BigDecimal.ZERO, TypeReglement.VIREMENT, true, false, false));
        when(numerotationService.nextNumero(any(), any())).thenReturn("NUM-2026-00001");
        when(articleService.getInfos(any())).thenReturn(Map.of(1L, new ArticleInfo(1L, "A1", "Article", "U",
                ArticleType.STOCKE, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("20"), new BigDecimal("6.500"),
                BigDecimal.ZERO, true)));
    }

    @Test
    void validerUnBlSortDuStock() {
        DocumentVente bl = document(TypeDocument.BON_LIVRAISON, null);
        when(helper.findBrouillon(TypeDocument.BON_LIVRAISON, 1L)).thenReturn(bl);

        service.valider(TypeDocument.BON_LIVRAISON, 1L);

        ArgumentCaptor<StockOperation> op = ArgumentCaptor.forClass(StockOperation.class);
        verify(stockService).sortie(op.capture());
        assertThat(op.getValue().quantite()).isEqualByComparingTo("3");
        assertThat(bl.getLignes().get(0).isImpacteStock()).isTrue();
        assertThat(bl.getNumero()).isEqualTo("NUM-2026-00001");
        assertThat(bl.getStatut()).isEqualTo(StatutDocument.VALIDE);
    }

    @Test
    void factureIssueDUnBlNeTouchePasAuStock() {
        DocumentVente facture = document(TypeDocument.FACTURE, 50L);
        when(helper.findBrouillon(TypeDocument.FACTURE, 2L)).thenReturn(facture);
        when(repository.findTypes(any())).thenReturn(List.of(new DocumentTypeRef(50L, TypeDocument.BON_LIVRAISON)));

        service.valider(TypeDocument.FACTURE, 2L);

        verify(stockService, never()).sortie(any());
        verify(stockService, never()).entree(any());
        assertThat(facture.getDateEcheance()).isEqualTo(facture.getDateDocument().plusDays(30));
        assertThat(facture.getLignes().get(0).getCoutUnitaire()).isEqualByComparingTo("6.5");
    }

    @Test
    void factureDirecteSortDuStock() {
        DocumentVente facture = document(TypeDocument.FACTURE, null);
        when(helper.findBrouillon(TypeDocument.FACTURE, 3L)).thenReturn(facture);

        service.valider(TypeDocument.FACTURE, 3L);

        verify(stockService).sortie(any());
        verify(helper).toResponse(any(), anyList());
    }

    private static DocumentVente document(TypeDocument type, Long sourceDocumentId) {
        DocumentVente doc = new DocumentVente();
        doc.setId(10L);
        doc.setType(type);
        doc.setTiersId(7L);
        doc.setDepotId(1L);
        doc.setDateDocument(LocalDate.of(2026, 5, 10));
        DocumentVenteLigne l = new DocumentVenteLigne();
        l.setArticleId(1L);
        l.setDesignation("Article");
        l.setQuantite(new BigDecimal("3"));
        l.setPrixUnitaireHt(BigDecimal.TEN);
        l.setTauxTva(new BigDecimal("20"));
        l.setArticleStocke(true);
        l.setSourceDocumentId(sourceDocumentId);
        doc.addLigne(l);
        DocumentCalculator.calculerTotaux(doc);
        return doc;
    }
}

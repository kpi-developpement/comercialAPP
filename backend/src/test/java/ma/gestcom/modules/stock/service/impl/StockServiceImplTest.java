package ma.gestcom.modules.stock.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import ma.gestcom.common.enums.ArticleType;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.parametre.service.SocieteService;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.entity.StockArticleDepot;
import ma.gestcom.modules.stock.entity.StockMouvement;
import ma.gestcom.modules.stock.repository.StockArticleDepotRepository;
import ma.gestcom.modules.stock.repository.StockMouvementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockArticleDepotRepository stockRepository;
    @Mock
    private StockMouvementRepository mouvementRepository;
    @Mock
    private ArticleService articleService;
    @Mock
    private SocieteService societeService;
    @InjectMocks
    private StockServiceImpl service;

    private StockArticleDepot stock;

    @BeforeEach
    void setUp() {
        stock = new StockArticleDepot();
        stock.setArticleId(1L);
        stock.setDepotId(2L);
        stock.setQuantite(new BigDecimal("10"));
    }

    @Test
    void entreeAchatRecalculeLePumpEtJournalise() {
        when(articleService.getInfo(1L)).thenReturn(article(ArticleType.STOCKE, "100"));
        when(stockRepository.findForUpdate(1L, 2L)).thenReturn(Optional.of(stock));
        when(stockRepository.sumQuantite(1L)).thenReturn(new BigDecimal("10"));

        service.entree(op("30", "120", TypeMouvement.ENTREE_ACHAT, true));

        assertThat(stock.getQuantite()).isEqualByComparingTo("40");
        verify(articleService).updatePump(eq(1L), eq(new BigDecimal("115.000")));
        StockMouvement m = mouvement();
        assertThat(m.getQuantite()).isEqualByComparingTo("30");
        assertThat(m.getStockApres()).isEqualByComparingTo("40");
        assertThat(m.getPumpApres()).isEqualByComparingTo("115");
        assertThat(m.getReferenceNumero()).isEqualTo("REC-2026-00001");
    }

    @Test
    void sortieDiminueLeStockAvecQuantiteNegativeAuJournal() {
        when(articleService.getInfo(1L)).thenReturn(article(ArticleType.STOCKE, "100"));
        when(stockRepository.findForUpdate(1L, 2L)).thenReturn(Optional.of(stock));

        service.sortie(op("4", null, TypeMouvement.SORTIE_VENTE, false));

        assertThat(stock.getQuantite()).isEqualByComparingTo("6");
        assertThat(mouvement().getQuantite()).isEqualByComparingTo("-4");
        verify(articleService, never()).updatePump(any(), any());
    }

    @Test
    void sortieRefuseeSiStockInsuffisant() {
        when(articleService.getInfo(1L)).thenReturn(article(ArticleType.STOCKE, "100"));
        when(stockRepository.findForUpdate(1L, 2L)).thenReturn(Optional.of(stock));
        when(societeService.isStockNegatifAutorise()).thenReturn(false);

        assertThatThrownBy(() -> service.sortie(op("11", null, TypeMouvement.SORTIE_VENTE, false)))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.STOCK_INSUFFICIENT);
        assertThat(stock.getQuantite()).isEqualByComparingTo("10");
        verify(mouvementRepository, never()).save(any());
    }

    @Test
    void sortieAutoriseeEnNegatifSelonParametre() {
        when(articleService.getInfo(1L)).thenReturn(article(ArticleType.STOCKE, "100"));
        when(stockRepository.findForUpdate(1L, 2L)).thenReturn(Optional.of(stock));
        when(societeService.isStockNegatifAutorise()).thenReturn(true);

        service.sortie(op("12", null, TypeMouvement.SORTIE_VENTE, false));
        assertThat(stock.getQuantite()).isEqualByComparingTo("-2");
    }

    @Test
    void servicesIgnores() {
        when(articleService.getInfo(1L)).thenReturn(article(ArticleType.SERVICE, "0"));
        service.sortie(op("5", null, TypeMouvement.SORTIE_VENTE, false));
        verify(stockRepository, never()).findForUpdate(any(), any());
        verify(mouvementRepository, never()).save(any());
    }

    private StockMouvement mouvement() {
        ArgumentCaptor<StockMouvement> captor = ArgumentCaptor.forClass(StockMouvement.class);
        verify(mouvementRepository).save(captor.capture());
        return captor.getValue();
    }

    private static StockOperation op(String qte, String cout, TypeMouvement type, boolean pump) {
        return new StockOperation(1L, 2L, new BigDecimal(qte), cout == null ? null : new BigDecimal(cout), type, pump,
                TypeDocument.RECEPTION, 9L, "REC-2026-00001", null);
    }

    private static ArticleInfo article(ArticleType type, String pump) {
        return new ArticleInfo(1L, "A1", "Article", "U", type, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("20"),
                new BigDecimal(pump), BigDecimal.ZERO, true);
    }
}

package ma.gestcom.modules.vente.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import ma.gestcom.modules.vente.service.DocumentVenteTransformationService;
import ma.gestcom.modules.vente.service.DocumentVenteWorkflowService;
import ma.gestcom.security.Roles;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sales documents. {@code type} is one of: devis, commandes, livraisons, factures, avoirs.
 */
@RestController
@RequestMapping("/api/v1/ventes")
@RequiredArgsConstructor
public class DocumentVenteController {

    private static final String WRITE = "@documentAccess.peutModifierVente(#type)";

    private final DocumentVenteService service;
    private final DocumentVenteWorkflowService workflowService;
    private final DocumentVenteTransformationService transformationService;

    @GetMapping("/{type}")
    @PreAuthorize(Roles.VENTE_READ)
    public PageResponse<DocumentSummaryResponse> search(@PathVariable String type,
                                                        @ParameterObject DocumentFilter filter,
                                                        @ParameterObject @PageableDefault(sort = "dateDocument",
                                                                direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(typeOf(type), filter, pageable);
    }

    @GetMapping("/{type}/{id}")
    @PreAuthorize(Roles.VENTE_READ)
    public DocumentResponse get(@PathVariable String type, @PathVariable Long id) {
        return service.get(typeOf(type), id);
    }

    @PostMapping("/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(WRITE)
    public DocumentResponse create(@PathVariable String type, @Valid @RequestBody DocumentRequest request) {
        return service.create(typeOf(type), request);
    }

    @PutMapping("/{type}/{id}")
    @PreAuthorize(WRITE)
    public DocumentResponse update(@PathVariable String type, @PathVariable Long id,
                                   @Valid @RequestBody DocumentRequest request) {
        return service.update(typeOf(type), id, request);
    }

    @DeleteMapping("/{type}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(WRITE)
    public void delete(@PathVariable String type, @PathVariable Long id) {
        service.delete(typeOf(type), id);
    }

    @PostMapping("/{type}/{id}/valider")
    @PreAuthorize(WRITE)
    public DocumentResponse valider(@PathVariable String type, @PathVariable Long id) {
        return workflowService.valider(typeOf(type), id);
    }

    @PostMapping("/{type}/{id}/annuler")
    @PreAuthorize(WRITE)
    public DocumentResponse annuler(@PathVariable String type, @PathVariable Long id) {
        return workflowService.annuler(typeOf(type), id);
    }

    @PostMapping("/{type}/{id}/transformer")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.VENTE_READ + " and @documentAccess.peutModifierVente(#request.cible().getPath())")
    public DocumentResponse transformer(@PathVariable String type, @PathVariable Long id,
                                        @Valid @RequestBody TransformationRequest request) {
        return transformationService.transformer(typeOf(type), id, request);
    }

    @PostMapping("/factures/grouper")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.VENTE_WRITE)
    public DocumentResponse grouper(@Valid @RequestBody GroupementRequest request) {
        return transformationService.grouperLivraisons(request);
    }

    @GetMapping(value = "/{type}/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize(Roles.VENTE_READ)
    public ResponseEntity<byte[]> pdf(@PathVariable String type, @PathVariable Long id) {
        byte[] pdf = service.pdf(typeOf(type), id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(type + "-" + id + ".pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private static TypeDocument typeOf(String path) {
        return TypeDocument.venteFromPath(path);
    }
}

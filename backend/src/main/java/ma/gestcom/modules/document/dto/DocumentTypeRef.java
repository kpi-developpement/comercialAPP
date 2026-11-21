package ma.gestcom.modules.document.dto;

import ma.gestcom.common.enums.TypeDocument;

/**
 * (id, type) pair, used to know which kind of document a line was transformed from.
 */
public record DocumentTypeRef(Long id, TypeDocument type) {
}

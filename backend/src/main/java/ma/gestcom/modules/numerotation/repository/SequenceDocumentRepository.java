package ma.gestcom.modules.numerotation.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.numerotation.entity.SequenceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SequenceDocumentRepository extends JpaRepository<SequenceDocument, Long> {

    /** SELECT ... FOR UPDATE: serializes concurrent number assignments for the same type and year. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SequenceDocument s where s.typeDocument = :type and s.annee = :annee")
    Optional<SequenceDocument> findForUpdate(@Param("type") TypeDocument type, @Param("annee") Integer annee);

    @Query("select s from SequenceDocument s where s.typeDocument = :type and s.annee = :annee")
    Optional<SequenceDocument> findCurrent(@Param("type") TypeDocument type, @Param("annee") Integer annee);

    /** Creates the yearly counter row if it does not exist yet (safe under concurrency). */
    @Modifying
    @Query(value = """
            insert into sequence_document (type_document, annee, compteur, created_at, updated_at, version)
            values (:type, :annee, 0, now(), now(), 0)
            on conflict (type_document, annee) do nothing
            """, nativeQuery = true)
    int insertIfAbsent(@Param("type") String type, @Param("annee") Integer annee);
}

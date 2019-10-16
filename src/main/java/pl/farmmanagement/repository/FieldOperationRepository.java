package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.farmmanagement.model.FieldOperation;

import java.util.List;

public interface FieldOperationRepository extends JpaRepository<FieldOperation, Long> {

  List<FieldOperation> findAllByFieldEntityIdOrderByOperationDate(Long id);
}

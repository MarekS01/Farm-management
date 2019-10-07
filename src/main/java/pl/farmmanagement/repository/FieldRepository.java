package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;

public interface FieldRepository extends JpaRepository<FieldEntity,Long> {

    FieldEntity findByNameIgnoreCase(String fieldName);
}

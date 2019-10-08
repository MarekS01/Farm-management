package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.farmmanagement.model.FieldEntity;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<FieldEntity,Long> {

    @Query("SELECT f from FieldEntity f join f.user u where lower(f.name) like ?1 and u.userName = ?2")
    Optional<FieldEntity> findByNameIgnoreCaseAndUserName(String fieldName, String userName);
}

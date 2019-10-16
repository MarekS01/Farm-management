package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.farmmanagement.model.Field;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field,Long> {

    @Query("SELECT f from Field f join f.user u where lower(f.name) like ?1 and u.userName = ?2")
    Optional<Field> findByNameIgnoreCaseAndUserName(String fieldName, String userName);
}

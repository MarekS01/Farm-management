package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserNameIgnoreCase(String name);
    Optional<UserEntity> findByUserNameIgnoreCaseAndPassword(String userName, String password);

    @Query("SELECT userFields from UserEntity u where u.userName=?1")
    List<FieldEntity> userFieldsByUserName(String name);

    List<UserEntity> findAllByRoles(UserRole role);
}

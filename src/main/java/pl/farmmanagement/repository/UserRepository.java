package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.farmmanagement.model.Field;
import pl.farmmanagement.model.User;
import pl.farmmanagement.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserNameIgnoreCase(String name);
    Optional<User> findByUserNameIgnoreCaseAndPassword(String userName, String password);

    @Query("SELECT userFields from User u where u.userName=?1")
    List<Field> userFieldsByUserName(String name);

    List<User> findAllByRoles(UserRole role);
}

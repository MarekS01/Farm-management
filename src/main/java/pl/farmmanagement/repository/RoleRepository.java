package pl.farmmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.farmmanagement.model.UserRole;

public interface RoleRepository extends JpaRepository<UserRole, Long> {

    UserRole findByRole(String role);
}

package pl.farmmanagement.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.UserRole;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void whenFindRoleByName_thenShouldReturnsFoundRole() {
        UserRole userRole = UserRole.builder()
                .id(1L)
                .role("USER")
                .build();
        UserRole adminRole = UserRole.builder()
                .id(2L)
                .role("ADMIN")
                .build();
        testEntityManager.merge(userRole);
        testEntityManager.merge(adminRole);

        UserRole foundRole = roleRepository.findByRole("USER");

        Assert.assertEquals(userRole,foundRole);
    }

}
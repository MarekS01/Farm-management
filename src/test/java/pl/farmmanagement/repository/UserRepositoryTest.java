package pl.farmmanagement.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.model.UserRole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    private UserEntity userEntity;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userEntity = UserEntity
                .builder()
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();
    }


    @Test
    public void whenFindByUserNameIgnoreCase_thenShouldReturnsFoundUser() {
        String userNameWithUpperCase = "ROOT12";
        testEntityManager.persistAndFlush(userEntity);

        UserEntity foundUser = userRepository.findByUserNameIgnoreCase(userNameWithUpperCase);

        assertNotNull(foundUser);
        assertEquals(userEntity, foundUser);

    }

    @Test
    public void whenFindByUserNameIgnoreCaseAndPassword_thenShouldReturnsFoundUser() {
        String userNameWithUpperCase = "Root12";
        String userPass = "root1234";

        testEntityManager.persistAndFlush(userEntity);

        Optional<UserEntity> foundUser = userRepository
                .findByUserNameIgnoreCaseAndPassword(userNameWithUpperCase, userPass);

        assertTrue(foundUser.isPresent());
        assertEquals(userEntity, foundUser.get());
    }

    @Test
    public void whenFindUserFieldsByUserName_thenShouldReturnsUserFields() {
        String userName = "root12";
        FieldEntity field1 = FieldEntity
                .builder()
                .name("Field-1")
                .area(0.8)
                .user(userEntity)
                .build();
        FieldEntity field2 = FieldEntity
                .builder()
                .name("Field-2")
                .area(3.0)
                .user(userEntity)
                .build();
        List<FieldEntity> userFields = Arrays.asList(field1, field2);
        userEntity.setUserFields(userFields);
        testEntityManager.persistAndFlush(userEntity);

        List<FieldEntity> foundUserFields = userRepository.userFieldsByUserName(userName);

        assertEquals(userFields, foundUserFields);
    }

    @Test
    public void whenFindAllUserWithUserRole_thenShouldReturnsOnlyUserWithUserRole() {
        UserRole userRole = UserRole.builder()
                .id(1L)
                .role("USER")
                .build();
        UserRole adminRole = UserRole.builder()
                .id(2L)
                .role("ADMIN")
                .build();
        UserEntity userEntityWithAdminRole = UserEntity.builder()
                .userName("admin")
                .password("admin")
                .roles(Sets.newSet(adminRole))
                .build();
        userEntity.setRoles(Sets.newSet(userRole));

        testEntityManager.persistAndFlush(userEntity);
        testEntityManager.persistAndFlush(userEntityWithAdminRole);

        List<UserEntity> userWithAdminRole = userRepository.findAllByRoles(userRole);

        assertEquals(1,userWithAdminRole.size());
        assertEquals(userEntity,userWithAdminRole.get(0));
    }


}
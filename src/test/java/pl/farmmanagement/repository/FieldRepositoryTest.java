package pl.farmmanagement.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FieldRepositoryTest {

    private static final double MIN_FIELD_AREA = 0.01;
    private FieldEntity fieldEntity;
    private UserEntity userEntity;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private FieldRepository fieldRepository;

    @Before
    public void setUp() {
        userEntity = UserEntity.builder()
                .id(1000L)
                .userName("user")
                .password("password")
                .build();

        fieldEntity = FieldEntity
                .builder()
                .name("FIELD1")
                .area(0.8)
                .user(userEntity)
                .build();


    }

    @Test
    public void whenFindFieldByNameIgnoreCaseAndUser_thenReturnsFoundField(){
        String fieldNameWithoutUpperCase = "field1";
        testEntityManager.merge(userEntity);
        testEntityManager.persistAndFlush(fieldEntity);

        Optional<FieldEntity> foundField = fieldRepository
                .findByNameIgnoreCaseAndUserName(fieldNameWithoutUpperCase,userEntity.getUserName());

        assertTrue(foundField.isPresent());
        assertEquals("FIELD1",foundField.get().getName());
        assertEquals(0.8,foundField.get().getArea(), MIN_FIELD_AREA);
    }

}
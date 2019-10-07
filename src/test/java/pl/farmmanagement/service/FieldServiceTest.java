package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.FieldRepository;
import pl.farmmanagement.repository.UserRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FieldServiceTest {

    private static final double MIN_FIELD_SIZE = 0.01;
    private FieldEntity fieldEntity;
    private FieldDTO fieldDTO;

    @Autowired
    private FieldService fieldService;

    @MockBean
    private FieldRepository fieldRepository;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp(){
        fieldEntity = FieldEntity
                .builder()
                .name("Field-1")
                .area(1.5)
                .build();

        fieldDTO = FieldDTO
                .builder()
                .name("Field-1")
                .area(1.5)
                .build();
    }

    @Test
    public void whenAddField_thenSaveFieldAndReturnsCorrectFieldDetailsWithId() {
        Mockito.when(fieldRepository.save(fieldEntity))
                .then((Answer<FieldEntity>) invocationOnMock -> {
                    fieldEntity.setId(1L);
                    return fieldEntity;
                });

        FieldDTO savedField = fieldService.addField(fieldDTO);

        assertEquals(Long.valueOf(1), savedField.getId());
        assertEquals(fieldDTO.getName(),savedField.getName() );
        assertEquals(fieldDTO.getArea(),savedField.getArea(), MIN_FIELD_SIZE);
        assertNull(savedField.getOperationsList());
        assertNull(savedField.getUserEntity());
    }

    @Test
    public void whenFindFieldOwnerByName_thenReturnsFieldOwner(){
        String userName = "user";
        UserEntity userEntity = UserEntity
                .builder()
                .id(1L)
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();

        Mockito.when(userRepository.findByUserNameIgnoreCase(userName)).thenReturn(userEntity);

        UserEntity foundUser = fieldService.findFieldOwnerByName(userName);

        assertEquals(userEntity,foundUser);
    }

    @Test
    public void whenFindFieldById_thenReturnsFoundField(){
        Mockito.when(fieldRepository.findById(1L)).thenReturn(Optional.of(fieldEntity));

        FieldDTO foundField = fieldService.findFieldById(1L);

        assertEquals(fieldDTO,foundField);
    }

    @Test
    public void whenDeleteField_thenUseDeleteByIdMethod(){
        fieldService.deleteField(1L);

        Mockito.verify(fieldRepository,Mockito.times(1)).deleteById(1L);
    }

}
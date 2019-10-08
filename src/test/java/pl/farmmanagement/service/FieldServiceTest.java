package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FieldServiceTest {

    private static final double MIN_FIELD_SIZE = 0.01;
    private FieldEntity fieldEntity;
    private FieldDTO fieldDTO;
    private UserEntity userEntity;

    @Autowired
    private FieldService fieldService;

    @MockBean
    private FieldRepository fieldRepository;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
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

        userEntity = UserEntity
                .builder()
                .id(1L)
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();
    }

    @Test
    public void whenAddField_thenSaveFieldAndReturnsCorrectFieldDetailsWithId() {
        when(userRepository.findByUserNameIgnoreCase("root12")).thenReturn(userEntity);
        when(fieldRepository.save(any())).thenReturn(fieldEntity);

        FieldDTO savedField = fieldService.addField("root12", fieldDTO);

        assertEquals(fieldDTO.getName(), savedField.getName());
        assertEquals(fieldDTO.getArea(), savedField.getArea(), MIN_FIELD_SIZE);
        assertNull(savedField.getOperationsList());
        assertNull(savedField.getUserEntity());
    }

    @Test
    public void whenFindFieldOwnerByName_thenReturnsFieldOwner() {
        String userName = "user";
        when(userRepository.findByUserNameIgnoreCase(userName)).thenReturn(userEntity);

        UserEntity foundUser = fieldService.findFieldOwnerByName(userName);

        assertEquals(userEntity, foundUser);
    }

    @Test
    public void whenFindFieldById_thenReturnsFoundField() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(fieldEntity));

        FieldDTO foundField = fieldService.findFieldById(1L);

        assertEquals(fieldDTO, foundField);
    }

    @Test
    public void whenDeleteField_thenUseDeleteByIdMethod() {
        fieldService.deleteField(1L);

        verify(fieldRepository, times(1)).deleteById(1L);
    }

}
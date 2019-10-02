package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserEntity userEntity;
    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() {
        userEntity = UserEntity
                .builder()
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();

        user = User
                .builder()
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();
    }

    @Test
    public void whenAddUser_thenSaveUserAndReturnsCorrectUserDetailsWithId() {

        Mockito.when(userRepository.save(userEntity))
                .then((Answer<UserEntity>) invocationOnMock -> {
                    userEntity.setId(1L);
                    return userEntity;
                });

        User addedUser = userService.add(user);

        assertEquals(Long.valueOf(1), addedUser.getId());
        assertEquals(user.getUserName(), addedUser.getUserName());
        assertEquals(user.getPassword(), addedUser.getPassword());
        assertEquals(user.getEMail(), addedUser.getEMail());
        assertNull(addedUser.getGivenName());
        assertNull(addedUser.getSurname());
    }

//    @Test
//    public void whenGetByUserName_thenReturnsSearchedUser(){
//        String userName = "root12";
//        Mockito.when(userRepository.findByUserNameIgnoreCase(userName)).thenReturn(userEntity);
//
//        UpdateUserDTO returnedUser = userService.getByUserName(userName);
//
//        assertEquals(user,returnedUser);
//    }

    @Test
    public void whenGetByUserNameAndPassword_thenReturnsSearchedUser(){
        String userName = "root12";
        String password = "root1234";

        Mockito.when(userRepository.findByUserNameIgnoreCaseAndPassword(userName,password))
                .thenReturn(Optional.of(userEntity));

        Optional<User> foundUser = userService.getByUserNameAndPassword(userName, password);

        assertEquals(user,foundUser.get());
    }

    @Test
    public void whenGetAllUserFieldsById_thenReturnsAllUserFields(){
        FieldEntity field = FieldEntity
                .builder()
                .name("Field-1")
                .area(2.2)
                .build();

        Mockito.when(userRepository.userFieldsById(1L))
                .thenReturn(Collections.singletonList(field));
        List<FieldEntity> userFields = userService.getAllUserFieldById(1L);

        assertEquals(Collections.singletonList(field),userFields);
    }


}
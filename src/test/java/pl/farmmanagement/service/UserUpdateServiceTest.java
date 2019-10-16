package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.dto.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.repository.UserRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserUpdateServiceTest {

    private User userEntity;
    private UpdateUserDTO updateUserDTO;

    @Autowired
    private UserUpdateService userUpdateService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp(){
        userEntity = User.builder()
                .id(1L)
                .surname("user")
                .password("user")
                .build();

        updateUserDTO = UpdateUserDTO.builder()
                .id(1L)
                .surname("user")
                .password("user")
                .build();


    }

    @Test
    public void whenFindByUserName_thenReturnsUser(){
        String userName = "user";
        when(userRepository.findByUserNameIgnoreCase(userName)).thenReturn(userEntity);

        UpdateUserDTO theUser = userUpdateService.findByUserName(userName);

        assertEquals(updateUserDTO,theUser);
    }

    @Test
    public void whenUpdateUserDetails_thenUserIsUpdated(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        userUpdateService.updateUserGivenName(updateUserDTO);
        userUpdateService.updateUserSurname(updateUserDTO);
        userUpdateService.updateUserEmail(updateUserDTO);
        userUpdateService.updateUserPassword(updateUserDTO);

        verify(userRepository,times(4)).save(any(User.class));
    }
}
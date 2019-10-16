package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.*;
import pl.farmmanagement.model.dto.UserDTO;
import pl.farmmanagement.repository.RoleRepository;
import pl.farmmanagement.repository.UserRepository;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    private User userEntity;
    private UserDTO userDTO;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        userEntity = User
                .builder()
                .id(1L)
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();

        userDTO = UserDTO
                .builder()
                .id(1L)
                .userName("root12")
                .password("root1234")
                .eMail("root@gmail.com")
                .build();
    }

    @Test
    public void whenAddUser_thenSaveUserAndReturnsCorrectUserDetails() {
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDTO addedUser = userService.add(userDTO);

        assertEquals(Long.valueOf(1), addedUser.getId());
        assertEquals(userDTO.getUserName(), addedUser.getUserName());
        assertEquals(userDTO.getEMail(), addedUser.getEMail());
        assertNull(addedUser.getGivenName());
        assertNull(addedUser.getSurname());
    }

    @Test
    public void whenFindAllUsers_thenReturnsAllUsersList(){
        UserRole userRole = UserRole.builder()
                .id(1L)
                .role("USER")
                .build();

        when(roleRepository.findByRole("USER")).thenReturn(userRole);
        when(userRepository.findAllByRoles(userRole))
                .thenReturn(Arrays.asList(userEntity));

        List<UserDTO> allUsers = userService.findAllUsers();

        assertEquals(1,allUsers.size());
        assertEquals(Long.valueOf(1),allUsers.get(0).getId());
    }

    @Test
    public void whenFindUserByExistingUserId_theReturnsUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Optional<UserDTO> foundedUser = userService.findByUserId(1L);

        assertEquals(userDTO,foundedUser.get());
    }

    @Test
    public void whenFindUserByNotExistingUserId_thenNotFindUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDTO> foundedUser = userService.findByUserId(1L);

        assertFalse(foundedUser.isPresent());
    }

    @Test
    public void whenDeleteUserById_thenRemoveUser(){
        userService.delete(1L);

        verify(userRepository,times(1)).deleteById(1L);
    }

    @Test
    public void whenGetByUserNameAndPassword_thenReturnsSearchedUser() {
        String userName = "root12";
        String password = "root1234";

        when(userRepository.findByUserNameIgnoreCaseAndPassword(userName, password))
                .thenReturn(Optional.of(userEntity));

        Optional<UserDTO> foundUser = userService.findByUserNameAndPassword(userName, password);

        assertTrue(foundUser.isPresent());
        assertEquals(userDTO, foundUser.get());
    }

    @Test
    public void whenGetAllUserFieldsByUserName_thenReturnsAllUserFields() {
        String userName = "user";

        Field field = Field
                .builder()
                .name("Field-1")
                .area(2.2)
                .build();

        List<Field> fieldsList = Arrays.asList(field);

        when(userRepository.userFieldsByUserName(userName))
                .thenReturn(fieldsList);
        List<Field> userFields = userService.findAllUserFieldByUserName(userName);

        assertEquals(fieldsList, userFields);
    }
}
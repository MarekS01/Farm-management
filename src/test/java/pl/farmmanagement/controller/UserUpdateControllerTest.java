package pl.farmmanagement.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityConfig;
import pl.farmmanagement.security.SecurityUserDetailsService;
import pl.farmmanagement.service.UserService;
import pl.farmmanagement.service.UserUpdateService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserUpdateController.class)
@MockBean({SecurityUserDetailsService.class, AuthenticationSuccessHandler.class, UserRepository.class})
@WithMockUser
public class UserUpdateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserUpdateService userUpdateService;

    @MockBean
    private UserService userService;

    @Autowired
    private SecurityConfig securityConfig;

    @Captor
    private ArgumentCaptor<UpdateUserDTO> argumentCaptor;

    private UpdateUserDTO theUser;

    @Before
    public void setUp() {
        theUser = UpdateUserDTO.builder()
                .id(1L)
                .givenName("User")
                .surname("Root")
                .eMail("user@root.com")
                .password("1234user")
                .rePassword("1234user")
                .build();
    }

    @Test
    public void whenUserGoToCockpitView_thenReturnStatusOkAndViewDetails() throws Exception {

        when(userUpdateService.findByUserName("user")).thenReturn(theUser);

        mvc.perform(get("/userUpdate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("userCockpit"))
                .andExpect(model().attribute("user", theUser));
    }

    @Test
    public void whenNewUserGivenNameIsCorrect_thenReturnsPositiveUpdateMessageAndUpdateGivenName() throws Exception {

        String correctGivenName = "user";
        theUser.setGivenName(correctGivenName);

        mvc.perform(post("/userUpdate/givenName")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "The first name was changed successfully"));

        Mockito.verify(userUpdateService, times(1)).updateUserGivenName(argumentCaptor.capture());
        assertEquals("user", argumentCaptor.getValue().getGivenName());
    }

    @Test
    public void whenNewUserGivenNameIsIncorrect_thenReturnNegativeUpdateGivenNameMessage() throws Exception {
        String incorrectGivenName = "U";
        theUser.setGivenName(incorrectGivenName);

        mvc.perform(post("/userUpdate/givenName")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "Incorrect new first name"));
    }

    @Test
    public void whenNewUserSurnameIsCorrect_thenReturnsPositiveUpdateMessageAndUpdateSurname() throws Exception {

        String correctSurname = "root";
        theUser.setSurname(correctSurname);

        mvc.perform(post("/userUpdate/surname")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "The last name was changed successfully"));

        Mockito.verify(userUpdateService, times(1)).updateUserSurname(argumentCaptor.capture());
        assertEquals("root", argumentCaptor.getValue().getSurname());
    }

    @Test
    public void whenNewUserSurnameIsIncorrect_thenReturnsNegativeUpdateSurnameMessage() throws Exception {
        String incorrectSurname = "U";
        theUser.setSurname(incorrectSurname);

        mvc.perform(post("/userUpdate/surname")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "Incorrect new last name"));
    }

    @Test
    public void whenNewUserEmailIsCorrect_thenReturnsPositiveUpdateMessageAndUpdateEmail() throws Exception {

        String correctEmail = "user@root.com";
        theUser.setEMail(correctEmail);

        mvc.perform(post("/userUpdate/email")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "The email was changed successfully"));

        Mockito.verify(userUpdateService, times(1)).updateUserEmail(argumentCaptor.capture());
        assertEquals("user@root.com", argumentCaptor.getValue().getEMail());
    }

    @Test
    public void whenNewUserEmailIsIncorrect_thenReturnsNegativeUpdateEmailMessage() throws Exception {
        String incorrectEmail = "root@";
        theUser.setEMail(incorrectEmail);

        mvc.perform(post("/userUpdate/email")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "Incorrect new email"));
    }

    @Test
    public void whenNewUserPasswordDetailsIsCorrect_thenReturnsPositiveUpdateMessageAndUpdatePassword() throws Exception {

        String userEntityPassword = "root1234";
        String correctUserUpdateOldPassword = "root1234";

        theUser.setOldPassword(correctUserUpdateOldPassword);

        Optional<User> returnedUserFromDB =
                Optional.of(User.builder()
                .password(securityConfig.passwordEncoder().encode(userEntityPassword))
                .build());

        when(userService.findByUserId(1L)).thenReturn(returnedUserFromDB);

        mvc.perform(post("/userUpdate/password")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "The password was changed successfully"));

        Mockito.verify(userUpdateService, times(1)).updateUserPassword(argumentCaptor.capture());
        assertEquals("root1234", argumentCaptor.getValue().getOldPassword());
        assertEquals("1234user", argumentCaptor.getValue().getPassword());
        assertEquals("1234user", argumentCaptor.getValue().getRePassword());
    }

    @Test
    public void whenNewUserPasswordDetailsIsIncorrect_thenReturnedNegativeUpdatePasswordMessage() throws Exception {

        String userEntityPassword = "root1234";
        String incorrectUserUpdateOldPassword = "user1234";

        theUser.setOldPassword(incorrectUserUpdateOldPassword);

        Optional<User> returnedUserFromDB =
                Optional.of(User.builder()
                        .password(securityConfig.passwordEncoder().encode(userEntityPassword))
                        .build());

        when(userService.findByUserId(1L)).thenReturn(returnedUserFromDB);

        mvc.perform(post("/userUpdate/password")
                .flashAttr("user", theUser))
                .andDo(print())
                .andExpect(request().sessionAttribute("updateMessage", "Password change failed"));
    }
}
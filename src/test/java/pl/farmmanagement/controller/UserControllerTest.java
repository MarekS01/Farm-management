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
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.User;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityUserDetailsService;
import pl.farmmanagement.service.UserService;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@MockBean({SecurityUserDetailsService.class, AuthenticationSuccessHandler.class, UserRepository.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;

    private User user;

    @Before
    public void setUp() {
        user = User
                .builder()
                .id(1L)
                .userName("root12")
                .password("root1234")
                .rePassword("root1234")
                .eMail("root@gmail.com")
                .givenName("Root")
                .surname("Root")
                .build();
    }

    @Test
    public void whenAccessSignUpEndPoint_thenReturnsOkStatusAndViewDetails() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/signUp"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("newUser-form.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    public void whenAccessHomeEndPoint_thenReturnsOkStatusAndViewDetails() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginPage.html"));
    }

    @Test
    public void whenCreateUserWithCorrectData_thenReturnsFoundStatusAndUserDetails() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                .flashAttr("user", user))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        Mockito.verify(userService, Mockito.times(1))
                .add(argumentCaptor.capture());
        assertEquals("root12", argumentCaptor.getValue().getUserName());
        assertEquals("root1234", argumentCaptor.getValue().getPassword());
        assertEquals("root@gmail.com", argumentCaptor.getValue().getEMail());
        assertEquals("Root", argumentCaptor.getValue().getGivenName());
        assertEquals("Root", argumentCaptor.getValue().getSurname());
    }

    @Test
    public void whenCreateUserWithIncorrectDta_thenReturnsConflictStatusAndViewDetails() throws Exception {
        String invalidName = "";
        user.setUserName(invalidName);

        mvc.perform(MockMvcRequestBuilders.post("/signUp")
                .flashAttr("user", user))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.view().name("newUser-form"));
    }

    @Test
    public void whenUserIsAnonymous_thenUserIsUnauthenticatedAndSecuredViewReturnsIsFoundStatus() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/user"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @WithMockUser
    @Test
    public void whenUserIsLogged_thenUserSecuredViewReturnsOkStatusAndUserAuthentication() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/user"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(authenticated().withRoles("USER"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void whenAdminIsLogged_thenUserHasAdminAuthenticationAndAdminSecuredViewReturnsOkStatus() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(authenticated().withRoles("ADMIN"));
    }

    @WithMockUser
    @Test
    public void whenUserIsLogged_thenAdminSecuredViewIsForbidden() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @WithMockUser
    @Test
    public void whenUserLoggedInSuccessful_thenReturnsOkStatusAndAllUserField() throws Exception {

        FieldEntity field1 = FieldEntity
                .builder()
                .id(1L)
                .name("Field-1")
                .area(0.8)
                .build();

        Mockito.when(userService.findAllUserFieldByUserName("user"))
                .thenReturn(Collections.singletonList(field1));

        mvc.perform(MockMvcRequestBuilders.get("/user"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("fields", Collections.singletonList(field1)));
    }
}
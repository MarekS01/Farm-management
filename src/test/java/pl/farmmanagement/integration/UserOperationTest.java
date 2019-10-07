package pl.farmmanagement.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.FieldOperation;
import pl.farmmanagement.model.User;
import pl.farmmanagement.security.SecurityConfig;
import pl.farmmanagement.service.FieldOperationService;
import pl.farmmanagement.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserOperationTest {

    private User user;
    private FieldDTO field;
    private FieldOperation fieldOperation;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private FieldOperationService fieldOperationService;

    @Before
    public void setUp() {
        user = User.builder()
                .userName("user1234")
                .password("pass1234")
                .rePassword("pass1234")
                .eMail("user@wp.pl")
                .givenName("user")
                .surname("user")
                .build();
        field = FieldDTO.builder()
                .name("Field-1")
                .area(0.8)
                .build();
        fieldOperation = FieldOperation.builder()
                .task("Task-1")
                .operationDate(LocalDate.of(2019, 10, 01))
                .build();
    }

    @WithMockUser(username = "user1234", roles = {"USER", "ADMIN"})
    @Test
    public void whenUserWithRolesDoAllBaseOperation_thenAllOperationsAreSuccessful() throws Exception {
        mvc.perform(post("/signUp")
                .flashAttr("user", user))
                .andDo(print())
                .andExpect(redirectedUrl("/"));
        List<User> allUsers = userService.findAllUsers();
        assertEquals(1, allUsers.size());
        User foundUser = allUsers.get(0);
        assertEquals(user.getUserName(), foundUser.getUserName());
        assertTrue(securityConfig.passwordEncoder().matches(user.getPassword(), foundUser.getPassword()));


        mvc.perform(post("/user/newField")
                .flashAttr("newField", field))
                .andDo(print())
                .andExpect(redirectedUrl("/user"));
        List<FieldEntity> allUserField = userService.findAllUserFieldByUserName(foundUser.getUserName());
        assertEquals(1, allUserField.size());
        FieldEntity foundField = allUserField.get(0);
        assertEquals("Field-1", foundField.getName());

        Long foundFieldId = foundField.getId();
        String foundFieldName = foundField.getName();
        mvc.perform(post("/user/operations/new")
                .flashAttr("operation", fieldOperation)
                .sessionAttr("fieldId", foundFieldId)
                .sessionAttr("fieldName", foundFieldName))
                .andDo(print())
                .andExpect(redirectedUrl
                        (String.format("/user/operations?id=%d&fieldName=%s", foundFieldId, foundFieldName)));
        List<FieldOperation> allFieldOperations = fieldOperationService.findAllOperationsByField(foundField.getId());
        assertEquals(1, allFieldOperations.size());
        FieldOperation foundFieldOperation = allFieldOperations.get(0);
        assertEquals("Task-1", foundFieldOperation.getTask());
        assertEquals(LocalDate.of(2019, 10, 01), foundFieldOperation.getOperationDate());

        mvc.perform(get("/admin/delete")
                .param("id", foundUser.getId().toString()))
                .andDo(print())
                .andExpect(redirectedUrl("/admin"));
        List<User> allUsersAfterRemoved = userService.findAllUsers();
        assertEquals(0, allUsersAfterRemoved.size());
    }
}

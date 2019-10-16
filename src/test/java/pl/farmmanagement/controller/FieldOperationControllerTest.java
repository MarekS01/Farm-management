package pl.farmmanagement.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.farmmanagement.model.dto.FieldOperationDTO;
import pl.farmmanagement.security.SecurityUserDetailsService;
import pl.farmmanagement.service.FieldOperationService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FieldOperationController.class)
@MockBean({SecurityUserDetailsService.class, AuthenticationSuccessHandler.class})
@WithMockUser
public class FieldOperationControllerTest {

    private static final String INCORRECT_FIELD_NAME = "";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FieldOperationService fieldOperationService;

    @Captor
    private ArgumentCaptor<FieldOperationDTO> argumentCaptor;

    private FieldOperationDTO fieldOperation;

    @Before
    public void setUp() {
        fieldOperation = FieldOperationDTO.builder()
                .id(1L)
                .task("First task")
                .operationDate(LocalDate.of(2019, 10, 01))
                .build();
    }

    @WithMockUser(roles = "")
    @Test
    public void whenUserWithoutUserRoleGetSecuredOperationsEndPoint_thenReturnsForbiddenStatus() throws Exception {

        mvc.perform(get("/user/operations")
                .param("id", "1")
                .param("fieldName", "Field-1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUserGetAllFieldOperations_thenReturnViewWithAllOperations() throws Exception {
        List<FieldOperationDTO> fieldOperations = Arrays.asList(fieldOperation);

        when(fieldOperationService.findAllOperationsByField(1L)).thenReturn(fieldOperations);

        mvc.perform(get("/user/operations")
                .param("id", "1")
                .param("fieldName", "Field-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("operations", fieldOperations))
                .andExpect(view().name("operations"));
    }

    @Test
    public void whenCreateNewFieldWithCorrectData_thenAddNewFieldAndReturnsRedirectUrl() throws Exception {
        mvc.perform(post("/user/operations/new")
                .sessionAttr("fieldId", 1L)
                .sessionAttr("fieldName", "Field-1")
                .flashAttr("operation", fieldOperation))
                .andDo(print())
                .andExpect(redirectedUrl("/user/operations?id=1&fieldName=Field-1"));

        verify(fieldOperationService).addFieldOperation(any(), argumentCaptor.capture());
        assertEquals("First task", argumentCaptor.getValue().getTask());
        assertEquals(LocalDate.of(2019, 10, 01), argumentCaptor.getValue().getOperationDate());
    }

    @Test
    public void whenCreateNewFieldWithIncorrectData_theReturnsConflictStatusAndTheSameView() throws Exception {
        fieldOperation.setTask(INCORRECT_FIELD_NAME);

        mvc.perform(post("/user/operations/new")
                .sessionAttr("fieldId", 1L)
                .sessionAttr("fieldName", "Field-1")
                .flashAttr("operation", fieldOperation))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(view().name("operation-form"));
    }

    @Test
    public void whenGetUpdateOperation_thenReturnsNewFieldOperationViewWithCorrectAttribute() throws Exception {
        when(fieldOperationService.findOperationById(1L))
                .thenReturn(Optional.of(fieldOperation));

        mvc.perform(get("/user/operations/updateOperation")
                .param("id", "1"))
                .andDo(print())
                .andExpect(request().sessionAttribute("addOrUpdateOperation", "Update"))
                .andExpect(model().attribute("operation", fieldOperation));
    }

    @Test
    public void whenGetDeleteFieldOperation_thenDeleteOperationAndRedirectToOperationsList() throws Exception {
        mvc.perform(get("/user/operations/delete")
                .param("id", "1")
                .sessionAttr("fieldId", 1L)
                .sessionAttr("fieldName", "Field-1"))
                .andDo(print())
                .andExpect(redirectedUrl("/user/operations?id=1&fieldName=Field-1"));

        verify(fieldOperationService, times(1)).deleteById(1L);
    }

    @Test
    public void whenGetDoneTask_thenRedirectToOperationsListAndDoneTask() throws Exception {

        mvc.perform(get("/user/operations/doneTask")
                .param("id", "1")
                .sessionAttr("fieldId", 1L)
                .sessionAttr("fieldName", "Field-1"))
                .andDo(print())
                .andExpect(redirectedUrl("/user/operations?id=1&fieldName=Field-1"));

        verify(fieldOperationService, times(1)).doneTask(1L, 1L);
    }

}
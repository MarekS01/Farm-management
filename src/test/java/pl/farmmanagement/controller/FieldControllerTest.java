package pl.farmmanagement.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.repository.FieldRepository;
import pl.farmmanagement.security.SecurityUserDetailsService;
import pl.farmmanagement.service.FieldService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@WebMvcTest(FieldController.class)
@MockBean({SecurityUserDetailsService.class, AuthenticationSuccessHandler.class, FieldRepository.class})
@WithMockUser()
public class FieldControllerTest {

    private static final double MIN_FIELD_SIZE = 0.01;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FieldService fieldService;

    @Captor
    ArgumentCaptor<FieldDTO> argumentCaptor;

    @Test
    public void whenAccessNewFieldEndPoint_thenReturnOkStatusAndViewDetails() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/newField"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("updateOrAdd", "Add"))
                .andExpect(MockMvcResultMatchers.view().name("newField-form"));
    }

    @Test
    public void whenCreateFieldWithIncorrectData_thenReturnsConflictStatusAndViewDetails() throws Exception {
        FieldDTO field = FieldDTO
                .builder()
                .name("")
                .area(10.1)
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/user/newField")
                .flashAttr("newField", field))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.view().name("newField-form"));
    }

    @Test
    public void whenCreateFieldWithCorrectData_thenReturnsFoundStatusAndCreatedFieldDetails() throws Exception {
        FieldDTO field = FieldDTO
                .builder()
                .name("Field-1")
                .area(10.1)
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/user/newField")
                .flashAttr("newField", field))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user"));

        Mockito.verify(fieldService, times(1)).addField(any(),argumentCaptor.capture());
        assertEquals("Field-1", argumentCaptor.getValue().getName());
        assertEquals(10.1, argumentCaptor.getValue().getArea(), MIN_FIELD_SIZE);
    }

    @Test
    public void whenDeleteFieldWithIdOne_thenReturnsFoundStatusAndViewDetails() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/delete")
                .param("id", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user"));

        Mockito.verify(fieldService, times(1)).deleteField(1L);
    }

    @Test
    public void whenUpdateFieldWithIdOne_thenReturnsOkStatusAndViewDetails() throws Exception {
        FieldDTO field = FieldDTO
                .builder()
                .id(1L)
                .name("Field-1")
                .area(10.1)
                .build();

        Mockito.when(fieldService.findFieldById(1L)).thenReturn(field);

        mvc.perform(MockMvcRequestBuilders.get("/user/updateField").param("id", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("newField"))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("updateOrAdd", "Update"))
                .andExpect(MockMvcResultMatchers.view().name("newField-form"));
    }
}
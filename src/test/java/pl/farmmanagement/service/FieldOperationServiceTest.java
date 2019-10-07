package pl.farmmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.FieldOperation;
import pl.farmmanagement.model.FieldOperationEntity;
import pl.farmmanagement.repository.FieldOperationRepository;
import pl.farmmanagement.repository.FieldRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FieldOperationServiceTest {

    private FieldOperation fieldOperation;
    private FieldEntity fieldEntity;
    private FieldOperationEntity fieldOperationEntity;

    @Autowired
    private FieldOperationService fieldOperationService;

    @MockBean
    private FieldOperationRepository operationRepository;

    @MockBean
    private FieldRepository fieldRepository;

    @Before
    public void setUp() {
        fieldEntity = FieldEntity.builder()
                .id(1L)
                .name("Field-1")
                .area(5.0)
                .build();

        fieldOperation = FieldOperation.builder()
                .id(1L)
                .task("Task 1")
                .fieldEntity(fieldEntity)
                .build();

        fieldOperationEntity = FieldOperationEntity.builder()
                .id(1L)
                .task("Task 1")
                .fieldEntity(fieldEntity)
                .build();
    }

    @Test
    public void whenAddFieldOperation_thenOperationIsAdded() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(fieldEntity));

        FieldOperation savedFieldOperation = fieldOperationService.addFieldOperation(1L, fieldOperation);

        verify(operationRepository, times(1)).save(any(FieldOperationEntity.class));
        assertEquals(fieldOperation, savedFieldOperation);
    }

    @Test
    public void whenFindAllOperationsByField_thenReturnsFieldOperationsList() {
        when(operationRepository.findAllByFieldEntityIdOrderByOperationDate(1L))
                .thenReturn(Arrays.asList(fieldOperationEntity));

        List<FieldOperation> foundFieldOperations = fieldOperationService.findAllOperationsByField(1L);

        assertEquals(1, foundFieldOperations.size());
        assertEquals(fieldOperation, foundFieldOperations.get(0));
    }

    @Test
    public void whenFindOperationByExistId_thenFieldOperationIsPresent() {
        when(operationRepository.findById(1L)).thenReturn(Optional.of(fieldOperationEntity));

        Optional<FieldOperation> foundFieldOperation = fieldOperationService.findOperationById(1L);

        assertTrue(foundFieldOperation.isPresent());
        assertEquals(fieldOperation, foundFieldOperation.get());
    }

    @Test
    public void whenFindOperationByNotExistId_thenFieldOperationIsUnPresent() {
        Optional<FieldOperation> foundFieldOperation = fieldOperationService.findOperationById(1L);

        assertFalse(foundFieldOperation.isPresent());
    }

    @Test
    public void whenGetDoneTask_thenTaskIsDone() {
        when(operationRepository.findById(1L)).thenReturn(Optional.of(fieldOperationEntity));

        Optional<FieldOperation> doneFieldOperation = fieldOperationService.doneTask(1L, 1L);

        assertTrue(doneFieldOperation.isPresent());
        assertTrue(doneFieldOperation.get().isDone());
    }

    @Test
    public void whenDeleteFieldOperationById_thenOperationIsDeleted() {
        fieldOperationService.deleteById(1L);

        verify(operationRepository, times(1)).deleteById(1L);
    }
}
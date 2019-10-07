package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.FieldOperation;
import pl.farmmanagement.model.FieldOperationEntity;
import pl.farmmanagement.repository.FieldOperationRepository;
import pl.farmmanagement.repository.FieldRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldOperationService {

    private final FieldOperationRepository fieldOperationRepository;
    private final FieldRepository fieldRepository;

    public FieldOperation addFieldOperation(Long fieldId, FieldOperation fieldOperation) {
        fieldRepository.findById(fieldId)
                .ifPresent(fieldOperation::setFieldEntity);
        FieldOperationEntity fieldOperationEntity = mapToFieldOperationEntity(fieldOperation);
        fieldOperationRepository.save(fieldOperationEntity);
        return mapToFieldOperation(fieldOperationEntity);
    }

    public List<FieldOperation> findAllOperationsByField(Long id) {
        List<FieldOperationEntity> fieldOperations =
                fieldOperationRepository.findAllByFieldEntityIdOrderByOperationDate(id);
        return fieldOperations.stream()
                .map(this::mapToFieldOperation)
                .collect(Collectors.toList());
    }

    public Optional<FieldOperation> findOperationById(Long id) {
        Optional<FieldOperationEntity> foundFieldOperation = fieldOperationRepository.findById(id);
        if (foundFieldOperation.isPresent()) {
            FieldOperation fieldOperation = mapToFieldOperation(foundFieldOperation.get());
            return Optional.of(fieldOperation);
        } else {
            return Optional.empty();
        }
    }

    public Optional<FieldOperation> doneTask(Long fieldId, Long operationId) {
        Optional<FieldOperationEntity> foundOperation = fieldOperationRepository.findById(operationId);
        foundOperation.ifPresent(operation -> {
            operation.setDone(true);
            FieldOperation fieldOperation = mapToFieldOperation(operation);
            addFieldOperation(fieldId,fieldOperation);
        });
        return foundOperation.map(this::mapToFieldOperation);
    }

    public void deleteById(Long id) {
        fieldOperationRepository.deleteById(id);
    }

    private FieldOperation mapToFieldOperation(FieldOperationEntity field) {
        return FieldOperation.builder()
                .id(field.getId())
                .task(field.getTask())
                .operationDate(field.getOperationDate())
                .isDone(field.isDone())
                .fieldEntity(field.getFieldEntity())
                .build();
    }

    private FieldOperationEntity mapToFieldOperationEntity(FieldOperation field) {
        return FieldOperationEntity.builder()
                .id(field.getId())
                .task(field.getTask())
                .operationDate(field.getOperationDate())
                .isDone(field.isDone())
                .fieldEntity(field.getFieldEntity())
                .build();
    }


}

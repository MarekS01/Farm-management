package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.dto.FieldOperationDTO;
import pl.farmmanagement.model.FieldOperation;
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

    public FieldOperationDTO addFieldOperation(Long fieldId, FieldOperationDTO fieldOperation) {
        fieldRepository.findById(fieldId)
                .ifPresent(fieldOperation::setFieldEntity);
        FieldOperation fieldOperationEntity = mapToFieldOperationEntity(fieldOperation);
        fieldOperationRepository.save(fieldOperationEntity);
        return mapToFieldOperation(fieldOperationEntity);
    }

    public List<FieldOperationDTO> findAllOperationsByField(Long id) {
        List<FieldOperation> fieldOperations =
                fieldOperationRepository.findAllByFieldEntityIdOrderByOperationDate(id);
        return fieldOperations.stream()
                .map(this::mapToFieldOperation)
                .collect(Collectors.toList());
    }

    public Optional<FieldOperationDTO> findOperationById(Long id) {
        Optional<FieldOperation> foundFieldOperation = fieldOperationRepository.findById(id);
        if (foundFieldOperation.isPresent()) {
            FieldOperationDTO fieldOperation = mapToFieldOperation(foundFieldOperation.get());
            return Optional.of(fieldOperation);
        } else {
            return Optional.empty();
        }
    }

    public Optional<FieldOperationDTO> doneTask(Long fieldId, Long operationId) {
        Optional<FieldOperation> foundOperation = fieldOperationRepository.findById(operationId);
        foundOperation.ifPresent(operation -> {
            operation.setDone(true);
            FieldOperationDTO fieldOperation = mapToFieldOperation(operation);
            addFieldOperation(fieldId,fieldOperation);
        });
        return foundOperation.map(this::mapToFieldOperation);
    }

    public void deleteById(Long id) {
        fieldOperationRepository.deleteById(id);
    }

    private FieldOperationDTO mapToFieldOperation(FieldOperation field) {
        return FieldOperationDTO.builder()
                .id(field.getId())
                .task(field.getTask())
                .operationDate(field.getOperationDate())
                .isDone(field.isDone())
                .fieldEntity(field.getFieldEntity())
                .build();
    }

    private FieldOperation mapToFieldOperationEntity(FieldOperationDTO field) {
        return FieldOperation.builder()
                .id(field.getId())
                .task(field.getTask())
                .operationDate(field.getOperationDate())
                .isDone(field.isDone())
                .fieldEntity(field.getFieldEntity())
                .build();
    }


}

package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.FieldRepository;
import pl.farmmanagement.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldDTO addField(FieldDTO dto) {
        FieldEntity field = mapToFieldEntity(dto);
        FieldEntity savedField = fieldRepository.save(field);
        return mapToFieldDTO(savedField);
    }

    public UserEntity findFieldOwner(Long id){
        Optional<UserEntity> user = userRepository.findById(id);
        return user.get();
    }

    public FieldDTO findFieldById(Long id){
        FieldEntity field = fieldRepository.findById(id).get();
        return mapToFieldDTO(field);
    }

    private FieldEntity mapToFieldEntity(FieldDTO dto) {
        return FieldEntity
                .builder()
                .id(dto.getId())
                .name(dto.getName())
                .area(dto.getArea())
                .operationsList(dto.getOperationsList())
                .user(dto.getUserEntity())
                .build();
    }

    private FieldDTO mapToFieldDTO(FieldEntity field) {
        return FieldDTO
                .builder()
                .id(field.getId())
                .name(field.getName())
                .area(field.getArea())
                .operationsList(field.getOperationsList())
                .userEntity(field.getUser())
                .build();
    }
}

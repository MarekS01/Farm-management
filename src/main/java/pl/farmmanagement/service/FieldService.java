package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.FieldRepository;
import pl.farmmanagement.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldDTO addField(String userName, FieldDTO addField) {
        UserEntity fieldOwner = findFieldOwnerByName(userName);
        addField.setUserEntity(fieldOwner);
        FieldEntity field = mapToFieldEntity(addField);
        FieldEntity savedField = fieldRepository.save(field);
        return mapToFieldDTO(savedField);
    }

    public UserEntity findFieldOwnerByName(String name){
        return userRepository.findByUserNameIgnoreCase(name);
    }

    public FieldDTO findFieldById(Long id){
        FieldEntity field = fieldRepository.findById(id).get();
        return mapToFieldDTO(field);
    }

    public void deleteField(Long id){
        fieldRepository.deleteById(id);
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

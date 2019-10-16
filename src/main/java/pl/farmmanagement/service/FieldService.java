package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.dto.FieldDTO;
import pl.farmmanagement.model.Field;
import pl.farmmanagement.model.User;
import pl.farmmanagement.repository.FieldRepository;
import pl.farmmanagement.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldDTO addField(String userName, FieldDTO addField) {
        User fieldOwner = findFieldOwnerByName(userName);
        addField.setUserEntity(fieldOwner);
        Field field = mapToFieldEntity(addField);
        Field savedField = fieldRepository.save(field);
        return mapToFieldDTO(savedField);
    }

    public User findFieldOwnerByName(String name){
        return userRepository.findByUserNameIgnoreCase(name);
    }

    public FieldDTO findFieldById(Long id){
        Field field = fieldRepository.findById(id).get();
        return mapToFieldDTO(field);
    }

    public void deleteField(Long id){
        fieldRepository.deleteById(id);
    }

    private Field mapToFieldEntity(FieldDTO dto) {
        return Field
                .builder()
                .id(dto.getId())
                .name(dto.getName())
                .area(dto.getArea())
                .operationsList(dto.getOperationsList())
                .user(dto.getUserEntity())
                .build();
    }

    private FieldDTO mapToFieldDTO(Field field) {
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

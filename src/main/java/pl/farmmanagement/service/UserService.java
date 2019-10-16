package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.Field;
import pl.farmmanagement.model.dto.UserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.model.UserRole;
import pl.farmmanagement.repository.RoleRepository;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig securityConfig;

    public UserDTO add(UserDTO user) {
        User entity = mapToUserEntity(user);
        Set<UserRole> roles = Stream.of(roleRepository.findByRole("USER"))
                .collect(Collectors.toSet());
        entity.setRoles(roles);
        entity.setPassword(securityConfig.passwordEncoder().encode(entity.getPassword()));
        User savedUser = userRepository.save(entity);
        return mapToUserDTO(savedUser);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository
                .findAllByRoles(roleRepository.findByRole("USER"))
                .stream().map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findByUserId(Long id){
        Optional<User> theUser = userRepository.findById(id);
        return theUser.map(this::mapToUserDTO);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserDTO> findByUserNameAndPassword(String userName, String password) {
        Optional<User> byUserNameAndPassword = userRepository.findByUserNameIgnoreCaseAndPassword(userName, password);
        return byUserNameAndPassword.map(this::mapToUserDTO);
    }

    public List<Field> findAllUserFieldByUserName(String userName) {
        return userRepository.userFieldsByUserName(userName);
    }

    private User mapToUserEntity(UserDTO a) {
        return User.builder()
                .id(a.getId())
                .userName(a.getUserName())
                .password(a.getPassword())
                .eMail(a.getEMail())
                .givenName(a.getGivenName())
                .surname(a.getSurname())
                .build();
    }

    private UserDTO mapToUserDTO(User a) {
        return UserDTO.builder()
                .id(a.getId())
                .userName(a.getUserName())
                .password(a.getPassword())
                .eMail(a.getEMail())
                .givenName(a.getGivenName())
                .surname(a.getSurname())
                .roles(a.getRoles())
                .build();
    }
}

package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.*;
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

    public User add(User user) {
        UserEntity entity = mapToUserEntity(user);
        Set<UserRole> roles = Stream.of(roleRepository.findByRole("USER"))
                .collect(Collectors.toSet());
        entity.setRoles(roles);
        entity.setPassword(securityConfig.passwordEncoder().encode(entity.getPassword()));
        UserEntity savedUser = userRepository.save(entity);
        return mapToUserDTO(savedUser);
    }

    public List<User> findAllUsers() {
        return userRepository
                .findAllByRoles(roleRepository.findByRole("USER"))
                .stream().map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> findByUserId(Long id){
        Optional<UserEntity> theUser = userRepository.findById(id);
        return theUser.map(this::mapToUserDTO);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUserNameAndPassword(String userName, String password) {
        Optional<UserEntity> byUserNameAndPassword = userRepository.findByUserNameIgnoreCaseAndPassword(userName, password);
        return byUserNameAndPassword.map(this::mapToUserDTO);
    }

    public List<FieldEntity> findAllUserFieldByUserName(String userName) {
        return userRepository.userFieldsByUserName(userName);
    }

    private UserEntity mapToUserEntity(User a) {
        return UserEntity.builder()
                .id(a.getId())
                .userName(a.getUserName())
                .password(a.getPassword())
                .eMail(a.getEMail())
                .givenName(a.getGivenName())
                .surname(a.getSurname())
                .build();
    }

    private User mapToUserDTO(UserEntity a) {
        return User.builder()
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

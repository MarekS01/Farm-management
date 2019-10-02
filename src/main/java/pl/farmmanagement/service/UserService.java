package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.RoleRepository;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig securityConfig;

    public User add(User user) {
        UserEntity entity = mapToUser(user);
        entity.setRoles(roleRepository.findByRole("USER"));
        entity.setPassword(securityConfig.passwordEncoder().encode(entity.getPassword()));
        UserEntity savedUser = userRepository.save(entity);
        return mapToUserDTO(savedUser);
    }

    public List<User> findAllUsers() {
        return userRepository
                .findAllByRoles(roleRepository.findById(1L).get())
                .stream().map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UpdateUserDTO getByUserName(String userName) {
        UserEntity byUserName = userRepository.findByUserNameIgnoreCase(userName);
        return mapToUpdateUserDTO(byUserName);
    }

    public Optional<User> getByUserNameAndPassword(String userName, String password) {
        Optional<UserEntity> byUserNameAndPassword = userRepository.findByUserNameIgnoreCaseAndPassword(userName, password);
        return byUserNameAndPassword.map(this::mapToUserDTO);
    }

    public List<FieldEntity> getAllUserFieldById(Long userId) {
        return userRepository.userFieldsById(userId);
    }

    public void updateUserGivenName(UpdateUserDTO user) {
        Optional<UserEntity> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setGivenName(user.getGivenName());
            userRepository.save(currentUser);
        });
    }

    public void updateUserSurname(UpdateUserDTO user) {
        Optional<UserEntity> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setSurname(user.getSurname());
            userRepository.save(currentUser);
        });
    }

    public void updateUserEmail(UpdateUserDTO user) {
        Optional<UserEntity> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setEMail(user.getEMail());
            userRepository.save(currentUser);
        });
    }

    public void updateUserPassword(UpdateUserDTO user) {
        Optional<UserEntity> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(currentUser);
        });
    }

    private UserEntity mapToUser(User a) {
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

    private UpdateUserDTO mapToUpdateUserDTO(UserEntity theUser) {
        return UpdateUserDTO.builder()
                .id(theUser.getId())
                .givenName(theUser.getGivenName())
                .surname(theUser.getSurname())
                .eMail(theUser.getEMail())
                .password(theUser.getPassword())
                .build();
    }
}

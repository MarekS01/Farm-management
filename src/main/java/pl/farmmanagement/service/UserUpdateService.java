package pl.farmmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.farmmanagement.model.dto.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityConfig;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    public UpdateUserDTO findByUserName(String userName) {
        User byUserName = userRepository.findByUserNameIgnoreCase(userName);
        return mapToUpdateUserDTO(byUserName);
    }

    public void updateUserGivenName(UpdateUserDTO user) {
        Optional<User> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setGivenName(user.getGivenName());
            userRepository.save(currentUser);
        });
    }

    public void updateUserSurname(UpdateUserDTO user) {
        Optional<User> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setSurname(user.getSurname());
            userRepository.save(currentUser);
        });
    }

    public void updateUserEmail(UpdateUserDTO user) {
        Optional<User> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setEMail(user.getEMail());
            userRepository.save(currentUser);
        });
    }

    public void updateUserPassword(UpdateUserDTO user) {
        Optional<User> theUser = userRepository.findById(user.getId());
        theUser.ifPresent(currentUser -> {
            currentUser.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(currentUser);
        });
    }

    private UpdateUserDTO mapToUpdateUserDTO(User theUser) {
        return UpdateUserDTO.builder()
                .id(theUser.getId())
                .givenName(theUser.getGivenName())
                .surname(theUser.getSurname())
                .eMail(theUser.getEMail())
                .password(theUser.getPassword())
                .build();
    }
}

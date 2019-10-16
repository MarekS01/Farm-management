package pl.farmmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.farmmanagement.helper.UpdatePasswordValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@UpdatePasswordValid
public class UpdateUserDTO {

    private long id;

    @NotNull
    @Size(min = 2)
    private String givenName;

    @NotNull
    @Size(min = 2)
    private String surname;

    @NotNull
    @Email
    private String eMail;

    private String oldPassword;

    private String password;

    private String rePassword;
}

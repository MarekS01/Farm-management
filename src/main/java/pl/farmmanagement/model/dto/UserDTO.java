package pl.farmmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.farmmanagement.helper.PasswordValid;
import pl.farmmanagement.helper.UniqueUserName;
import pl.farmmanagement.model.UserRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordValid
public class UserDTO {

  private Long id;

  @NotNull(message = "Required")
  @Size(min = 3, message = "Minimum 3 characters")
  @UniqueUserName
  private String userName;

  @NotNull(message = "Required")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
      message = "Minimum eight characters, at least one letter and one number")
  private String password;

  @NotNull
  private String rePassword;

  @NotNull(message = "Required")
  @Email(message = "Invalid email")
  private String eMail;

  @NotNull(message = "Required")
  @Size(min = 2, message = "Minimum 2 characters")
  private String givenName;

  @NotNull(message = "Required")
  @Size(min = 2, message = "Minimum 2 characters")
  private String surname;

  private Set<UserRole> roles;
}

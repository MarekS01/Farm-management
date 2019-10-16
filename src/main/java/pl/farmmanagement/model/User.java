package pl.farmmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "initialValue",initialValue = 1000)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "initialValue")
  private Long id;

  private String userName;
  private String password;
  private String eMail;
  private String givenName;
  private String surname;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
          cascade = CascadeType.ALL)
  private List<Field> userFields;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<UserRole> roles;

}

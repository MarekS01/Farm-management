package pl.farmmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String role;

    @Override
    public String toString() {
        return role;
    }

    public int hashCode() {
        return Objects.hash(this.role);
    }

    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (o == null || !(o instanceof UserRole) ) { return false; }
        return Objects.equals(this.role, ((UserRole) o).role);
    }
}

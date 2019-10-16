package pl.farmmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double area;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fieldEntity",
    cascade = CascadeType.ALL)
    private List<FieldOperation> operationsList;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

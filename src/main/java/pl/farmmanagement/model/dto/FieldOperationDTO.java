package pl.farmmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.farmmanagement.model.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldOperationDTO {

  private Long id;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull(message = "Required")
  private LocalDate operationDate;

  @NotNull(message = "Required")
  @Size(min = 1, message = "Required")
  private String task;

  private boolean isDone;
  private Field fieldEntity;
}

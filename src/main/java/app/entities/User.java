package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {
    @Id
    private Long userId;
    private String username;
    private String email;
    private String password;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}

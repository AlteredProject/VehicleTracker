package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MileageEntry {
    @Id
    private Integer entryId;
    private LocalDate date;
    private float mileage;

    @ManyToOne
    @ToString.Exclude
    private Vehicle vehicle;
}

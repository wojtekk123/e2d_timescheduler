package pl.codeconscept.e2d.timescheduler.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table (name = "time_scheduler")
public class RideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "student_id")
    private Long studentId;

    @NonNull
    @Column(name = "instructor_id")
    private Long instructorId;

    @NonNull
    @Column(name = "car_id")
    private Long carId;

    @NonNull
    @Column(name = "ride_date_form")
    private Date rideDateFrom;

    @NonNull
    @Column(name = "ride_date_to")
    private Date rideDateTo;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type")
    private ReservationType Type;

}

package pl.codeconscept.e2d.timescheduler.database.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "history")
public class HistoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "student_name")
    private String studentName;

    @NonNull
    @Column(name = "instructor_name")
    private String instructorName;

    @NonNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_scheduler")
    private RideEntity rideEntity;

    @NotNull
    @Column (name = "action")
    private String action;

}

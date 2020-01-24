package pl.codeconscept.e2d.timescheduler.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "workday")
public class WorkdayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "start_working")
    private Date startWorking;

    @Column(name = "end_working")
    private  Date endWorking;

}

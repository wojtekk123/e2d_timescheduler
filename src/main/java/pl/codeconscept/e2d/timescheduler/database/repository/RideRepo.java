package pl.codeconscept.e2d.timescheduler.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;

@Repository
public interface RideRepo extends JpaRepository<RideEntity,Long> {

    RideEntity findByStudentId(Long id);
    RideEntity findByInstructorId(Long id);

}

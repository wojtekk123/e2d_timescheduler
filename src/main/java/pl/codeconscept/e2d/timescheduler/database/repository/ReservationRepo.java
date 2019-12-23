package pl.codeconscept.e2d.timescheduler.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {
     ReservationEntity findByStudentId(Long id);
}

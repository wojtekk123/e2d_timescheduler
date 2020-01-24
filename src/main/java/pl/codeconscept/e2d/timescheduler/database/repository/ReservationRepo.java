package pl.codeconscept.e2d.timescheduler.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {
     List<ReservationEntity> findByStudentId(Long id);
}

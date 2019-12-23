package pl.codeconscept.e2d.timescheduler.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeconscept.e2d.timescheduler.database.entity.HistoryEntity;

@Repository
public interface HistoryRepo extends JpaRepository<HistoryEntity,Long> {
}

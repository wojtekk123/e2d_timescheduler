package pl.codeconscept.e2d.timescheduler.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;

import java.util.Date;
import java.util.List;


@Repository
public interface WorkdayRepo extends JpaRepository<WorkdayEntity,Long> {

    List<WorkdayEntity> findByStartWorkingLessThanEqualAndEndWorkingGreaterThanEqual(Date startWorking, Date endWorking);


}

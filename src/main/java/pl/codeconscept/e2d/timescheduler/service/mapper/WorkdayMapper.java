package pl.codeconscept.e2d.timescheduler.service.mapper;

import freemarker.template.utility.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import pl.codeconcept.e2d.e2dmasterdata.model.Workday;
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;

import java.util.Date;

public class WorkdayMapper {


    public static WorkdayEntity mapToEntity(Workday workday, Long instructorId) {

        WorkdayEntity workdayEntity = new WorkdayEntity();
        workdayEntity.setInstructorId(instructorId);
        workdayEntity.setStartWorking(workday.getStartWork());
        workdayEntity.setEndWorking(workday.getEndWork());
        return workdayEntity;
    }


    public static Workday mapToModel(WorkdayEntity workdayEntity) {

        Workday workday = new Workday();
        workday.setId(workdayEntity.getId());
        workday.setInstructorId(workdayEntity.getInstructorId());
        workday.setStartWork(workdayEntity.getStartWorking());
        workday.setEndWork(workdayEntity.getEndWorking());
        return workday;
    }
}

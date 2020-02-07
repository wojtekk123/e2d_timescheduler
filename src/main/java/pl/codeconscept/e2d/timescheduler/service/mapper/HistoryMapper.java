package pl.codeconscept.e2d.timescheduler.service.mapper;

import pl.codeconscept.e2d.timescheduler.database.entity.HistoryEntity;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;

public class HistoryMapper {

    public static HistoryEntity mapToHistoryEntity(RideEntity rideEntity, String instructorName, String studentName, String type) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setRideEntity(rideEntity);
        historyEntity.setInstructorName(instructorName);
        historyEntity.setStudentName(studentName);
        historyEntity.setAction(type);
        return historyEntity;
    }
}

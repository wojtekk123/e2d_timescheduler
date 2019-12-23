package pl.codeconscept.e2d.timescheduler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import pl.codeconcept.e2d.e2dmasterdata.model.Ride;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.service.mapper.RideMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@RunWith(MockitoJUnitRunner.class)
public class RideMapperTest {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
    private final RideEntity rideEntity = new RideEntity(1L, 1L, 1L, sdf.parse("2019-10-23 12:00:00"),sdf.parse("2019-10-23 13:00:00"), ScheduleType.DONE);

    @Test
    public void whenEqualsCorrect () throws ParseException {
        RideEntity rideEntity = RideMapper.mapToEntity(getRide(),1L,ScheduleType.DONE);
        Assert.assertEquals(rideEntity,this.rideEntity);
    }

    private Ride getRide () throws ParseException {
        Ride ride = new Ride();
        ride.setStudentId(1L);
        ride.setInstructorId(1L);
        ride.setCarId(1L);
        ride.setRideDateTo( sdf.parse("2019-10-23 13:00:00"));
        ride.setRideDataFrom( sdf.parse("2019-10-23 12:00:00"));
        ride.setTypeReservation("DONE");
        return ride;
    }

    public RideMapperTest() throws ParseException {
    }
}

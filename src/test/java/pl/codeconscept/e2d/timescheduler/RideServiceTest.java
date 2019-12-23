package pl.codeconscept.e2d.timescheduler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.codeconcept.e2d.e2dmasterdata.model.Ride;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.database.repository.RideRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.ConflictDateAbstract;
import pl.codeconscept.e2d.timescheduler.service.RideService;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.template.TemplateRestQueries;

import java.rmi.ConnectIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class RideServiceTest extends ConflictDateAbstract {


    @Mock
    TemplateRestQueries templateRestQueries;
    @Mock
    RideRepo rideRepo;
    @Mock
    JwtAuthFilter jwtAuthFilter;
    @Mock
    PrivilegeService privilegeService;

    @InjectMocks
    RideService rideService;

    private Long id =1L;
    private  String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcmVrIiwicm9sZSI6IlJPTEVfSU5TVFJVQ1RPUiIsImlkIjo2LCJpYXQiOjE1NzY4MzE3NjIsImV4cCI6MTU3Njg0Mzc2Mn0.NZtEjNAIM5yApNk-en3WkwGvtLt9riDCPLa8EwuW_xQ3EE4UkDtjtK2DsLSZ-VXaY7f-f_qeQpyGc1NcTpsAxg";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

    @Before
    public void init() throws ParseException, ConnectIOException {
        MockitoAnnotations.initMocks(RideService.class);
        BDDMockito.given(rideRepo.findAll()).willReturn(mockPrepareGetRides());
        BDDMockito.given(rideRepo.findById(id)).willReturn(mockPrepareGetId());
        BDDMockito.given(privilegeService.getRole()).willReturn("INSTRUCTOR");
        BDDMockito.given(privilegeService.getAuthId()).willReturn(prepareMockInstructorId());
        BDDMockito.given(jwtAuthFilter.getToken()).willReturn(token);
        BDDMockito.given(templateRestQueries.getInstructorId(token,1L)).willReturn(mockGetUser());
    }


    @Test
    public void shouldByNotNull() throws ParseException {
        List<RideEntity> rideEntities = idConflict(sdf.parse("2019-10-23 12:00:00"), sdf.parse("2019-10-23 13:00:00"));
        Assert.assertNotNull(rideEntities);
    }

    @Test
    public void shouldByNull() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        List<RideEntity> rideEntities = idConflict(sdf.parse("2019-10-23 15:00:00"), sdf.parse("2019-10-23 16:00:00"));
        Assert.assertNull(rideEntities);
    }

    @Test  (expected = E2DMissingException.class)
    public void shouldThrowExceptionIfEquals() throws ParseException {
        rideService.save(getRide());
    }

    @Test //  (expected = E2DMissingException.class)
    public void shouldBeEqual()  {
        ResponseEntity<Void> delete = rideService.delete(id);
        Assert.assertEquals(delete.getStatusCode().value(), HttpStatus.valueOf(200).value());
    }



    private Optional<RideEntity> mockPrepareGetId() throws ParseException {
        return Optional.of(getRideEntities());
    }

    private List<RideEntity> mockPrepareGetRides() throws ParseException {
        List<RideEntity> rides = new ArrayList<>();
        rides.add(getRideEntities());
        return rides;
    }

    private Long prepareMockInstructorId () {
        return 1L;
    }

    private RideEntity getRideEntities() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        RideEntity rideEntity = new RideEntity();
        rideEntity.setId(1L);
        rideEntity.setInstructorId(1L);
        rideEntity.setStudentId(1L);
        rideEntity.setRideDateFrom(sdf.parse("2019-10-23 11:00:00"));
        rideEntity.setRideDateTo(sdf.parse("2019-10-23 12:55:00"));
        rideEntity.setType(ScheduleType.CANCELED);
        return rideEntity;
    }

    private Ride getRide() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setInstructorId(1L);
        ride.setStudentId(1L);
        ride.setCarId(1L);
        ride.setRideDataFrom(sdf.parse("2019-10-23 11:00:00"));
        ride.setRideDateTo(sdf.parse("2019-10-23 12:55:00"));
        ride.setTypeReservation(ScheduleType.CANCELED.toString());
        return ride;
    }

    private UserId mockGetUser(){
        UserId userId = new UserId();
        userId.setUserName("Wojtek");
        userId.setId(1L);
        return userId;
    }

    @Override
    public List<RideEntity> idConflict(Date dateFrom, Date dateTo) {
        List<RideEntity> allReservation = rideRepo.findAll();
        List<RideEntity> collect = allReservation.stream().filter(e -> isWithinRange(e.getRideDateFrom(), e.getRideDateTo(), dateFrom, dateTo)).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return null;
        } else return collect;
    }

}
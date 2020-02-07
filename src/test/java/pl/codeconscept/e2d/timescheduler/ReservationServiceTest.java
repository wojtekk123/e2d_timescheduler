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
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.repository.ReservationRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.ConflictDateAbstract;
import pl.codeconscept.e2d.timescheduler.service.ReservationService;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.mapper.ReservationMapper;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.queries.TemplateRestQueries;

import java.rmi.ConnectIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest extends ConflictDateAbstract {


    @Mock
    TemplateRestQueries templateRestQueries;

    @Mock
    ReservationRepo reservationRepo;

    @Mock
    JwtAuthFilter jwtAuthFilter;

    @Mock
    PrivilegeService privilegeService;

    @InjectMocks
    ReservationService reservationService;

    private Long id =1L;
    private String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcmVrIiwicm9sZSI6IlJPTEVfSU5TVFJVQ1RPUiIsImlkIjo2LCJpYXQiOjE1NzY4MzE3NjIsImV4cCI6MTU3Njg0Mzc2Mn0.NZtEjNAIM5yApNk-en3WkwGvtLt9riDCPLa8EwuW_xQ3EE4UkDtjtK2DsLSZ-VXaY7f-f_qeQpyGc1NcTpsAxg";

    @Before
    public void init() throws ParseException, ConnectIOException {
        MockitoAnnotations.initMocks(ReservationEntity.class);
        BDDMockito.given(reservationRepo.findAll()).willReturn(mockPrepareGetRides());
        BDDMockito.given(reservationRepo.findById(id)).willReturn(mockPrepareGetId());
        BDDMockito.given(privilegeService.getRole()).willReturn("INSTRUCTOR");
        BDDMockito.given(privilegeService.getAuthId()).willReturn(1L);
        BDDMockito.given(jwtAuthFilter.getToken()).willReturn(token);
        BDDMockito.given(templateRestQueries.getStudentByAuthId(token,1L)).willReturn(mockGetUser());
    }

    @Test
    public void shouldByNotNull() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        List<ReservationEntity> reservationEntities = idConflict(sdf.parse("2019-10-23 12:00:00"), sdf.parse("2019-10-23 13:00:00"));
        Assert.assertNotNull(reservationEntities);
    }

    @Test
    public void shouldByNull() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        List<ReservationEntity> reservationEntities = idConflict(sdf.parse("2019-10-23 15:00:00"), sdf.parse("2019-10-23 16:00:00"));
        Assert.assertNull(reservationEntities);
    }

//    @Test //  (expected = E2DMissingException.class)
//    public void shouldBeEqual()  {
//        ResponseEntity<Void> delete = reservationService.delete(id);
//        Assert.assertEquals(delete.getStatusCode().value(), HttpStatus.valueOf(200).value());
//    }
    @Test  (expected = E2DMissingException.class)
    public void shouldThrowExceptionIfEquals() throws ParseException {
        reservationService.save(ReservationMapper.mapToModel(getReservationEntities()));
    }

    private Optional<ReservationEntity> mockPrepareGetId() throws ParseException {
        return Optional.of(getReservationEntities());
    }

    private List<ReservationEntity> mockPrepareGetRides() throws ParseException {
        List<ReservationEntity> reservationEntities = new ArrayList<>();
        reservationEntities.add(getReservationEntities());
        return reservationEntities;
    }

    private ReservationEntity getReservationEntities() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId(1L);
        reservationEntity.setInstructorId(1L);
        reservationEntity.setStudentId(1L);
        reservationEntity.setRideDateFrom(sdf.parse("2019-10-23 11:00:00"));
        reservationEntity.setRideDateTo(sdf.parse("2019-10-23 12:55:00"));
        reservationEntity.setType(ReservationType.OPEN);
        return reservationEntity;
    }

    @Override
    public List<ReservationEntity> idConflict(Date dataFrom, Date dateTo) {
        List<ReservationEntity> allReservation = reservationRepo.findAll();
        List<ReservationEntity> collect = allReservation.stream().filter(e -> isWithinRange(e.getRideDateFrom(), e.getRideDateTo(), dataFrom, dateTo)).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return null;
        } else return collect;
    }

    private UserId mockGetUser(){
        UserId userId = new UserId();
        userId.setUserName("Wojtek");
        userId.setId(1L);
        return userId;
    }

}

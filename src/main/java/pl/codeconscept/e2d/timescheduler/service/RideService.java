package pl.codeconscept.e2d.timescheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.Ride;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.database.repository.RideRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DIllegalArgument;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.mapper.HistoryMapper;
import pl.codeconscept.e2d.timescheduler.service.mapper.ReservationMapper;
import pl.codeconscept.e2d.timescheduler.service.mapper.RideMapper;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.template.TemplateRestQueries;

import java.rmi.ConnectIOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RideService extends ConflictDateAbstract {

    private final RideRepo rideRepo;
    private final PrivilegeService privilegeService;
    private final TemplateRestQueries templateRestQueries;
    private final JwtAuthFilter jwtAuthFilter;
    private final ReservationService reservationService;
    private final HistoryService historyService;


    public ResponseEntity<Ride> save(Ride ride) {
        String token = jwtAuthFilter.getToken();
        Long authId = privilegeService.getAuthId();
        String role = privilegeService.getRole();

        try {

            if (!role.equals("ROLE_ADMIN")) {
                List<RideEntity> rideEntities = idConflict(ride.getRideDataFrom(), ride.getRideDateTo());
                Long instructorId = templateRestQueries.getInstructorId(token, authId).getId();

                if (rideEntities != null) {
                    rideEntities.forEach(e -> {

                        if (e.getInstructorId().equals(instructorId)) {
                            throw new IllegalArgumentException();
                        }
                    });
                }
                return getRideResponseEntity(ride, instructorId);

            }
            return getRideResponseEntity(ride, ride.getInstructorId());

        } catch (ConnectIOException e) {
            throw new E2DIllegalArgument("problem with connection");
        } catch (DataAccessException e) {
            throw new E2DIllegalArgument("student already has ride : " + privilegeService.getAuthId());
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("instructor already has a ride on this time : ");
        } catch (NullPointerException e) {
            throw new E2DMissingException("Student id :" + authId);
        }
    }

    public ResponseEntity<Void> delete(Long id) {
        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        try {

            if (!role.equals("ROLE_ADMIN")) {
                RideEntity rideEntity = rideRepo.findById(id).orElseThrow(IllegalArgumentException::new);
                Long instructorId = templateRestQueries.getInstructorId(token, authId).getId();

                if (!rideEntity.getInstructorId().equals(instructorId)) {
                    throw new RuntimeException();
                }
            }
            rideRepo.deleteById(id);
            return ResponseEntity.ok().build();

        } catch (ConnectIOException e) {
            throw new E2DIllegalArgument("connection problem ");
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("you can't delete user: " + id);
        } catch (RuntimeException e) {
            throw new E2DMissingException("delete id: " + id);
        }
    }

    public ResponseEntity<Ride> update(Long id, Ride ride) {

        try {
            RideEntity rideToChange = rideRepo.findById(id).orElseThrow(IllegalAccessError::new);
            RideMapper.mapToExistingEntity(rideToChange, ride);
            rideRepo.save(rideToChange);
            return new ResponseEntity<>(RideMapper.mapToModel(rideToChange), HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new E2DMissingException("update id: " + id);
        }
    }

    public ResponseEntity<Void> done(Long id) {
        return getVoidResponseEntity(id, ScheduleType.IN_PROGRESS, ScheduleType.DONE);
    }

    public ResponseEntity<Void> cancel(Long id) {
        return getVoidResponseEntity(id, ScheduleType.PLANNED, ScheduleType.CANCELED);
    }

    public ResponseEntity<Void> start(Long id) {
        return getVoidResponseEntity(id, ScheduleType.PLANNED, ScheduleType.IN_PROGRESS);
    }


    @Override
    protected List<RideEntity> idConflict(Date dateFrom, Date dateTo) {
        List<RideEntity> allReservation = rideRepo.findAll();
        List<RideEntity> collect = allReservation.stream().filter(e -> isWithinRange(e.getRideDateFrom(), e.getRideDateTo(), dateFrom, dateTo)).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return null;
        } else return collect;
    }

    private ResponseEntity<Ride> getRideResponseEntity(Ride ride, Long instructorId) {
        List<ReservationEntity> reservationEntities = reservationService.idConflict(ride.getRideDataFrom(), ride.getRideDateTo());

        if (!(reservationEntities == null)) {
            reservationEntities.stream().filter(e -> e.getType().equals(ReservationType.OPEN)).forEach(e -> reservationService.decline(e.getId()));
        }

        RideEntity timesRideEntity = rideRepo.save(RideMapper.mapToEntity(ride, instructorId, ScheduleType.PLANNED));
        return new ResponseEntity<>(RideMapper.mapToModel(timesRideEntity), HttpStatus.OK);
    }

    private ResponseEntity<Void> getVoidResponseEntity(Long id, ScheduleType checkType, ScheduleType actionType) {
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        try {
            RideEntity rideEntity = rideRepo.findById(id).orElseThrow(IllegalArgumentException::new);

            if (!rideEntity.getType().equals(checkType))
                throw new E2DIllegalArgument("set: " + actionType + " status");

            ReservationMapper.mapToRideType(rideEntity, actionType);
            rideRepo.save(rideEntity);
            UserId instructorId = templateRestQueries.getInstructorId(token, authId);
            UserId studentId = templateRestQueries.getStudent(token, rideEntity.getStudentId());
            historyService.save(HistoryMapper.mapToHistoryEntity(rideEntity, instructorId.getUserName(), studentId.getUserName(), actionType));

        } catch (ConnectIOException e) {
            throw new E2DIllegalArgument("Problem with connection");
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("Wrong id: " + id);
        } catch (NullPointerException e) {
            throw new E2DIllegalArgument("Problem with fetching date");
        }
        return ResponseEntity.ok().build();
    }
}

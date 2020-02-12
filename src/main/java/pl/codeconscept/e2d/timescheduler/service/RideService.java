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
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.database.repository.RideRepo;
import pl.codeconscept.e2d.timescheduler.database.repository.WorkdayRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DIllegalArgument;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.mapper.HistoryMapper;
import pl.codeconscept.e2d.timescheduler.service.mapper.ReservationMapper;
import pl.codeconscept.e2d.timescheduler.service.mapper.RideMapper;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.queries.TemplateRestQueries;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final WorkdayRepo workdayRepo;

    public ResponseEntity<Ride> save(Ride ride) {
        String token = jwtAuthFilter.getToken();
        Long authId = privilegeService.getAuthId();
        String role = privilegeService.getRole();

        try {

            List<WorkdayEntity> check = workdayRepo.findByStartWorkingLessThanEqualAndEndWorkingGreaterThanEqual(ride.getRideDataFrom(), ride.getRideDateTo());

            if (!check.isEmpty() || (ride.getRideDateTo().compareTo(ride.getRideDataFrom())) < 0) {
                throw new NoSuchElementException();
            }


            if (!role.equals("ROLE_ADMIN")) {
                List<RideEntity> rideEntities = idConflict(ride.getRideDataFrom(), ride.getRideDateTo());
                Long instructorId = templateRestQueries.getInstructorByAuthId(token, authId).getId();

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

        } catch (DataAccessException e) {
            throw new E2DIllegalArgument("student already has ride : " + privilegeService.getAuthId());
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("instructor already has a ride on this time : ");
        } catch (NullPointerException e) {
            throw new E2DMissingException("Student id :" + authId);
        } catch (NoSuchElementException e) {
            throw new E2DIllegalArgument("instructor does not working then or wrong date ");
        }
    }

    public ResponseEntity<Void> delete(Long id) {
        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        try {

            if (!role.equals("ROLE_ADMIN")) {
                RideEntity rideEntity = rideRepo.findById(id).orElseThrow(IllegalArgumentException::new);
                Long instructorId = templateRestQueries.getInstructorByAuthId(token, authId).getId();

                if (!rideEntity.getInstructorId().equals(instructorId)) {
                    throw new RuntimeException();
                }
            }
            rideRepo.deleteById(id);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("you can't delete user: " + id);
        } catch (RuntimeException e) {
            throw new E2DMissingException("delete id: " + id);
        }
    }

    public ResponseEntity<Ride> get(Long id) {
        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        RideEntity rideEntity = new RideEntity();

        try {
            if (!role.equals("ROLE_ADMIN")) {
                rideEntity = rideRepo.findById(id).orElseThrow(IllegalArgumentException::new);
                Long instructorId = templateRestQueries.getInstructorByAuthId(token, authId).getId();

                if (!rideEntity.getInstructorId().equals(instructorId)) {
                    throw new RuntimeException();
                }
            }

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("you can't get ride: " + id);
        }
        return new ResponseEntity<>(RideMapper.mapToModel(rideEntity), HttpStatus.OK);
    }

    public ResponseEntity<Ride> update(Long id, Ride ride) {

        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();


        try {
            List<RideEntity> rideEntities = idConflict(ride.getRideDataFrom(), ride.getRideDateTo());
            RideEntity rideToChange = rideRepo.findById(id).orElseThrow(IllegalAccessError::new);


            if ((rideEntities != null)||!(rideToChange.getType().toString().equals("PLANNED"))) {
                throw new IllegalArgumentException(rideToChange.getType().toString());
            }

            if (!role.equals("ROLE_ADMIN")) {
                UserId instructorByAuthId = templateRestQueries.getInstructorByAuthId(token, authId);

                if (!(role.equals("ROLE_INSTRUCTOR") || instructorByAuthId.getId().equals(rideToChange.getInstructorId()))) {
                    throw new NoSuchElementException();

                }
            }

            RideMapper.mapToExistingEntity(rideToChange, ride);
            rideRepo.save(rideToChange);

//            UserId instructorById = templateRestQueries.getInstructorById(token, rideToChange.getInstructorId());
//            UserId studentById = templateRestQueries.getStudentById(token, rideToChange.getId());
//            historyService.save(HistoryMapper.mapToHistoryEntity(rideToChange,instructorById.getUserName(),studentById.getUserName(),"UPDATE"));

            return new ResponseEntity<>(RideMapper.mapToModel(rideToChange), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("ride isn't PLANNED or date are duplicated ");
        } catch (NoSuchElementException e) {
            throw new E2DIllegalArgument("no access ");
        } catch (RuntimeException e) {
            throw new E2DMissingException("update id: " + id);
        } catch (Exception e){
            e.printStackTrace();
            throw new E2DIllegalArgument("something wrong ");
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
        List<RideEntity> allRide = rideRepo.findAll();
        List<RideEntity> collect = allRide.stream().filter(e -> isWithinRange(e.getRideDateFrom(), e.getRideDateTo(), dateFrom, dateTo)).collect(Collectors.toList());

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
            UserId instructorId = templateRestQueries.getInstructorByAuthId(token, authId);
            UserId studentId = templateRestQueries.getStudentById(token, rideEntity.getStudentId());
            historyService.save(HistoryMapper.mapToHistoryEntity(rideEntity, instructorId.getUserName(), studentId.getUserName(), actionType.toString()));

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("Wrong id: " + id);
        } catch (NullPointerException e) {
            throw new E2DIllegalArgument("Problem with fetching date");
        }
        return ResponseEntity.ok().build();
    }
}

package pl.codeconscept.e2d.timescheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.Reservation;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.database.repository.ReservationRepo;
import pl.codeconscept.e2d.timescheduler.database.repository.RideRepo;
import pl.codeconscept.e2d.timescheduler.database.repository.WorkdayRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DIllegalArgument;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.event.EventService;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
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
public class ReservationService extends ConflictDateAbstract {

    private final PrivilegeService privilegeService;
    private final ReservationRepo reservationRepo;
    private final TemplateRestQueries templateRestQueries;
    private final JwtAuthFilter jwtAuthFilter;
    private final RideRepo rideRepo;
    private final EventService eventService;
    private final WorkdayRepo workdayRepo;

    public ResponseEntity<Reservation> save(Reservation reservation) {

        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();
        String role = privilegeService.getRole();

        try {
            List<WorkdayEntity> check = workdayRepo.findByStartWorkingLessThanEqualAndEndWorkingGreaterThanEqual(reservation.getRideDataFrom(), reservation.getRideDateTo()); //check if the instructor is working

            if (!check.isEmpty() || (reservation.getRideDateTo().compareTo(reservation.getRideDataFrom())) < 0) {
                throw new NoSuchElementException();

            }

            if (!role.equals("ROLE_ADMIN")) {
                List<ReservationEntity> reservationEntities = idConflict(reservation.getRideDataFrom(), reservation.getRideDateTo()); //check if the rides collide

                if (reservationEntities != null) {
                    Long instructorId = reservation.getInstructorId();
                    reservationEntities.forEach(e -> {

                        if (e.getInstructorId().equals(instructorId)) {
                            throw new IllegalArgumentException();
                        }
                    });
                }

                return getReservationResponseEntity(reservation, templateRestQueries.getStudentByAuthId(token, authId).getId());
            }
            return getReservationResponseEntity(reservation, reservation.getStudentId());

        } catch (DataAccessException e) {
            throw new E2DIllegalArgument("student reservation already exist: ");
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("instructor already has a ride on this time  : ");
        } catch (NoSuchElementException e) {
            throw new E2DIllegalArgument("instructor does not working then or wrong date ");
        } catch (NullPointerException e) {
            throw new E2DMissingException("missing some data");
        } catch (Exception e) {
            e.printStackTrace();
            throw new E2DIllegalArgument("something wrong : ");
        }

    }

    public ResponseEntity<Void> delete(Long id) {

        Long authId = privilegeService.getAuthId();
        String role = privilegeService.getRole();
        String token = jwtAuthFilter.getToken();

        try {
            ReservationEntity reservationEntity = reservationRepo.findById(id).orElseThrow(NullPointerException::new);

            if (!role.equals("ROLE_ADMIN")) {

                UserId instructorByAuthId = templateRestQueries.getInstructorByAuthId(token, authId);

                if (!(reservationEntity.getType().equals(ReservationType.OPEN) && instructorByAuthId.getId().equals(reservationEntity.getInstructorId()))) {
                    throw new IllegalArgumentException();
                }
            }
            reservationRepo.delete(reservationEntity);
            return ResponseEntity.ok().build();
        } catch (NullPointerException e) {
            throw new E2DMissingException("delete id: " + id);
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("delete id: " + id);
        }
    }

    public ResponseEntity<List<Reservation>> getAll() {

        Long authId = privilegeService.getAuthId();
        String role = privilegeService.getRole();

        List<Reservation> collect;
        List<ReservationEntity> all = reservationRepo.findAll();

        if (role.equals("ROLE_STUDENT")) {
            List<ReservationEntity> collectedEntity = all.stream().filter(e -> e.getStudentId().equals(authId)).collect(Collectors.toList());
            collect = collectedEntity.stream().map(ReservationMapper::mapToModel).collect(Collectors.toList());

        } else {
            collect = all.stream().map(ReservationMapper::mapToModel).collect(Collectors.toList());

        }

        return new ResponseEntity<>(collect, HttpStatus.OK);
    }

    public ResponseEntity<Void> approve(Long id) {

        try {
            ReservationEntity reservationEntity = reservationRepo.findById(id).orElseThrow(IllegalArgumentException::new);

            if (!(reservationEntity.getType().equals(ReservationType.OPEN))) {
                throw new IllegalArgumentException();
            }

            ReservationMapper.mapToReservationType(reservationEntity, ReservationType.APPROVE);
            reservationRepo.save(reservationEntity);
            rideRepo.save(RideMapper.mapToEntity(RideMapper.mapToRide(reservationEntity), reservationEntity.getInstructorId(), ScheduleType.PLANNED));
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("Wrong id");
        }
    }

    public ResponseEntity<Void> decline(Long id) {

        try {
            ReservationEntity reservationEntity = reservationRepo.findById(id).orElseThrow(IllegalArgumentException::new);
            ReservationMapper.mapToReservationType(reservationEntity, ReservationType.DECLINE);
            reservationRepo.save(reservationEntity);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("Wrong id");
        }
    }

    @Override
    public List<ReservationEntity> idConflict(Date dataFrom, Date dateTo) {

        List<ReservationEntity> allReservation = reservationRepo.findAll();
        List<ReservationEntity> collect = allReservation.stream().filter(e -> isWithinRange(e.getRideDateFrom(), e.getRideDateTo(), dataFrom, dateTo)).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return null;
        }
        return collect;
    }

    private ResponseEntity<Reservation> getReservationResponseEntity(Reservation reservation, Long studentId) {
        ReservationEntity reservationEntity = reservationRepo.save(ReservationMapper.mapToEntity(reservation, studentId, ReservationType.OPEN));
        eventService.sendNotification(ReservationMapper.mapToModel(reservationEntity));
        return new ResponseEntity<>(ReservationMapper.mapToModel(reservationEntity), HttpStatus.OK);
    }

}


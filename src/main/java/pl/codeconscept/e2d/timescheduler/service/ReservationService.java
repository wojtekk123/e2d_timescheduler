package pl.codeconscept.e2d.timescheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.Reservation;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;
import pl.codeconscept.e2d.timescheduler.database.repository.ReservationRepo;
import pl.codeconscept.e2d.timescheduler.database.repository.RideRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DIllegalArgument;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
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
public class ReservationService extends ConflictDateAbstract {

    private final PrivilegeService privilegeService;
    private final ReservationRepo reservationRepo;
    private final TemplateRestQueries templateRestQueries;
    private final JwtAuthFilter jwtAuthFilter;
    private final RideRepo rideRepo;

    public ResponseEntity<Reservation> save(Reservation reservation) {

        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();
        String role = privilegeService.getRole();

        try {

            if (!role.equals("ROLE_ADMIN")) {
                List<ReservationEntity> reservationEntities = idConflict(reservation.getRideDataFrom(), reservation.getRideDateTo());

                if (reservationEntities != null) {
                    Long instructorId = reservation.getInstructorId();
                    reservationEntities.forEach(e -> {

                        if (e.getInstructorId().equals(instructorId)) {
                            throw new IllegalArgumentException();
                        }
                    });
                }
                return getReservationResponseEntity(reservation, templateRestQueries.getStudentId(token, authId).getId());
            }
            return getReservationResponseEntity(reservation, reservation.getStudentId());

        } catch (ConnectIOException e) {
            throw new E2DIllegalArgument("problem with connection");
        } catch (DataAccessException e) {
            throw new E2DIllegalArgument("student reservation already exist: ");
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("instructor already has a ride on this time : ");
        } catch (NullPointerException e) {
            throw new E2DMissingException("missing some data");
        } catch (Exception e) {
            throw new E2DIllegalArgument("something wrong : ");
        }

    }

    public ResponseEntity<Void> delete(Long id) {

        Long authId = privilegeService.getAuthId();
        String role = privilegeService.getRole();

        try {

            ReservationEntity reservationEntity = reservationRepo.findById(id).orElseThrow(NullPointerException::new);

            if (role.equals("ROLE_ADMIN")) {
                reservationRepo.delete(reservationEntity);
                return ResponseEntity.ok().build();

            } else if (reservationEntity.getType().equals(ReservationType.OPEN)) {
                ReservationEntity byStudentId = reservationRepo.findByStudentId(reservationEntity.getStudentId());
                UserId studentId = templateRestQueries.getStudentId(jwtAuthFilter.getToken(), authId);

                if (!role.equals("ROLE_STUDENT") || studentId.getId().equals(byStudentId.getStudentId())) {
                    reservationRepo.delete(reservationEntity);
                    return ResponseEntity.ok().build();
                }
            }
            throw new IllegalArgumentException();

        } catch (ConnectIOException e) {
            throw new E2DIllegalArgument("Connection Problem");
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
        } else return collect;
    }

    private ResponseEntity<Reservation> getReservationResponseEntity(Reservation reservation, Long studentId) throws ConnectIOException {
        ReservationEntity reservationEntity = reservationRepo.save(ReservationMapper.mapToEntity(reservation, studentId, ReservationType.OPEN));
        return new ResponseEntity<>(ReservationMapper.mapToModel(reservationEntity), HttpStatus.OK);
    }}

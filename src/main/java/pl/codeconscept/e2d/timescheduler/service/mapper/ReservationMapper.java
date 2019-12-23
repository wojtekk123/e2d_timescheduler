package pl.codeconscept.e2d.timescheduler.service.mapper;

import pl.codeconcept.e2d.e2dmasterdata.model.Reservation;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;
import pl.codeconscept.e2d.timescheduler.database.enums.ScheduleType;

public class ReservationMapper {

    public static ReservationEntity mapToEntity(Reservation reservation, Long id, ReservationType type) {

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setInstructorId(reservation.getInstructorId());
        reservationEntity.setStudentId(id);
        reservationEntity.setRideDateFrom(reservation.getRideDataFrom());
        reservationEntity.setRideDateTo(reservation.getRideDateTo());
        reservationEntity.setCarId(reservation.getCarId());
        reservationEntity.setType(type);
        return reservationEntity;
    }

    public static Reservation mapToModel(ReservationEntity reservationEntity) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationEntity.getId());
        reservation.setInstructorId(reservationEntity.getInstructorId());
        reservation.setStudentId(reservationEntity.getStudentId());
        reservation.setRideDataFrom(reservationEntity.getRideDateFrom());
        reservation.setRideDateTo(reservationEntity.getRideDateTo());
        reservation.setCarId(reservationEntity.getCarId());
        reservation.setTypeReservation(reservationEntity.getType().toString());
        return reservation;
    }

    public static void mapToReservationType(ReservationEntity reservationEntity, ReservationType type) {
        reservationEntity.setType(type);
    }

    public static void mapToRideType(RideEntity rideEntity, ScheduleType type) {
        rideEntity.setType(type);
    }
}

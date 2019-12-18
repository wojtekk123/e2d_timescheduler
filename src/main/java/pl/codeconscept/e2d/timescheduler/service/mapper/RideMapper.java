package pl.codeconscept.e2d.timescheduler.service.mapper;

import pl.codeconcept.e2d.e2dmasterdata.model.Ride;
import pl.codeconscept.e2d.timescheduler.database.entity.ReservationEntity;
import pl.codeconscept.e2d.timescheduler.database.entity.RideEntity;
import pl.codeconscept.e2d.timescheduler.database.enums.ReservationType;

public class RideMapper {


    public static RideEntity mapToEntity (Ride ride, long instructorId,ReservationType reservationType){

        RideEntity timesRideEntity = new RideEntity();
        timesRideEntity.setStudentId(ride.getStudentId());
        timesRideEntity.setInstructorId(instructorId);
        timesRideEntity.setCarId(ride.getCarId());
        timesRideEntity.setRideDateTo(ride.getRideDateTo());
        timesRideEntity.setRideDateFrom(ride.getRideDataFrom());
        timesRideEntity.setType(reservationType);
        return timesRideEntity;
    }


    public static Ride mapToModel (RideEntity rideEntity){
        Ride ride = new Ride();
        ride.setId(rideEntity.getId());
        ride.setInstructorId(rideEntity.getInstructorId());
        ride.setStudentId(rideEntity.getStudentId());
        ride.setCarId(rideEntity.getCarId());
        ride.setRideDataFrom(rideEntity.getRideDateFrom());
        ride.setRideDateTo(rideEntity.getRideDateTo());
        ride.setTypeReservation(rideEntity.getType().toString());
        return ride;
    }

    public static void mapToExistingEntity(RideEntity rideToChange, Ride ride) {

        rideToChange.setStudentId(ride.getStudentId());
        rideToChange.setCarId(ride.getCarId());
        rideToChange.setId(ride.getInstructorId());
        rideToChange.setRideDateFrom(ride.getRideDataFrom());
        rideToChange.setRideDateTo(ride.getRideDateTo());

    }

    public static Ride mapToRide(ReservationEntity reservationEntity) {

        Ride ride = new Ride();
        ride.setInstructorId(reservationEntity.getId());
        ride.setStudentId(reservationEntity.getId());
        ride.setCarId(reservationEntity.getCarId());
        ride.setRideDateTo(reservationEntity.getRideDateTo());
        ride.setRideDataFrom(reservationEntity.getRideDateFrom());
        ride.setTypeReservation(reservationEntity.getType().toString());
        return ride;
    }

   private static ReservationType getType(String type) {
        switch (type.toLowerCase()) {
            case "open":
                return ReservationType.OPEN;
            case "decline":
                return ReservationType.DECLINE;
            case "approve":
                return ReservationType.APPROVE;
            default:
                return null;
        }
    }
}

package pl.codeconscept.e2d.timescheduler.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.codeconcept.e2d.e2dmasterdata.api.ReservationApi;
import pl.codeconcept.e2d.e2dmasterdata.model.Reservation;
import pl.codeconscept.e2d.timescheduler.service.ReservationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {


    private final ReservationService reservationService;


    @Override
    public ResponseEntity<Void> approveReservation(Long id) {
        return reservationService.approve(id);
    }

    @Override
    public ResponseEntity<Reservation> createReservation(@Valid Reservation reservation) {
        return reservationService.save(reservation);
    }

    @Override
    public ResponseEntity<Void> declineReservation(Long id) {
        return reservationService.decline(id);
    }

    @Override
    public ResponseEntity<Void> deleteReservation(Long id) {
        return reservationService.delete(id);
    }

    @Override
    public ResponseEntity<List<Reservation>> getAllReservation() {
        return reservationService.getAll();
    }
}

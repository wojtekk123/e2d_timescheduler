package pl.codeconscept.e2d.timescheduler.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.codeconcept.e2d.e2dmasterdata.api.RideApi;
import pl.codeconcept.e2d.e2dmasterdata.model.Ride;
import pl.codeconscept.e2d.timescheduler.service.RideService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RideController implements RideApi {

    private final RideService rideService;

    @Override
    public ResponseEntity<Ride> createRide(@Valid Ride ride) {
        return rideService.save(ride);
    }

    @Override
    public ResponseEntity<Ride> getRide(@Valid Long id) { return rideService.get(id); }

    @Override
    public ResponseEntity<Void> deleteRide(Long id) {
        return rideService.delete(id);
    }

    @Override
    public ResponseEntity<Ride> updateRide(Long id, @Valid Ride ride) {
        return rideService.update(id, ride);
    }

    @Override
    public ResponseEntity<Void> doneRide(Long id) {
        return rideService.done(id);
    }

    @Override
    public ResponseEntity<Void> startRide(Long id) {
        return rideService.start(id);
    }

    @Override
    public ResponseEntity<Void> cancelRide(Long id) {
        return rideService.cancel(id);
    }

}

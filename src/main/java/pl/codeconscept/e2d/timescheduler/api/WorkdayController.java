package pl.codeconscept.e2d.timescheduler.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.codeconcept.e2d.e2dmasterdata.api.WorkdayApi;
import pl.codeconcept.e2d.e2dmasterdata.model.Workday;
import pl.codeconscept.e2d.timescheduler.service.WorkdayService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkdayController implements WorkdayApi {

    private final WorkdayService workdayService;


    @Override
    public ResponseEntity<Workday> addWorkday(@Valid Workday workday) {
        return workdayService.save(workday);
    }

    @Override
    public ResponseEntity<Void> deleteWorkday(Long id) {
        return workdayService.delete(id);
    }

    @Override
    public ResponseEntity<List<Workday>> getAllworkdays() {
        return workdayService.getAll();
    }
}

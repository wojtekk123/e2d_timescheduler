package pl.codeconscept.e2d.timescheduler.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.codeconcept.e2d.e2dmasterdata.api.HistoryApi;
import pl.codeconscept.e2d.timescheduler.database.entity.HistoryEntity;
import pl.codeconscept.e2d.timescheduler.service.HistoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HistoryController implements HistoryApi {

    private final HistoryService historyService;


    @Override
    public ResponseEntity<List<HistoryEntity>> getAllHistory() {
        return historyService.getAll();
    }
}

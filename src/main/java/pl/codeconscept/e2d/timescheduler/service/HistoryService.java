package pl.codeconscept.e2d.timescheduler.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.History;
import pl.codeconscept.e2d.timescheduler.database.entity.HistoryEntity;
import pl.codeconscept.e2d.timescheduler.database.repository.HistoryRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepo historyRepo;

    public void save (HistoryEntity historyEntity) {
        historyRepo.save(historyEntity);
    }

    public ResponseEntity<List<HistoryEntity>> getAll() {
        List<HistoryEntity> all = historyRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}

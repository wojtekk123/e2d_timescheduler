package pl.codeconscept.e2d.timescheduler.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.codeconscept.e2d.timescheduler.database.entity.HistoryEntity;
import pl.codeconscept.e2d.timescheduler.database.repository.HistoryRepo;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepo historyRepo;

    public void save (HistoryEntity historyEntity) {
        historyRepo.save(historyEntity);
    }
}

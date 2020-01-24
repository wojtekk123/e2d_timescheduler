package pl.codeconscept.e2d.timescheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconcept.e2d.e2dmasterdata.model.Workday;
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;
import pl.codeconscept.e2d.timescheduler.database.repository.WorkdayRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.WorkdayService;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.queries.TemplateRestQueries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class WorkdayServiceTest {


    @Mock
    WorkdayRepo workdayRepo;
    @Mock
    TemplateRestQueries templateRestQueries;
    @Mock
    PrivilegeService privilegeService;
    @Mock
    JwtAuthFilter jwtAuthFilter;

    @InjectMocks
    WorkdayService workdayService;

    private long id = 1L;
    private String token = "token";
    private Date startDate = new Date();
    private Date endDate = new Date();


    @Before
    public void init ()  {
//        MockitoAnnotations.initMocks(WorkdayService.class);
//        BDDMockito.given(workdayRepo.findByStartWorkingLessThanEqualAndEndWorkingGreaterThanEqual(startDate,endDate)).willReturn(mockPrepareGetList());
//        BDDMockito.given(templateRestQueries.getInstructorByAuthId(token,id)).willReturn(mockgetinstrucotrid());
//        BDDMockito.given(workdayRepo.findById(id)).willReturn(mockGetWorkday());
    }

    private Optional<WorkdayEntity> mockGetWorkday() {
        return Optional.of(getWorkdayEntity());
    }


    @Test (expected = E2DMissingException.class)
     public void shouldThrowException () {
        workdayService.save(new Workday());
    }

    @Test (expected = E2DMissingException.class)
    public void shouldTrowException () {
        workdayService.delete(id);
    }




    private WorkdayEntity getWorkdayEntity() {
        return new WorkdayEntity();
    }
}

package pl.codeconscept.e2d.timescheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconcept.e2d.e2dmasterdata.model.Workday;
import pl.codeconscept.e2d.timescheduler.database.entity.WorkdayEntity;
import pl.codeconscept.e2d.timescheduler.database.repository.WorkdayRepo;
import pl.codeconscept.e2d.timescheduler.exception.E2DIllegalArgument;
import pl.codeconscept.e2d.timescheduler.exception.E2DMissingException;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.mapper.WorkdayMapper;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.queries.TemplateRestQueries;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkdayService {


    private final WorkdayRepo workdayRepo;
    private final PrivilegeService privilegeService;
    private final TemplateRestQueries templateRestQueries;
    private final JwtAuthFilter jwtAuthFilter;

    public ResponseEntity<Workday> save(Workday workday) {

        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        try {
            List<WorkdayEntity> list = workdayRepo.findByStartWorkingLessThanEqualAndEndWorkingGreaterThanEqual(workday.getStartWork(), workday.getEndWork());

            UserId instructorById = null;
            UserId schoolByAuthI = null;
            UserId instructorByAuthId = null;

            switch (role) {
                case "ROLE_INSTRUCTOR":// necessary to get info about instructor id
                    instructorByAuthId = templateRestQueries.getInstructorByAuthId(token, authId);
                    break;
                case "ROLE_SCHOOL":// necessary to compare that registered  instructor has the same school as school User
                    schoolByAuthI = templateRestQueries.getSchoolByAuthI(token, authId);
                    instructorById = templateRestQueries.getInstructorById(token, workday.getInstructorId());
                    break;
            }

            if (!list.isEmpty()) {
                throw new NoSuchElementException();
            }

            if (!(role.equals("ROLE_ADMIN") || (role.equals("ROLE_SCHOOL") && (instructorById.getSchoolId().equals(schoolByAuthI.getSchoolId()))))) {

                if (!(role.equals("ROLE_INSTRUCTOR"))) {
                    throw new IllegalArgumentException();

                }
                return getWorkdayResponseEntity(workday, instructorByAuthId.getId()); // is invoked when user is instructor, "instructorId" is not required

            }
            return getWorkdayResponseEntity(workday, workday.getInstructorId()); //is invoked when user is admin or school, "instructorId is required

        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("for instructor id:" + workday.getInstructorId());
        } catch (NullPointerException e) {
            throw new E2DMissingException("missing some data");
        } catch (NoSuchElementException e) {
            throw new E2DIllegalArgument("the working hours are duplicated or wrong date");
        }
    }

    public ResponseEntity<List<Workday>> getAll() {

        List<Workday> collect = workdayRepo.findAll().stream().map(WorkdayMapper::mapToModel).collect(Collectors.toList());
        return new ResponseEntity<>(collect, HttpStatus.OK);
    }

    public ResponseEntity<Void> delete(Long id) {

        String role = privilegeService.getRole();
        Long authId = privilegeService.getAuthId();
        String token = jwtAuthFilter.getToken();

        try {

            if (!role.equals("ROLE_ADMIN")) {
                WorkdayEntity workdayEntity = workdayRepo.findById(id).orElseThrow(NullPointerException::new);
                UserId instructorByAuthId = templateRestQueries.getInstructorByAuthId(token, authId);

                if (!(role.equals("ROLE_INSTRUCTOR")) || !(instructorByAuthId.getId().equals(workdayEntity.getInstructorId()))) {
                    throw new IllegalArgumentException();

                }
            }

            workdayRepo.deleteById(id);
            return ResponseEntity.ok().build();

        } catch (NullPointerException e) {
            throw new E2DMissingException("delete id: " + id);
        } catch (IllegalArgumentException e) {
            throw new E2DIllegalArgument("delete id: " + id);
        }
    }


    private ResponseEntity<Workday> getWorkdayResponseEntity(Workday workday, Long instructorId) {
        Workday savedWorkday = WorkdayMapper.mapToModel(workdayRepo.save(WorkdayMapper.mapToEntity(workday, instructorId)));
        return new ResponseEntity<>(savedWorkday, HttpStatus.OK);
    }
}
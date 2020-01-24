package pl.codeconscept.e2d.timescheduler.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import pl.codeconcept.e2d.e2dmasterdata.model.InfoToPrintDto;
import pl.codeconcept.e2d.e2dmasterdata.model.MessageDto;
import pl.codeconcept.e2d.e2dmasterdata.model.Reservation;
import pl.codeconcept.e2d.e2dmasterdata.model.UserId;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;
import pl.codeconscept.e2d.timescheduler.service.privilege.PrivilegeService;
import pl.codeconscept.e2d.timescheduler.service.queries.TemplateRestQueries;

import java.rmi.ConnectIOException;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final String ORIGINAL_EXCHANGE = "timescheduler.exchange";
    private final static Logger logger = LoggerFactory.getLogger(EventService.class);


    private final JwtAuthFilter jwtAuthFilter;
    private final TemplateRestQueries templateRestQueries;
    private final EventProducers eventProducers;
    private final PrivilegeService privilegeService;

    public void sendNotification(Reservation reservation)   {

        String token = jwtAuthFilter.getToken();
        Long authId = privilegeService.getAuthId();
        UserId studentByAuthId = templateRestQueries.getStudentByAuthId(token, authId);
        UserId instructor = templateRestQueries.getInstructorById(token, reservation.getInstructorId());

        InfoToPrintDto userInfo = new InfoToPrintDto();
        userInfo.setStudentName(studentByAuthId.getUserName());
        userInfo.setInstructorName(instructor.getUserName());
        userInfo.setInstructorEmail(instructor.getEmail());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserInfo(userInfo);
        messageDto.setReservation(reservation);

        eventProducers.timeschedulerNotificationProducer().send(MessageBuilder.withPayload(messageDto).build());
        logger.info("message sent to queue");
    }
}
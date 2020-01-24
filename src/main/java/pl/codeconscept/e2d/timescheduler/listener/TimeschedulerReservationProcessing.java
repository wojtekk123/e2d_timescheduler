package pl.codeconscept.e2d.timescheduler.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.codeconcept.e2d.e2dmasterdata.model.MessageDto;
import pl.codeconscept.e2d.timescheduler.service.event.EventConsumers;
import pl.codeconscept.e2d.timescheduler.service.mail.EmailService;
import pl.codeconscept.e2d.timescheduler.service.mail.Mail;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TimeschedulerReservationProcessing {

    @Autowired
    private EmailService emailService;

    @StreamListener(target = EventConsumers.TIMESCHEDULER_NOTIFICATION_CONSUMER)
    public void listener (Message<MessageDto> message)  {
        log.info(message.getPayload().toString());
        try {
            send(message.getPayload());
        } catch (MessagingException e) {
            log.error("Sending problem",e);
        }catch (Exception e){
            log.error("other problem",e);
        }
    }

    private void send(MessageDto message) throws MessagingException {

        Map<String, Object> model = new HashMap<>();
        model.put("instructorName",message.getUserInfo().getInstructorName());
        model.put("time",new Date().toString());
        model.put("startData",message.getReservation().getRideDataFrom());
        model.put("endData",message.getReservation().getRideDateTo());
        model.put("carId",message.getReservation().getCarId());
        model.put("studentName",message.getUserInfo().getStudentName());

        Mail mail = new Mail();
        mail.setFrom("e2scloudsender@gmail.com");
        mail.setTo(message.getUserInfo().getInstructorEmail());
        mail.setSubject("Reservation email");
        mail.setContent(message.toString());
        mail.setModel(model);

        emailService.sendSimpleMessage(mail);
    }
}

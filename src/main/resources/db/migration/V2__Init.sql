
INSERT INTO reservation (instructor_id, student_id,car_id,ride_date_form,ride_date_to,reservation_type)
VALUES (2,1,2,'2019-12-17 8:00:00','2019-12-17 9:55:00','OPEN');
INSERT INTO reservation (instructor_id, student_id,car_id,ride_date_form,ride_date_to,reservation_type)
VALUES (2,2,2,'2019-12-17 10:00:00','2019-12-17 11:55:00','OPEN');
INSERT INTO reservation (instructor_id, student_id,car_id,ride_date_form,ride_date_to,reservation_type)
VALUES (1,3,2,'2019-12-17 10:00:00','2019-12-17 11:55:00','OPEN');

INSERT INTO time_scheduler (instructor_id,student_id,car_id,ride_date_form,ride_date_to,schedule_type)
VALUES (1,1,1,'2019-10-23 11:00:00','2019-10-23 12:55:00','PLANNED');
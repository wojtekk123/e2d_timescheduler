CREATE TABLE reservation
(
    id             SERIAL NOT NULL,
    instructor_id  BIGINT NOT NULL,
    student_id     BIGINT NOT NULL,
    car_id         BIGINT NOT NULL,
    ride_date_form TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ride_date_to   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reservation_type VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE time_scheduler
(
    id             SERIAL NOT NULL,
    instructor_id  BIGINT NOT NULL,
    student_id     BIGINT NOT NULL,
    car_id         BIGINT NOT NULL,
    ride_date_form TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ride_date_to   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reservation_type VARCHAR(20),
    PRIMARY KEY (id)
);
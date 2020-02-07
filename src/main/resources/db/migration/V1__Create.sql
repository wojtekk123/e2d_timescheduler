CREATE TABLE reservation
(
    id               SERIAL NOT NULL,
    instructor_id    BIGINT NOT NULL,
    student_id       BIGINT NOT NULL,
    car_id           BIGINT NOT NULL,
    ride_date_form   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ride_date_to     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reservation_type VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE ride
(
    id             SERIAL NOT NULL,
    instructor_id  BIGINT NOT NULL,
    student_id     BIGINT NOT NULL,
    car_id         BIGINT NOT NULL,
    ride_date_form TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ride_date_to   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    schedule_type  VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE history
(
    id              SERIAL NOT NULL,
    id_scheduler    BIGINT REFERENCES ride (id),
    instructor_name VARCHAR(20),
    student_name    VARCHAR(20),
    action          VARCHAR(20),
    PRIMARY KEY (id)

);

CREATE TABLE workday
(
    id            SERIAL NOT NULL,
    instructor_id BIGINT NOT NULL,
    start_working TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_working   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)

)
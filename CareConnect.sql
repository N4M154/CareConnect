

-- Create patients table
DROP TABLE patients CASCADE CONSTRAINTS;
CREATE TABLE patients (
    id VARCHAR(10) PRIMARY KEY,
    username VARCHAR2(50) UNIQUE,
    realname VARCHAR(100),
    password VARCHAR(50),
    age int,
    bloodgroup varchar(5)
);

-- Create doctors table

DROP TABLE doctors CASCADE CONSTRAINTS;
CREATE TABLE doctors (
    id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    realname VARCHAR(100),
    password_ VARCHAR(50),
    specialty VARCHAR(100),
    phone_number VARCHAR(20),
    day VARCHAR(10), -- Column for storing the day,
    max_patients int,
    start_time VARCHAR(10), -- Column for storing the start time
    end_time VARCHAR(10) -- Column for storing the end time
);

-- Create appointments table
DROP TABLE appointments CASCADE CONSTRAINTS;
CREATE TABLE appointments (
    id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10),
    doctor_id VARCHAR(10),
    patient_username VARCHAR(50),
    doctor_name VARCHAR(50),
    doctor_specialty VARCHAR(50),
    appointment_day varchar(20),
    appointment_date date,
    CONSTRAINT fk_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

drop table prescriptions cascade constraints;
CREATE TABLE prescriptions (
    prescription_id varchar(10) PRIMARY KEY,
    app_id varchar(10),
    patient_id VARCHAR2(50),
    patient_realname VARCHAR2(100),
    age NUMBER,
    blood_group VARCHAR2(10),
    doctor_id VARCHAR2(50),
    doctor_realname VARCHAR2(100),
    symptoms VARCHAR2(500),
    medicine varchar2(500),
    FOREIGN KEY (app_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Create appointments table
drop table bills cascade constraints;
CREATE TABLE bills (
    id VARCHAR(10) PRIMARY KEY,
    app_id varchar(10),
    patient_id VARCHAR(10),
    doctor_id VARCHAR(10),
    patient_username VARCHAR(50),
    doctor_name VARCHAR(50),
    doctor_specialty VARCHAR(50),
    appointment_day varchar(20),
    appointment_date date,
    amount decimal(10,2),
    CONSTRAINT fk_patient2 FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_doctor2 FOREIGN KEY (doctor_id) REFERENCES doctors(id),
CONSTRAINT fk_app FOREIGN KEY (app_id) REFERENCES appointments(id)
);


-- Create sequence for patients
DROP SEQUENCE patient_id_seq;
CREATE SEQUENCE patient_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create sequence for doctors
DROP SEQUENCE doctor_id_seq;
CREATE SEQUENCE doctor_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create sequence for appointments
DROP SEQUENCE appointment_id_seq;
CREATE SEQUENCE appointment_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create sequence for prescription ID
drop sequence prescription_id_seq;
CREATE SEQUENCE prescription_id_seq 
       START WITH 1 
       INCREMENT BY 1
       NOCACHE
       NOCYCLE;

DROP SEQUENCE bill_id_seq;
CREATE SEQUENCE bill_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Trigger to generate patient ID
CREATE OR REPLACE TRIGGER patient_id_trigger
BEFORE INSERT ON patients
FOR EACH ROW
BEGIN
    SELECT 'PT-' || LPAD(patient_id_seq.NEXTVAL, 3, '0') INTO :new.id FROM dual;
END;
/

-- Trigger to generate doctor ID
CREATE OR REPLACE TRIGGER doctor_id_trigger
BEFORE INSERT ON doctors
FOR EACH ROW
BEGIN
    SELECT 'DOC-' || LPAD(doctor_id_seq.NEXTVAL, 3, '0') INTO :new.id FROM dual;
END;
/

-- Trigger to generate appointment ID
CREATE OR REPLACE TRIGGER appointment_id_trigger
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    SELECT 'APP-' || LPAD(appointment_id_seq.NEXTVAL, 3, '0') INTO :new.id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER prescription_id_trigger
BEFORE INSERT ON prescriptions
FOR EACH ROW
BEGIN
    SELECT 'PRES-' || LPAD(prescription_id_seq.NEXTVAL, 3, '0') INTO :new.prescription_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER bill_id_trigger
BEFORE INSERT ON bills
FOR EACH ROW
BEGIN
    SELECT 'BILL-' || LPAD(bill_id_seq.NEXTVAL, 3, '0') INTO :new.id FROM dual;
END;
/



CREATE OR REPLACE PROCEDURE view_doctor_appointments(
    p_doctor_name IN VARCHAR2,
    p_appointments OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_appointments FOR
    SELECT a.id as appointment_id, p.id as patient_id, p.realname, a.appointment_day, a.appointment_date
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    WHERE a.doctor_name = p_doctor_name;
END;
/


--retreive the app id
CREATE OR REPLACE PROCEDURE insert_appointment(
    p_patient_id       IN  VARCHAR2,
    p_doctor_id        IN  VARCHAR2,
    p_patient_username IN  VARCHAR2,
    p_doctor_name      IN  VARCHAR2,
    p_doctor_specialty IN  VARCHAR2,
    p_appointment_day  IN  VARCHAR2,
    p_appointment_date IN  DATE,
    p_appointment_id   OUT VARCHAR2
) AS
BEGIN
    INSERT INTO appointments (
        patient_id, doctor_id, patient_username, doctor_name, doctor_specialty, appointment_day, appointment_date
    ) VALUES (
        p_patient_id, p_doctor_id, p_patient_username, p_doctor_name, p_doctor_specialty, p_appointment_day, p_appointment_date
    ) RETURNING id INTO p_appointment_id;
END;
/

--view prescription(doctor id)
CREATE OR REPLACE PROCEDURE prescriptions_by_doctor(
    p_doctor_id IN VARCHAR2,
    p_prescriptions_cursor OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_prescriptions_cursor FOR
    SELECT pres.prescription_id, pres.app_id, pres.patient_id, pres.patient_realname, 
           pres.age, pres.blood_group, pres.symptoms, pres.medicine
    FROM prescriptions pres
    JOIN appointments a ON pres.app_id = a.id
    JOIN patients p ON pres.patient_id = p.id
    WHERE a.doctor_id = p_doctor_id;
END;
/

--view prescription(patient side)
CREATE OR REPLACE PROCEDURE view_prescriptions (
    p_patient_id IN VARCHAR2,
    p_prescriptions_cursor OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_prescriptions_cursor FOR
    SELECT p.realname AS patient_realname, p.age, p.bloodgroup, pr.doctor_realname, pr.symptoms, pr.medicine
    FROM prescriptions pr
    JOIN patients p ON pr.patient_id = p.id
    WHERE pr.patient_id = p_patient_id;
END;
/


--cancel appointment
CREATE OR REPLACE PROCEDURE cancel_appointment (
    p_appointment_id IN VARCHAR2,
    p_result OUT NUMBER
)
IS
BEGIN
    DELETE FROM appointments WHERE id = p_appointment_id;
    p_result := SQL%ROWCOUNT;
END;
/

--view doctor's list
 CREATE OR REPLACE PROCEDURE show_doctors_list (
        doctors_cursor OUT SYS_REFCURSOR
    )
    IS
    BEGIN
        OPEN doctors_cursor FOR
        SELECT id, realname, specialty, phone_number
    FROM doctors;
    END;
   /


--view doctors appointments
CREATE OR REPLACE PROCEDURE view_doctor_appointments (
    doctor_username IN VARCHAR2,
    appointments_cursor OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN appointments_cursor FOR
    SELECT a.id AS appointment_id, p.id AS patient_id, p.realname AS patient_name, a.appointment_day, a.appointment_date
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    WHERE a.doctor_name = doctor_username;
END;
/



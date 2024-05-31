import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private int patientId;
    private int doctorId;

    public Appointment(int id, LocalDateTime dateTime, int patientId, int doctorId) {
        this.id = id;
        this.dateTime = dateTime;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}

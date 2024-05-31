import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AppointmentDAO {
    private final String INSERT_APPOINTMENT_SQL = "INSERT INTO appointments (id, date_time, patient_id, doctor_id) VALUES (?, ?, ?, ?)";

    public boolean saveAppointment(Appointment appointment) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_APPOINTMENT_SQL)) {
            statement.setInt(1, appointment.getId());
            statement.setObject(2, appointment.getDateTime());
            statement.setInt(3, appointment.getPatientId());
            statement.setInt(4, appointment.getDoctorId());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

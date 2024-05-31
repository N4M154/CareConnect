import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecords implements MedicalRecordDAO {
    private final String INSERT_MEDICAL_RECORD_SQL = "INSERT INTO medical_records (id, patient_id, diagnosis, prescription) VALUES (?, ?, ?, ?)";
    private final String SELECT_MEDICAL_RECORD_SQL = "SELECT * FROM medical_records WHERE id = ?";
    private final String SELECT_MEDICAL_RECORDS_FOR_PATIENT_SQL = "SELECT * FROM medical_records WHERE patient_id = ?";
    private final String UPDATE_MEDICAL_RECORD_SQL = "UPDATE medical_records SET diagnosis = ?, prescription = ? WHERE id = ?";
    private final String DELETE_MEDICAL_RECORD_SQL = "DELETE FROM medical_records WHERE id = ?";

    @Override
    public boolean createMedicalRecord(MedicalRecord medicalRecord) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_MEDICAL_RECORD_SQL)) {
            statement.setInt(1, medicalRecord.getId());
            statement.setInt(2, medicalRecord.getPatientId());
            statement.setString(3, medicalRecord.getDiagnosis());
            statement.setString(4, medicalRecord.getPrescription());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MedicalRecord getMedicalRecord(int medicalRecordId) {
        MedicalRecord medicalRecord = null;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_MEDICAL_RECORD_SQL)) {
            statement.setInt(1, medicalRecordId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    medicalRecord = new MedicalRecord(
                            resultSet.getInt("id"),
                            resultSet.getInt("patient_id"),
                            resultSet.getString("diagnosis"),
                            resultSet.getString("prescription")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicalRecord;
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsForPatient(int patientId) {
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_MEDICAL_RECORDS_FOR_PATIENT_SQL)) {
            statement.setInt(1, patientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    MedicalRecord medicalRecord = new MedicalRecord(
                            resultSet.getInt("id"),
                            resultSet.getInt("patient_id"),
                            resultSet.getString("diagnosis"),
                            resultSet.getString("prescription")
                    );
                    medicalRecords.add(medicalRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicalRecords;
    }

    @Override
    public boolean updateMedicalRecord(MedicalRecord medicalRecord) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_MEDICAL_RECORD_SQL)) {
            statement.setString(1, medicalRecord.getDiagnosis());
            statement.setString(2, medicalRecord.getPrescription());
            statement.setInt(3, medicalRecord.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMedicalRecord(int medicalRecordId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_MEDICAL_RECORD_SQL)) {
            statement.setInt(1, medicalRecordId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

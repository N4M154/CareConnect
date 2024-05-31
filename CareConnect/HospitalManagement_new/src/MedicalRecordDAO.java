import java.util.List;

public interface MedicalRecordDAO {
    boolean createMedicalRecord(MedicalRecord medicalRecord);
    MedicalRecord getMedicalRecord(int medicalRecordId);
    List<MedicalRecord> getMedicalRecordsForPatient(int patientId);
    boolean updateMedicalRecord(MedicalRecord medicalRecord);
    boolean deleteMedicalRecord(int medicalRecordId);

}

import java.util.List;

public interface BillDAO {
    boolean generateBill(Bill bill);
    boolean processInsuranceClaim(Bill bill);
    List<Bill> viewBillsForPatient(int patientId);
    boolean updateBill(Bill bill);
    boolean deleteBill(int billId);

}

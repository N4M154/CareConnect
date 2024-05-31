import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Bills implements BillDAO {
    private final String INSERT_BILL_SQL = "INSERT INTO bills (id, patient_id, billing_date, amount) VALUES (?, ?, ?, ?)";
    private final String UPDATE_BILL_SQL = "UPDATE bills SET patient_id = ?, billing_date = ?, amount = ? WHERE id = ?";
    private final String DELETE_BILL_SQL = "DELETE FROM bills WHERE id = ?";
    private final String SELECT_BILLS_FOR_PATIENT_SQL = "SELECT * FROM bills WHERE patient_id = ?";

    @Override
    public boolean generateBill(Bill bill) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_BILL_SQL)) {
            statement.setInt(1, bill.getId());
            statement.setInt(2, bill.getPatientId());
            statement.setDate(3, Date.valueOf(bill.getBillingDate()));
            statement.setDouble(4, bill.getAmount());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean processInsuranceClaim(Bill bill) {
        // Implementation to process insurance claim for a bill
        return false;
    }

    @Override
    public List<Bill> viewBillsForPatient(int patientId) {
        List<Bill> bills = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BILLS_FOR_PATIENT_SQL)) {
            statement.setInt(1, patientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bill bill = new Bill(
                            resultSet.getInt("id"),
                            resultSet.getInt("patient_id"),
                            resultSet.getDate("billing_date").toLocalDate(),
                            resultSet.getDouble("amount")
                    );
                    bills.add(bill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    @Override
    public boolean updateBill(Bill bill) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BILL_SQL)) {
            statement.setInt(1, bill.getPatientId());
            statement.setDate(2, Date.valueOf(bill.getBillingDate()));
            statement.setDouble(3, bill.getAmount());
            statement.setInt(4, bill.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteBill(int billId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BILL_SQL)) {
            statement.setInt(1, billId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

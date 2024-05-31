import java.time.LocalDate;

public class Bill {
    private int id;
    private int patientId;
    private LocalDate billingDate;
    private double amount;

    public Bill(int id, int patientId, LocalDate billingDate, double amount) {
        this.id = id;
        this.patientId = patientId;
        this.billingDate = billingDate;
        this.amount = amount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
